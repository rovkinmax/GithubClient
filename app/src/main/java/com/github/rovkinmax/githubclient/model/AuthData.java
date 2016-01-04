package com.github.rovkinmax.githubclient.model;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.support.annotation.Nullable;

import com.github.rovkinmax.githubclient.BuildConfig;
import com.github.rovkinmax.githubclient.util.AuthUtil;

/**
 * @author Rovkin Max
 */
public class AuthData {
    private static AuthData sInstance;
    private String mAuthToken;

    @Nullable
    public static AuthData getInstance() {
        return sInstance;
    }

    public static void setup(Context context) {
        final Account account = AuthUtil.getUserAccount(context);
        if (account != null) {
            final AccountManager am = AccountManager.get(context);
            String authToken = am.peekAuthToken(account, BuildConfig.ACCOUNT_TYPE);
            sInstance = new AuthData(authToken);
        }
    }

    public AuthData(String authToken) {
        mAuthToken = authToken;
    }

    public String getAuthToken() {
        return mAuthToken;
    }
}
