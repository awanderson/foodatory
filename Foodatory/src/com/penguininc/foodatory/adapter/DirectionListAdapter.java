package com.penguininc.foodatory.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.penguininc.foodatory.R;
import com.penguininc.foodatory.orm.object.Direction;

public class DirectionListAdapter extends ArrayAdapter<Direction> {
	
	private Context context;
	private List<Direction> directions;
	private List<Direction> originalDirections;
	
	public class DirectonComparator implements Comparator<Direction>{
		
		@Override
	    public int compare(Direction d1, Direction d2) {
	        return d1.getOrder() - d2.getOrder();
	    }
	}
	
	public DirectionListAdapter(Context context, List<Direction> directions) {
		super(context, R.layout.list_item_direction, directions);
		this.context = context;
		
		Collections.sort(directions, new DirectonComparator());
		Iterator<Direction> iter = directions.iterator();
		// double check order of list, can get out of whack when we remove items
		int order = 1;
		while(iter.hasNext()){
			Direction d = iter.next();
			d.setOrder(order);
			order++;
		}
		this.directions = directions;
		this.originalDirections = new ArrayList<Direction>(directions);
		
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater)
					context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(R.layout.list_item_direction, null);
		}
		
		TextView mDirectionContent = (TextView) convertView.findViewById(R.id.direction_content);
		TextView mDirectionOrder = (TextView) convertView.findViewById(R.id.direction_order);
		
		mDirectionContent.setText(directions.get(position).getContent());
		mDirectionOrder.setText(String.valueOf(directions.get(position).getOrder()));
		
		return convertView;
	}
	
	public static void changeCount(View v, int order) {
		
		TextView mDirectionOrder = (TextView)v.findViewById(R.id.direction_order);
		Log.d("DirecitonListAdapter", "changing from " + mDirectionOrder.getText().toString() + " to " + order);
		mDirectionOrder.setText(String.valueOf(order));
		
	}
	
	public List<Direction> getDirections() {
		return directions;
	}
	
	public List<Direction> getOriginalDirections() {
		return originalDirections;
	}
}