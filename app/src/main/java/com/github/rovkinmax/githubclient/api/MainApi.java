package com.github.rovkinmax.githubclient.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.github.rovkinmax.githubclient.app.App;
import com.github.rovkinmax.githubclient.model.Repo;
import com.github.rovkinmax.githubclient.util.AuthUtil;
import com.github.rovkinmax.githubclient.util.RealmUtil;

import java.util.List;

import io.realm.Realm;
import retrofit.RequestInterceptor;
import retrofit.http.GET;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author Rovkin Max
 */
public class MainApi {
    private static final String AUTH_HEADER_KEY = "Authorization";
    private static final String AUTH_HEADER_VALUE_FORMAT = "%s %s";
    private static final String AUTH_TOKEN_TYPE = "Token";
    private static RepoApi sRepoApi = Common.getRestAdapter()
            .setRequestInterceptor(buildRequestInterceptor())
            .build()
            .create(RepoApi.class);


    @NonNull
    private static RequestInterceptor buildRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                String authToken = AuthUtil.getToken(App.getInstance());
                if (!TextUtils.isEmpty(authToken)) {
                    String headerValue = String.format(AUTH_HEADER_VALUE_FORMAT, AUTH_TOKEN_TYPE, authToken);
                    request.addHeader(AUTH_HEADER_KEY, headerValue);
                }
            }
        };
    }

    public static Observable<List<Repo>> repoList(final Context context) {
        return sRepoApi.repoList().flatMap(buildSaveRepoListFunc(context));
    }

    @NonNull
    private static Func1<List<Repo>, Observable<List<Repo>>> buildSaveRepoListFunc(final Context context) {
        return new Func1<List<Repo>, Observable<List<Repo>>>() {
            @Override
            public Observable<List<Repo>> call(final List<Repo> repos) {
                RealmUtil.safeTransaction(context, new RealmUtil.TransactionListener() {
                    @Override
                    public void makeTransaction(Realm realm) {
                        realm.copyToRealmOrUpdate(repos);
                    }
                });
                return Observable.just(repos);
            }
        };
    }

    private interface RepoApi {
        @GET("/user/repos")
        Observable<List<Repo>> repoList();
    }
}
