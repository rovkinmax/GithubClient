package com.github.rovkinmax.githubclient.activity;

import android.accounts.Account;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.github.rovkinmax.githubclient.util.AuthUtil;
import com.github.rovkinmax.githubclient.util.PrefUtil;


/**
 * @author Rovkin Max
 */
public abstract class BaseAuthActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean authNeeded = isAuthNeeded();
        if (authNeeded) {
            LoginActivity.start(this);
            finish();
        }
    }

    private boolean isAuthNeeded() {
        boolean authNeeded = false;
        final Account userAccount = AuthUtil.getUserAccount(this);
        if (userAccount == null) {
            authNeeded = true;
        }
        return authNeeded || TextUtils.isEmpty(PrefUtil.getUserName());
    }
}
