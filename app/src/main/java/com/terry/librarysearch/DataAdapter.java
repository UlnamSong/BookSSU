package com.terry.librarysearch;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.terry.librarysearch.CustomView.CustomFontTextView;

import java.util.List;

public class DataAdapter extends RecyclerView.Adapter {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<ResultItem> resultItemList;
    private int visibleThreshold = 4;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;
    public Activity activity;

    public DataAdapter(List<ResultItem> students, RecyclerView recyclerView, Activity activity) {
        resultItemList = students;
        this.activity = activity;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView,
                                       int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager
                            .findLastVisibleItemPosition();
                    if (!loading
                            && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        // End has been reached
                        // Do something
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return resultItemList.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_row, parent, false);

            vh = new ResultItemViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.progressbar_item, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ResultItemViewHolder) {
            ResultItem singleResultItem = (ResultItem) resultItemList.get(position);
            ((ResultItemViewHolder) holder).tvName.setText(singleResultItem.getBookTitle());
            ((ResultItemViewHolder) holder).tvAuthor.setText(singleResultItem.getBookAuthor());
            Glide.with(activity).load(singleResultItem.getImageUrl()).into(((ResultItemViewHolder) holder).ivBookImage);
            if (singleResultItem.getBookStatus().equals("이용가능")) {
                ((ResultItemViewHolder) holder).ivStatus.setImageResource(R.drawable.icon_1);
            } else if(singleResultItem.getBookStatus().equals("")) {
                ((ResultItemViewHolder) holder).ivStatus.setVisibility(View.INVISIBLE);
            } else {
                ((ResultItemViewHolder) holder).ivStatus.setImageResource(R.drawable.icon_2);
            }
            ((ResultItemViewHolder) holder).resultItem = singleResultItem;
        } else {
            ((ProgressViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void setLoaded() {
        loading = false;

    }

    @Override
    public int getItemCount() {
        return resultItemList.size();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public class ResultItemViewHolder extends RecyclerView.ViewHolder {
        public CustomFontTextView tvName;
        public CustomFontTextView tvAuthor;
        public ImageView ivBookImage;
        public ImageView ivStatus;
        public ResultItem resultItem;

        public ResultItemViewHolder(View v) {
            super(v);
            tvName = (CustomFontTextView) v.findViewById(R.id.tvTimeInfo);
            tvAuthor = (CustomFontTextView) v.findViewById(R.id.tvReservedPerson);
            ivBookImage = (ImageView) v.findViewById(R.id.ivBook);
            ivStatus = (ImageView) v.findViewById(R.id.ivStatus);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(activity, BookDetailActivity.class);
                    intent.putExtra("bookLinkUrl", resultItem.getLinkUrl());
                    intent.putExtra("bookImageUrl", resultItem.getImageUrl());
                    intent.putExtra("bookTitle", resultItem.getBookTitle());
                    activity.startActivity(intent);
                }
            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        }
    }
}
