package com.github.rovkinmax.githubclient.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

import com.github.rovkinmax.githubclient.api.MainApi;
import com.github.rovkinmax.githubclient.model.Repo;
import com.github.rovkinmax.githubclient.util.RealmUtil;

import java.util.List;

import io.realm.Realm;

/**
 * @author Rovkin Max
 */
public class RepoLoader extends AsyncTaskLoader<List<Repo>> {
    public RepoLoader(Context context) {
        super(context);
    }

    @Override
    public List<Repo> loadInBackground() {
        final List<Repo> repoList = MainApi.repoList();
        RealmUtil.safeTransaction(getContext(), new RealmUtil.TransactionListener() {
            @Override
            public void makeTransaction(Realm realm) {
                realm.copyToRealmOrUpdate(repoList);
            }
        });
        return repoList;
    }
}
