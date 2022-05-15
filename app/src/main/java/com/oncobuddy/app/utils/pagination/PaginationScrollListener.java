package com.oncobuddy.app.utils.pagination;


import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Pagination
 * Created by Suleiman19 on 10/15/16.ssssss
 * Copyright (c) 2016. Suleiman Ali Shakir. All rights reserved.
 */
public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager layoutManager;

    /**
     * Supporting only LinearLayoutManager for now.
     *
     * @param layoutManager
     */
    protected PaginationScrollListener(LinearLayoutManager layoutManager) {
        Log.d("page_load_tag_listener", "Initialized");
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        Log.d("page_load_tag_listener", "scrolled "+dy);
        if (!isLoading() && !isLastPage()) {
            Log.d("page_load_tag_listener", "visible item count "+visibleItemCount);
            Log.d("page_load_tag_listener", "first visible item position "+firstVisibleItemPosition);
            Log.d("page_load_tag_listener", "total Item count "+totalItemCount);
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0) {
                loadMoreItems();
                Log.d("page_load_tag_listener", "Loading more items");
            }
        }

    }

    protected abstract void loadMoreItems();

    public abstract int getTotalPageCount();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();

}
