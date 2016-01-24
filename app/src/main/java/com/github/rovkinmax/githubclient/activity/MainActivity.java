package com.github.rovkinmax.githubclient.activity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.github.rovkinmax.githubclient.R;
import com.github.rovkinmax.githubclient.loaders.RepoLoader;
import com.github.rovkinmax.githubclient.model.Repo;
import com.github.rovkinmax.githubclient.widget.RepoListAdapter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;

public class MainActivity extends BaseAuthActivity implements LoaderManager.LoaderCallbacks<List<Repo>>, RealmChangeListener {

    private Realm mRealm;
    private final RepoListAdapter mAdapter = new RepoListAdapter();
    private SwipeRefreshLayout mRefreshLayout;

    static void start(Context context) {
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
        showProgress(true);
        getLoaderManager().initLoader(R.id.repo_list_loader, Bundle.EMPTY, this).forceLoad();
    }

    private void initTollBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initRecyclerView() {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setRepoList(mRealm.allObjects(Repo.class));
    }

    private void loadRepoList() {
        getLoaderManager().restartLoader(R.id.repo_list_loader, Bundle.EMPTY, this);
    }

    private void showProgress(boolean refreshing) {
        mRefreshLayout.setRefreshing(refreshing);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mRealm.addChangeListener(this);
        setupPTR();
    }

    private void setupPTR() {
        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadRepoList();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mRealm.removeChangeListener(this);
        mRefreshLayout.setOnRefreshListener(null);
    }

    @Override
    protected void onDestroy() {
        if (mRealm != null && !mRealm.isClosed()) {
            mRealm.close();
        }
        super.onDestroy();
    }

    @Override
    public Loader<List<Repo>> onCreateLoader(int id, Bundle args) {
        if (id == R.id.repo_list_loader) {
            return new RepoLoader(getApplicationContext());
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<List<Repo>> loader, List<Repo> data) {
        showProgress(false);
    }

    @Override
    public void onLoaderReset(Loader<List<Repo>> loader) {

    }

    @Override
    public void onChange() {
        mAdapter.notifyDataSetChanged();
    }
}
