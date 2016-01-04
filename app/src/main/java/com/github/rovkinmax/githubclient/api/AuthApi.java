package com.github.rovkinmax.githubclient.api;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.github.rovkinmax.githubclient.model.AuthData;
import com.github.rovkinmax.githubclient.model.GitAccount;
import com.github.rovkinmax.githubclient.model.api.AuthBody;
import com.github.rovkinmax.githubclient.model.api.Authorization;
import com.github.rovkinmax.githubclient.util.PrefUtil;

import java.util.UUID;

import retrofit.http.Body;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.PUT;
import retrofit.http.Path;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Rovkin Max
 */
public class AuthApi {
    private static final String CLIENT_ID = "2efdc771362ea85e13ba";
    private static final String CLIENT_SECRET = "3ec59bbe8de2bb13906a5f9b7a63d249aadae441";
    private static final String AUTH_TYPE_BASIC = "Basic";
    private static AuthService sAuthService = Common.getRestAdapter().build().create(AuthService.class);

    public static Observable<Authorization> auth(Context context, String login, String pass) {
        final String credentials = Base64.encodeToString((login + ":" + pass).getBytes(), Base64.NO_WRAP);
        final String authHeader = String.format("%s %s", AUTH_TYPE_BASIC, credentials);
        final String fingerPrint = UUID.randomUUID().toString();
        final AuthBody body = new AuthBody();
        body.setClientSecret(CLIENT_SECRET);
        body.addScope("repo");
        return sAuthService.auth(CLIENT_ID, fingerPrint, authHeader, body)
                .flatMap(buildSaveTokenFunc(context, login, pass));
    }

    @NonNull
    private static Func1<Authorization, Observable<Authorization>> buildSaveTokenFunc(final Context context, final String login, final String pass) {
        return new Func1<Authorization, Observable<Authorization>>() {
            @Override
            public Observable<Authorization> call(Authorization authorization) {
                onTokenReceived(context, login, pass, authorization);
                return Observable.just(authorization);
            }
        };
    }

    private static void onTokenReceived(Context context, String login, String pass, Authorization authorization) {
        Account account = new GitAccount(login);
        AccountManager am = AccountManager.get(context);
        am.addAccountExplicitly(account, pass, new Bundle());
        am.setAuthToken(account, account.type, authorization.getToken());
        PrefUtil.setUserName(login);
        AuthData.setup(context);
    }


    private interface AuthService {
        @PUT("/authorizations/clients/{client_id}/{fingerprint}")
        @Headers("Content-type: application/json")
        Observable<Authorization> auth(@Path("client_id") String clientId,
                                       @Path("fingerprint") String fingerprint,
                                       @Header("Authorization") String credentials,
                                       @Body AuthBody authBody);
    }
}
