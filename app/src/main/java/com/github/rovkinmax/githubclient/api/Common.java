package com.github.rovkinmax.githubclient.api;

import android.support.annotation.NonNull;

import com.github.rovkinmax.githubclient.BuildConfig;
import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * @author Rovkin Max
 */
public final class Common {
    private static final String LOG_TAG = "!GH_Client";
    private static final int TIMEOUT = 60;
    private static final int WRITE_TIMEOUT = 120;
    private static final int CONNECT_TIMEOUT = 10;
    private static final OkHttpClient CLIENT = new OkHttpClient();

    static {
        CLIENT.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        CLIENT.setWriteTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS);
        CLIENT.setReadTimeout(TIMEOUT, TimeUnit.SECONDS);
    }

    private Common() {
    }

    static RestAdapter.Builder getRestAdapter() {
        // TODO: set log Level
        return new RestAdapter.Builder()
                .setLog(new AndroidLog(LOG_TAG))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(new Gson()))
                .setClient(new OkClient(CLIENT))
                .setRequestInterceptor(buildRequestInterceptor())
                .setEndpoint(BuildConfig.SERVER_URL);
    }


    @NonNull
    private static RequestInterceptor buildRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                /*AuthData authData = AuthData.getInstance();
                if (authData != null) {
                    request.addHeader(
                            AUTHORIZATION_HEADER_KEY,
                            String.format(AUTHORIZATION_HEADER_VALUE_FORMAT, authData.getTokenType(), authData.getAuthToken())
                    );
                }*/
            }
        };
    }
}
