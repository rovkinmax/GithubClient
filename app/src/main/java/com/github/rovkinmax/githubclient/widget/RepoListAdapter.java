package com.github.rovkinmax.githubclient.widget;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.rovkinmax.githubclient.R;
import com.github.rovkinmax.githubclient.model.Repo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rovkin Max
 */
public class RepoListAdapter extends RecyclerView.Adapter<RepoListAdapter.RepoHolder> {
    private LayoutInflater mInflater;
    private List<Repo> mRepoList = new ArrayList<>();


    @Override
    public RepoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        initInflaterIfNull(parent.getContext());
        return new RepoHolder(mInflater.inflate(R.layout.repo_layout, parent, false));
    }

    private void initInflaterIfNull(Context context) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public void onBindViewHolder(RepoHolder holder, int position) {
        holder.bind(mRepoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mRepoList.size();
    }

    public void setRepoList(List<Repo> repoList) {
        mRepoList = repoList;
    }

    static class RepoHolder extends RecyclerView.ViewHolder {
        private TextView mNameView;
        private TextView mDescriptionView;
        private ImageView mIconView;
        private TextView mLangView;

        public RepoHolder(View itemView) {
            super(itemView);
            mNameView = (TextView) itemView.findViewById(R.id.tv_name);
            mDescriptionView = (TextView) itemView.findViewById(R.id.tv_desc);
            mIconView = (ImageView) itemView.findViewById(R.id.iv_icon);
            mLangView = (TextView) itemView.findViewById(R.id.tv_language);
        }

        public void bind(Repo repo) {
            mNameView.setText(repo.getName());
            mDescriptionView.setText(repo.getDescription());
            mLangView.setText(repo.getLanguage());
            Glide.with(itemView.getContext())
                    .load(repo.getOwner().getAvatar())
                    .transform(new CircleTransform(itemView.getContext()))
                    .into(mIconView);
        }
    }
}
