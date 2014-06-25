package com.penguininc.foodatory.sqlite.loader;

import android.content.Context;
import android.database.Cursor;
import android.content.AsyncTaskLoader;

public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {
	
	private Cursor mCursor;
	
	protected abstract Cursor loadCursor();
	
	public SQLiteCursorLoader(Context context) {
		super(context);
	}
	
	@Override
	public Cursor loadInBackground() {
		Cursor cursor = loadCursor();
		if (cursor != null) {
			// Ensure that the content window is filled
			cursor.getCount();
		}
		return cursor;
	}
	
	@Override
	public void deliverResult(Cursor data) {
		Cursor oldCursor = mCursor;
		mCursor = data;
		if(isStarted()) {
			super.deliverResult(data);
		}
		
		if(oldCursor != null && oldCursor != data && !oldCursor.isClosed()) {
			oldCursor.close();
		}
	}
	
	@Override
	protected void onStartLoading() {
		if(mCursor != null) {
			deliverResult(mCursor);
		}
		if(takeContentChanged() || mCursor == null) {
			forceLoad();
		}
	}
	
	@Override
	protected void onStopLoading() {
		cancelLoad();
	}
	
	@Override
	public void onCanceled(Cursor cursor) {
		if(cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}
	
	@Override
	protected void onReset() {
		super.onReset();
	}
}