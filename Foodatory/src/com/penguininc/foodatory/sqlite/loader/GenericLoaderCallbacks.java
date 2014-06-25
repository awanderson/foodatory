package com.penguininc.foodatory.sqlite.loader;

import android.content.Context;
import android.os.Bundle;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;

/**
 * 
 * 
 * @author Alec Anderson
 *
 * Super clever class that makes running SQLlite calls on another thread
 * much simpler.
 * @param <D> Input that you are sending to the method
 * @param <E> Output you want back
 * 
 */
public abstract class GenericLoaderCallbacks<D, E> implements LoaderCallbacks<E> {
	
	// The is what you want done in the background (typically a method call
	// from an sql helper). The input to that method must be D, and the output
	// must be E
	protected abstract E doInBackground(D data);
	
	// What you want done when that task is finished
	protected abstract void loadFinished(E output);
	
	//reset loader
	protected abstract void resetLoader(Loader<E> args);
	
	private D data;
	protected Context context;
	
	public GenericLoaderCallbacks(Context context, D data) {
		this.data = data;
		this.context = context;
	}
	
	@Override
	public Loader<E> onCreateLoader(int id, Bundle args) {
		return new GenericLoader<D, E>(context, data) {

			@Override
			protected E getBackgroundCalls(D data) {
				return doInBackground(data);
			}
			
		};
	}
	
	@Override
	public void onLoadFinished(Loader<E> args, E output) {
		loadFinished(output);
	}
	
	// Don't need this usually, can simply overwrite this if you need it
	// in implementation
	@Override
	public void onLoaderReset(Loader<E> args) {
		resetLoader(args);
	}
	
	private abstract class GenericLoader<F, G> extends DataLoader<G> {
		
		protected abstract G getBackgroundCalls(F data);
		
		private F data;
		
		public GenericLoader(Context context, F data) {
			super(context);
			this.data = data;
		}
		
		@Override
		public G loadInBackground() {
			return getBackgroundCalls(data);
		}
		
	}
	
}