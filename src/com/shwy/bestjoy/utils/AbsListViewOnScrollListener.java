package com.shwy.bestjoy.utils;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;


public class AbsListViewOnScrollListener implements OnScrollListener {

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
	
	/**当触发了刷新动作的时候给子类的回调，实现此方法来刷新数据*/
	public void onRefresh() {
		
	}

}
