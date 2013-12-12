package com.shwy.bestjoy.utils;

import android.database.Cursor;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;

public class AdapterWrapper<T> {
	private static final String TAG = "AdapterWrapper";
	
	private T mAdapter;
	
	public AdapterWrapper(T adpapter) {
		mAdapter = adpapter;
	}
	
	public T getAdapter() {
		return mAdapter;
	}

	public void changeCursor(Cursor cursor) {
		if (mAdapter == null) {
			DebugUtils.logE(TAG, "changeCursor failed, mAdapter is null");
			return; 
		}
		if (mAdapter instanceof CursorAdapter) {
			((CursorAdapter) mAdapter).changeCursor(cursor);
		}
	}
	
	public void notifyDataSetChanged() {
		if (mAdapter instanceof CursorAdapter) {
			((CursorAdapter) mAdapter).notifyDataSetChanged();
		} else if (mAdapter instanceof BaseAdapter) {
			((BaseAdapter) mAdapter).notifyDataSetChanged();
		}
	}
	
	public void releaseAdapter() {
		if (mAdapter == null) return; 
		if (mAdapter instanceof CursorAdapter) {
			((CursorAdapter) mAdapter).changeCursor(null);
		}
		
		mAdapter = null;
	}
	
	public int getCount() {
		if (mAdapter instanceof CursorAdapter) {
			return ((CursorAdapter) mAdapter).getCount();
		} else if (mAdapter instanceof BaseAdapter) {
			((BaseAdapter) mAdapter).getCount();
		}
		return 0;
	}

}
