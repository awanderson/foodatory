package com.penguininc.foodatory;

import java.util.ArrayList;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mobeta.android.dslv.DragSortListView;
import com.penguininc.foodatory.adapter.DirectionListAdapter;
import com.penguininc.foodatory.dailog.NewDirectionDialog;
import com.penguininc.foodatory.sqlite.helper.DirectionHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Direction;
import com.penguininc.foodatory.sqlite.model.Recipe;
import com.penguininc.foodatory.templates.BasicFragment;

public class RecipeDirectionFragment extends BasicFragment {

	private long mRecipeId;
	private DragSortListView listview;
	private TextView emptyView;
	private LoaderCallbacks<ArrayList<Direction>> callbacks;
	private DirectionListAdapter adapter;
	private Fragment mThis;
	
	private static final int GET_DIRECTIONS = 1;
	private static final int NEW_DIRECTION = 2;
	private static final int NEW_LOADER_VALUE = 3;
	private static final int DELETE_LOADER_VALUE = 4;
	
	private int update_loader_value = 100;
	
	private DragSortListView.DropListener onDrop =
	    new DragSortListView.DropListener() {
	        @Override
	        public void drop(int from, int to) {
	        	ArrayList<Direction> directions = adapter.directions;
	            Direction item = directions.get(from);
	            directions.remove(item);
	            directions.add(to, item);
	            if(from > to) {
	            	for(int i = to; i <= from; i++) {
	            		Direction d = directions.get(i);
	            		d.setOrder(i + 1);
	            	}
	            } else {
	            	for(int i = from; i <= to; i++) {
	            		Direction d = directions.get(i);
	            		d.setOrder(i + 1);
	            	}
	            }
	            adapter.notifyDataSetChanged();
	        }
	    };

	private DragSortListView.RemoveListener onRemove = 
	    new DragSortListView.RemoveListener() {
	        @Override
	        public void remove(int which) {
	        	Direction d = adapter.getItem(which);
	            adapter.remove(d);
	            LoaderCallbacks<Void> removeCallbacks =
						(new GenericLoaderCallbacks<Direction, Void> (getActivity(), d){

							@Override
							protected Void doInBackground(Direction data) {
								(new DirectionHelper(context)).deleteDirection(data.getId());
								return null;
							}

							@Override
							protected void loadFinished(Void output) {
							}

							@Override
							protected void resetLoader(Loader<Void> args) {
							}
						});
	            LoaderManager l = getLoaderManager();
	            l.initLoader(update_loader_value, null, removeCallbacks);
	            update_loader_value++;
	            
	            //Update the count for every element after
	            ArrayList<Direction> directions = adapter.directions;
	            for(int i = which; i < directions.size(); i++) {
            		Direction direction = directions.get(i);
            		direction.setOrder(i + 1);
            	}
	            adapter.notifyDataSetChanged();
	        }
	    };
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Log.d("RecipeDirection", "in on create view");
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		Bundle b = getArguments();
		mRecipeId = b.getLong(Recipe.RECIPE_ID);
		
		listview = (DragSortListView)view.findViewById(R.id.listview);
		listview.setDropListener(onDrop);
		listview.setRemoveListener(onRemove);
		emptyView = (TextView)view.findViewById(R.id.empty_list);
		
		callbacks = (new GenericLoaderCallbacks<Long,
				ArrayList<Direction>>(getActivity(), mRecipeId) {

					@Override
					protected ArrayList<Direction> doInBackground(Long data) {
						return (new DirectionHelper(getActivity())).getDirections(data);
					}

					@Override
					protected void loadFinished(ArrayList<Direction> output) {
						Log.d("RecipeDirection", "restarting callbacks");
						if(adapter == null) {
							adapter = new DirectionListAdapter(getActivity(), output);
							listview.setEmptyView(emptyView);
							listview.setAdapter(adapter);
						}
						
					}

					@Override
					protected void resetLoader(Loader<ArrayList<Direction>> args) {
						listview.setAdapter(null);
					}
		
			
		});
		
		if(adapter == null) {
			Log.d("RecipeDirection", "adapter");
			getLoaderManager().initLoader(GET_DIRECTIONS, null, callbacks);
		} else {
			listview.setAdapter(adapter);
		}
		
		Button newDirection = (Button)view.findViewById(R.id.new_direction);
		
		mThis = this;
		newDirection.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DialogFragment frag = new NewDirectionDialog();
				frag.setTargetFragment(mThis, NEW_DIRECTION);
				frag.setArguments(getArguments());
				frag.show(getFragmentManager().beginTransaction(), "New Direction");
			}
		});
		return view;
		
	}
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_direction;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		
		case NEW_DIRECTION:
			if(resultCode == Activity.RESULT_OK) {
				Direction d = new Direction();
				d.setRecipeId(mRecipeId);
				d.setContent(data.getStringExtra(NewDirectionDialog.DIRECTION_CONTENT));
				d.setOrder(adapter.getCount() + 1);
				final Direction direction = d;
				
				Log.d("RecipeDirection", "inOn activity result");
				//save new direction
				LoaderCallbacks<Long> saveCallbacks = 
						(new GenericLoaderCallbacks<Direction, Long> (getActivity(), d){

							@Override
							protected Long doInBackground(Direction data) {
								return (new DirectionHelper(context)).createDirection(data);
							}

							@Override
							protected void loadFinished(Long output) {
								Log.d("RecipeDirection", "Adding direction");
								adapter.directions.add(direction);
								adapter.notifyDataSetChanged();
								getLoaderManager().destroyLoader(NEW_LOADER_VALUE);
							}

							@Override
							protected void resetLoader(Loader<Long> args) {
								
							}
						
						
						});
				
				LoaderManager l = getLoaderManager();
				if(l.getLoader(NEW_LOADER_VALUE) != null) {
					l.restartLoader(NEW_LOADER_VALUE, null, saveCallbacks);
				} else {
					l.initLoader(NEW_LOADER_VALUE, null, saveCallbacks);
				}
			}
		}
	}
	
	@Override
	public void onStop() {
		super.onStop();
		Log.d("RecipeDirection", "in on stop");
		LoaderManager l = getLoaderManager();
		//save reordering now
		for(int i = 0; i < adapter.directions.size() ; i++) {
			
			//remove the items that remian from the original list
			//to determine what needs to get deleted
			adapter.originalDirections.remove(adapter.directions.get(i));
			
			//check if item is in same position as it originally was
			if(adapter.originalDirections.size() < (i+1) || !adapter.directions.get(i).equals(adapter.originalDirections.get(i))){
				// item changed, update db
				Direction d = adapter.directions.get(i);
				LoaderCallbacks<Integer> updateCallbacks = 
						(new GenericLoaderCallbacks<Direction, Integer> (getActivity(), d) {

							@Override
							protected Integer doInBackground(Direction data) {
								return (new DirectionHelper(context)).updateDirection(data);
							}

							@Override
							protected void loadFinished(Integer output) {
								
							}

							@Override
							protected void resetLoader(Loader<Integer> args) {
								
							}
						});
				//we need a unique id for each loader, or we'll override
				//the previous call
				l.initLoader(update_loader_value, null, updateCallbacks);
				update_loader_value++;
			}
		}
		
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
		listview.setAdapter(null);
	}
	
}