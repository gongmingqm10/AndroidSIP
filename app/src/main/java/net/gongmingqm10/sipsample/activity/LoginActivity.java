package net.gongmingqm10.sipsample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.gongmingqm10.sipsample.R;
import net.gongmingqm10.sipsample.SipApplication;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends BaseActivity {

    @InjectView(R.id.userName)
    EditText userNameEdit;
    @InjectView(R.id.password)
    EditText passwordEdit;
    @InjectView(R.id.domain)
    EditText domainEdit;
    @InjectView(R.id.loginBtn)
    Button loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        ButterKnife.inject(this);
        if (SipApplication.getInstance().getAccount() != null) {
            startMainActivity();
        }
        init();
    }

    private void init() {
        setTitle("Login My Account");
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = userNameEdit.getText().toString().trim();
                String password = passwordEdit.getText().toString().trim();
                String domain = domainEdit.getText().toString().trim();
                if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(domain)) {
                    showToast("Please fill all the fields");
                    return;
                }
                SipApplication.getInstance().saveAccount(userName, password, domain);
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
