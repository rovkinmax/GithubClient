package com.github.rovkinmax.githubclient.model;

import android.accounts.Account;
import android.annotation.SuppressLint;
import android.os.Parcel;

import com.github.rovkinmax.githubclient.BuildConfig;

/**
 * @author Rovkin Max
 */
@SuppressLint("ParcelCreator")
public class GitAccount extends Account {
    public static final String REFRESH_TOKEN_KEY = "refresh_token_key";
    public static final String TOKEN_TYPE_KEY = "token_type_key";

    public GitAccount(String name) {
        super(name, BuildConfig.ACCOUNT_TYPE);
    }

    public GitAccount(Parcel in) {
        super(in);
    }
}
