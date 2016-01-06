package com.github.rovkinmax.githubclient.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.github.rovkinmax.githubclient.R;
import com.github.rovkinmax.githubclient.api.MainApi;
import com.github.rovkinmax.githubclient.model.Repo;
import com.github.rovkinmax.githubclient.rx.RxLoader;
import com.github.rovkinmax.githubclient.widget.RepoListAdapter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Subscription;
import rx.functions.Action1;

public class MainActivity extends BaseAuthActivity {

    private Realm mRealm;
    private final RepoListAdapter mAdapter = new RepoListAdapter();
    private Subscription mSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(this);
        setContentView(R.layout.ac_main);
        initTollBar();
        initRecyclerView();
        loadRepoList();
    }

    private void loadRepoList() {
        MainApi.repoList(this.getApplicationContext())
                .compose(RxLoader.<List<Repo>>buildLoaderTransformer(this, getLoaderManager(), R.id.repo_list_loader))
                .subscribe();
    }

    private void initTollBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribeForRepoListChanges();
    }

    private void subscribeForRepoListChanges() {
        mSubscription = mRealm.where(Repo.class)
                .findAllAsync()
                .asObservable()
                .subscribe(new Action1<RealmResults<Repo>>() {
                    @Override
                    public void call(RealmResults<Repo> repos) {
                        mAdapter.setRepoList(repos);
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onPause() {
        unsubscribeFromReposChanges();
        super.onPause();
    }

    private void unsubscribeFromReposChanges() {
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
        }
    }

    @Override
    protected void onDestroy() {
        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
        super.onDestroy();
    }
}
