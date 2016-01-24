package com.github.rovkinmax.githubclient.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.github.rovkinmax.githubclient.R;
import com.github.rovkinmax.githubclient.api.MainApi;
import com.github.rovkinmax.githubclient.model.Repo;
import com.github.rovkinmax.githubclient.widget.RepoListAdapter;
import com.github.rovkinmax.rxretain.EmptySubscriber;
import com.github.rovkinmax.rxretain.RetainFactory;
import com.github.rovkinmax.rxretain.RetainWrapper;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseAuthActivity {

    private Realm mRealm;
    private final RepoListAdapter mAdapter = new RepoListAdapter();
    private Subscription mSubscription;
    private SwipeRefreshLayout mRefreshLayout;
    private RetainWrapper<List<Repo>> mRetainWrapper;

    public static void start(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(this);
        setContentView(R.layout.ac_main);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshView);
        initTollBar();
        initRecyclerView();
        setupPTR();
        initRetainWrapper();
        loadRepoList();
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


    private void setupPTR() {
        RxSwipeRefreshLayout.refreshes(mRefreshLayout)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        loadRepoList();
                    }
                });
    }

    private void initRetainWrapper() {
        mRetainWrapper = RetainFactory.create(getFragmentManager(), buildRepoListObservable(), new EmptySubscriber<List<Repo>>() {
            @Override
            public void onStart() {
                showProgress(true);
            }
        });
    }

    private void loadRepoList() {
        mRetainWrapper.subscribe(new EmptySubscriber<List<Repo>>() {
            @Override
            public void onNext(List<Repo> repos) {
                mRetainWrapper.unsubscribe();
            }
        });
    }

    private void showProgress(boolean refreshing) {
        mRefreshLayout.setRefreshing(refreshing);
    }

    private Observable<List<Repo>> buildRepoListObservable() {
        return MainApi.repoList(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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
