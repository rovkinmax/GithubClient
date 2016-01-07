package com.github.rovkinmax.githubclient.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.github.rovkinmax.githubclient.R;
import com.github.rovkinmax.githubclient.api.MainApi;
import com.github.rovkinmax.githubclient.model.Repo;
import com.github.rovkinmax.githubclient.rx.RxLoader;
import com.github.rovkinmax.githubclient.widget.RepoListAdapter;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends BaseAuthActivity {

    private Realm mRealm;
    private final RepoListAdapter mAdapter = new RepoListAdapter();
    private Subscription mSubscription;
    private SwipeRefreshLayout mRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(this);
        setContentView(R.layout.ac_main);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshView);
        initTollBar();
        initRecyclerView();
        setupPTR();
        loadRepoList();
    }

    private void loadRepoList() {
        buildRepoListObservable()
                .doOnSubscribe(doOnSubscribe())
                .subscribe(repoLoadedListener(), repoLoadError());
    }

    @NonNull
    private Action0 doOnSubscribe() {
        return new Action0() {
            @Override
            public void call() {
                mRefreshLayout.setRefreshing(true);
            }
        };
    }

    private void setupPTR() {
        RxSwipeRefreshLayout.refreshes(mRefreshLayout)
                .flatMap(mapPtrObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(repoLoadedListener(), repoLoadError());
    }

    @NonNull
    private Func1<Void, Observable<List<Repo>>> mapPtrObservable() {
        return new Func1<Void, Observable<List<Repo>>>() {
            @Override
            public Observable<List<Repo>> call(Void aVoid) {
                return buildRepoListObservable();
            }
        };
    }

    @NonNull
    private Action1<List<Repo>> repoLoadedListener() {
        return new Action1<List<Repo>>() {
            @Override
            public void call(List<Repo> repos) {
                mRefreshLayout.setRefreshing(false);
            }
        };
    }

    @NonNull
    private Action1<Throwable> repoLoadError() {
        return new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                mRefreshLayout.setRefreshing(false);
            }
        };
    }

    private Observable<List<Repo>> buildRepoListObservable() {
        return MainApi.repoList(this.getApplicationContext())
                .compose(RxLoader.<List<Repo>>buildLoaderTransformer(this, getLoaderManager(), R.id.repo_list_loader));
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
