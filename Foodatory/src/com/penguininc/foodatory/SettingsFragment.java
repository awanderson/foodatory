package com.penguininc.foodatory;


import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.penguininc.foodatory.framework.HomeScreenFragment;




public class SettingsFragment extends HomeScreenFragment {
	
	public static final String NOTIFICATION_FREQUENCY = "notification_frequency";
	public static final int NOTIFICATION_FREQUENCY_DEFAULT = 0;
	
	public static final String PANTRY_RETENTION = "pantry_retention";
	public static final int PANTRY_RETENTION_DEFAULT = 1;
	
	public static final String[] NOTIFICATION_FREQUENCY_ARRAY = {"Daily", "Weekly",
		"Monthly", "Never"};
	public static final String[] PANTRY_RETENTION_ARRAY = {"Never", "1 Day", "2 Days"
		, "4 days", "1 Week", "Forever"};
	public static final int[] PANTRY_RETENTION_ARRAY_DAYS = {0, 1, 2, 4, 7, -1};
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.settings, menu);
	}

	@Override
	protected int getLayout() {
		return R.layout.fragment_settings;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		TextView versionNumber = (TextView)view.findViewById(R.id.version_number);
		PackageInfo pInfo;
		try {
			pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
			String version = "v" + pInfo.versionName;
			versionNumber.setText(version);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		//Load preferences
		SharedPreferences settings = getActivity().getPreferences(0);
		int notification_frequency = settings.getInt(NOTIFICATION_FREQUENCY, NOTIFICATION_FREQUENCY_DEFAULT);
		int pantry_retention = settings.getInt(PANTRY_RETENTION, PANTRY_RETENTION_DEFAULT);
		
		/* Notification spinner stuff */
		Spinner notificationSpinner = (Spinner) view.findViewById(R.id.notification_frequency);
		ArrayAdapter<String> notificationAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, NOTIFICATION_FREQUENCY_ARRAY);
		notificationSpinner.setAdapter(notificationAdapter);
		notificationSpinner.setSelection(notification_frequency);
		notificationSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				SharedPreferences settings = getActivity().getPreferences(0);
				Editor editor = settings.edit();
				editor.putInt(NOTIFICATION_FREQUENCY, position);
				editor.commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		/* Pantry Spinner Stuff */
		Spinner pantrySpinner = (Spinner) view.findViewById(R.id.pantry_retention);
		ArrayAdapter<String> pantryAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_dropdown_item, PANTRY_RETENTION_ARRAY);
		pantrySpinner.setAdapter(pantryAdapter);
		pantrySpinner.setSelection(pantry_retention);
		pantrySpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				SharedPreferences settings = getActivity().getPreferences(0);
				Editor editor = settings.edit();
				editor.putInt(PANTRY_RETENTION, position);
				editor.commit();
				
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		return view;
		
	}
}
