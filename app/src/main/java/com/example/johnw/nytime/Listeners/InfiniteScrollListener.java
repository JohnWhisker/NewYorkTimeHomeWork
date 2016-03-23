package com.example.johnw.nytime.Listeners;

import android.widget.AbsListView;

/**
 * Created by JohnWhisker on 3/21/16.
 */
public abstract class InfiniteScrollListener implements AbsListView.OnScrollListener {

    // VARIABLES DEFINE
    private int bufferItemCount = 10;
    private int currentPage = 0;
    private int itemCount = 0;
    private boolean isLoading = true;

    //METHODS DEFINE
    public InfiniteScrollListener(int bufferItemCount){
        this.bufferItemCount = bufferItemCount;
    }

    public abstract void loadMore (int page,int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,int totalItemCount){
        if(totalItemCount <itemCount ){
            this.itemCount = totalItemCount;
            if(totalItemCount ==0){
                this.isLoading = true;
            }

        }
        if(isLoading&&(totalItemCount>itemCount)){
            isLoading= false;
            itemCount = totalItemCount;
            currentPage++;
        }
        if(!isLoading&& (totalItemCount-visibleItemCount)<=(firstVisibleItem+bufferItemCount)){
            loadMore(currentPage+1,totalItemCount);
            isLoading=true;
        }
    }

}
