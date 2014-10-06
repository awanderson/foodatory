package com.penguininc.foodatory;


import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.Log;
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
import com.penguininc.foodatory.service.FreshFoodCheckerService;

public class SettingsFragment extends HomeScreenFragment {
	
	public static final String NOTIFICATION_FREQUENCY = "notification_frequency";
	public static final int NOTIFICATION_FREQUENCY_DEFAULT = 0;
	
	public static final String PANTRY_RETENTION = "pantry_retention";
	public static final int PANTRY_RETENTION_DEFAULT = 1;
	
	public static final String[] NOTIFICATION_FREQUENCY_ARRAY = {"Daily", "Weekly",
		"Monthly", "Never"};
	public static final int[] NOTIFICATION_FREQUENCY_ARRAY_DAYS = {1, 7, 30, -1};
	public static final String[] PANTRY_RETENTION_ARRAY = {"Never", "1 Day", "2 Days"
		, "4 days", "1 Week", "Forever"};
	public static final int[] PANTRY_RETENTION_ARRAY_DAYS = {0, 1, 2, 4, 7, -1};
	
	private static final int FOOD_CHECKER_ALARM_ID = 1;
	
	private static final String DEBUG_TAG = "SettingsFragment";
	
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
				setFoodCheckerNotificationAlarm(getActivity(), position);
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
	
	/**
	 * Sets the alarm for our food checker
	 * @param frequency how often to notify user, should be the index
	 * of NOTIFICATION_FREQUENCY_ARRAY_DAYS or NOTIFICATION_FREQUENCY_ARRAY
	 */
	public static void setFoodCheckerNotificationAlarm(Context context, int frequency) {
		
		// get our pending intent
		Intent foodCheckerIntent = new Intent(context,
				FreshFoodCheckerService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context,
				FOOD_CHECKER_ALARM_ID, foodCheckerIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarm = (AlarmManager)context
				.getSystemService(Context.ALARM_SERVICE);
		// cancel our alarm so we don't have multiple notifications going off
		alarm.cancel(pendingIntent);
		// if our frequency is -1, we don't want any notifications
		// so we can just return
		if(NOTIFICATION_FREQUENCY_ARRAY_DAYS[frequency] == -1) {
			return;
		}
		// set our alarm to go off around 6
		long currentTime = System.currentTimeMillis();
		long triggerAtMillis = (1000*60*60*24) * 18 - currentTime;
		// if our time is negative, add a day to get the right time
		if(triggerAtMillis < 0) {
			triggerAtMillis += (1000*60*60*24) * 24;
		}
		
		Calendar cur_cal = new GregorianCalendar();
		cur_cal.setTimeInMillis(System.currentTimeMillis());//set the current time and date for this calendar

		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.DAY_OF_YEAR, cur_cal.get(Calendar.DAY_OF_YEAR));
		cal.set(Calendar.HOUR_OF_DAY, 18);
		cal.set(Calendar.SECOND, cur_cal.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, cur_cal.get(Calendar.MILLISECOND));
		cal.set(Calendar.DATE, cur_cal.get(Calendar.DATE));
		cal.set(Calendar.MONTH, cur_cal.get(Calendar.MONTH));
		
		long intervalMillis = (1000*60*60*24)
				* NOTIFICATION_FREQUENCY_ARRAY_DAYS[frequency];
		
		Log.d(DEBUG_TAG, "triggerAtMillis = " + triggerAtMillis);
		
		// set our alarm
		alarm.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				cal.getTimeInMillis(), intervalMillis, pendingIntent);
	}
}
