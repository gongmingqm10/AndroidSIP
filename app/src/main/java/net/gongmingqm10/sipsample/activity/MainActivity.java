package net.gongmingqm10.sipsample.activity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.sip.SipAudioCall;
import android.net.sip.SipException;
import android.net.sip.SipManager;
import android.net.sip.SipProfile;
import android.net.sip.SipRegistrationListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.gongmingqm10.sipsample.R;
import net.gongmingqm10.sipsample.SipApplication;
import net.gongmingqm10.sipsample.data.Account;
import net.gongmingqm10.sipsample.receiver.IncomingCallReceiver;

import java.text.ParseException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "gongmingqm10";
    private static final String CALL_ACTION = "android.SIPSample.INCOMING_CALL";
    public static final String STATE_CONNECTED = "Connected";
    public static final String STATE_CONNECTED_FAILURE = "Register failed, try to refresh";
    public static final String STATE_CONNECTING = "Connecting...";
    public static final String STATE_CALLING = "Calling...";
    private SipAudioCall.Listener audioCallListener = new SipAudioCall.Listener() {
        @Override
        public void onCallEstablished(SipAudioCall call) {
            call.startAudio();
            call.setSpeakerMode(true);
            call.toggleMute();
        }

        @Override
        public void onCallEnded(SipAudioCall call) {
            super.onCallEnded(call);
            updateStatus(STATE_CONNECTED);
        }

        @Override
        public void onCalling(SipAudioCall call) {
            if (!call.isMuted())
                call.toggleMute();
            updateStatus(STATE_CALLING + " " + call.getPeerProfile().getUserName());
        }
    };
    public SipManager sipManager;
    public SipProfile sipProfile;
    public SipAudioCall call;
    public IncomingCallReceiver callReceiver;
    @InjectView(R.id.callBtn)
    Button callBtn;
    @InjectView(R.id.callNumber)
    EditText callNumber;
    @InjectView(R.id.callEnd)
    Button callEndBtn;

    private Account account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initSip();
        initComp();
    }

    private void initComp() {
        callBtn.setOnClickListener(this);
        callEndBtn.setOnClickListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(CALL_ACTION);
        callReceiver = new IncomingCallReceiver();
        this.registerReceiver(callReceiver, filter);
    }


    private void callPhone() {
        if (call != null && call.isInCall()) {
            showToast("You're currently busy...");
            return;
        }
        try {
            String callUsername = callNumber.getText().toString();
            if (TextUtils.isEmpty(callUsername)) return;
            SipProfile sipTarget = (new SipProfile.Builder(callUsername, account.getDomain())).build();
            call = sipManager.makeAudioCall(sipProfile.getUriString(), sipTarget.getUriString(), audioCallListener, 30);
        } catch (SipException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    private void endPhone() {
        try {
            if (call != null) {
                call.endCall();
                call.close();
                updateStatus(STATE_CONNECTED);
            }

        } catch (SipException e) {
            e.printStackTrace();
        }
    }

    private void initSip() {
        account = SipApplication.getInstance().getAccount();
        sipManager = SipManager.newInstance(this);
        if (sipManager == null) {
            showToast("SIP feature is not supported in your device");
            return;
        }

        try {
            SipProfile.Builder builder = new SipProfile.Builder(account.getUsername(), account.getDomain());
            builder.setPassword(account.getPassword());
            sipProfile = builder.build();
            connectSip();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
            showToast(e.getMessage());
        }
    }

    public void updateStatus(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setTitle(message);
                callBtn.setEnabled(STATE_CONNECTED.equals(message));
            }
        });
    }

    public void updateStatus(SipAudioCall call) {
        String useName = call.getPeerProfile().getDisplayName();
        if (useName == null) {
            useName = call.getPeerProfile().getUserName();
        }
        updateStatus(useName + "@" + call.getPeerProfile().getSipDomain());
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeLocalProfile();
        endPhone();
        if (callReceiver != null) this.unregisterReceiver(callReceiver);
    }

    private void connectSip() {
        try {
            Intent intent = new Intent();
            intent.setAction(CALL_ACTION);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, Intent.FILL_IN_DATA);
            sipManager.open(sipProfile, pendingIntent, null);
            sipManager.setRegistrationListener(sipProfile.getUriString(), new SipRegistrationListener() {
                @Override
                public void onRegistering(String localProfileUri) {
                    updateStatus(STATE_CONNECTING);
                }

                @Override
                public void onRegistrationDone(String localProfileUri, long expiryTime) {
                    updateStatus(STATE_CONNECTED);
                }

                @Override
                public void onRegistrationFailed(String localProfileUri, int errorCode, String errorMessage) {
                    updateStatus(STATE_CONNECTED_FAILURE);
                }
            });
        } catch (SipException e) {
            e.printStackTrace();
            showToast(e.getMessage());
            Log.e(TAG, e.getMessage());
        }
    }

    private void closeLocalProfile() {
        if (sipManager == null) return;
        if (sipProfile != null) {
            try {
                sipManager.close(sipProfile.getUriString());
            } catch (SipException e) {
                e.printStackTrace();
                Log.e(TAG, "Failed to close SipProfile: " + e.getMessage());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            connectSip();
            return true;
        } else if (id == R.id.action_logout) {
            SipApplication.getInstance().clearAccount();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_account) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.callBtn:
                callPhone();
                break;
            case R.id.callEnd:
                endPhone();
                break;
        }
    }
}
