package com.github.rovkinmax.githubclient.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.rovkinmax.githubclient.BuildConfig;
import com.github.rovkinmax.githubclient.R;

import java.io.IOException;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType(BuildConfig.ACCOUNT_TYPE);
        if (accounts.length > 0) {
            accountManager.getAuthToken(accounts[0], BuildConfig.ACCOUNT_TYPE, Bundle.EMPTY, false,
                    new AccountManagerCallback<Bundle>() {
                        @Override
                        public void run(AccountManagerFuture<Bundle> future) {
                            try {
                                Bundle result = future.getResult();
                                if (result.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                                    openMainActivity();
                                } else {
                                    openLoginActivity();
                                }
                            } catch (OperationCanceledException | IOException | AuthenticatorException e) {
                                openLoginActivity();
                            }
                        }
                    }, null);
        } else {
            openLoginActivity();
        }
    }

    private void openMainActivity() {
        MainActivity.start(this);
    }

    private void openLoginActivity() {
        LoginActivity.start(this);
    }
}
