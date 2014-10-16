package net.gongmingqm10.sipsample.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

import net.gongmingqm10.sipsample.R;
import net.gongmingqm10.sipsample.SipApplication;
import net.gongmingqm10.sipsample.data.Account;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ProfileActivity extends BaseActivity {

    @InjectView(R.id.userName)
    TextView userName;
    @InjectView(R.id.domain)
    TextView domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);
        Account account = SipApplication.getInstance().getAccount();
        userName.setText("Username：" + account.getUsername());
        domain.setText("Domain：" + account.getDomain());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
