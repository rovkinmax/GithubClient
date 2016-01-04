package com.github.rovkinmax.githubclient.util;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.text.TextUtils;

import com.github.rovkinmax.githubclient.BuildConfig;

/**
 * @author Rovkin Max
 */
public class AuthUtil {

    public static Account getUserAccount(Context context) {
        AccountManager am = AccountManager.get(context);
        Account[] accounts = am.getAccountsByType(BuildConfig.ACCOUNT_TYPE);
        String userName = PrefUtil.getUserName();
        Account userAccount = null;
        if (TextUtils.isEmpty(userName)) {
            return null;
        }

        for (Account account : accounts) {
            if (TextUtils.equals(account.name, userName)) {
                userAccount = account;
            }
        }
        return userAccount;
    }

    public static void logout(Context context) {
        Account account = AuthUtil.getUserAccount(context);
        if (account == null) {
            return;
        }
        AccountManager am = AccountManager.get(context);
        String token = am.peekAuthToken(account, account.type);
        am.invalidateAuthToken(account.type, token);
    }
}
