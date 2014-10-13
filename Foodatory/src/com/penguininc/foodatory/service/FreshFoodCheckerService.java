package com.penguininc.foodatory.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.penguininc.foodatory.MainActivity;
import com.penguininc.foodatory.R;
import com.penguininc.foodatory.SettingsFragment;
import com.penguininc.foodatory.orm.DatabaseHelper;
import com.penguininc.foodatory.orm.dao.PantryDao;
import com.penguininc.foodatory.orm.object.Pantry;
import com.penguininc.foodatory.orm.object.Product;

public class FreshFoodCheckerService extends Service {

	private final static String DEBUG_TAG = "FreshFoodCheckerService";
	
	private DatabaseHelper databaseHelper = null;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		
		if (databaseHelper == null) {
	        databaseHelper =
	            OpenHelperManager.getHelper(this, DatabaseHelper.class);
	    }
		try {
			
			// Get the number of days we keep our products from our settings
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			int retention_lookup = preferences
					.getInt(SettingsFragment.PANTRY_RETENTION, SettingsFragment.PANTRY_RETENTION_DEFAULT);
			int retention = SettingsFragment.PANTRY_RETENTION_ARRAY_DAYS[retention_lookup];
			
			PantryDao pantryDao = databaseHelper.getPantryDao();
			List<Pantry> pantry = pantryDao.queryForType(Product.FRESH_FOOD, retention);
			List<Pantry> badPantryItems = new ArrayList<Pantry>();
			for(Pantry pantryItem : pantry) {
				// right type, now check if it's within our retention time
				int days = (int)( ((pantryItem.getDateExpire().getTime())
						- Calendar.getInstance().getTime().getTime())
						/ (1000 * 60 * 60 * 24) );
				
				// if our food is going bad today
				if(days < 1) {
					badPantryItems.add(pantryItem);
				}
			}
			
			// intent to call if notification is clicked
			Intent clickIntent = new Intent(this, MainActivity.class);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(MainActivity.class);
			stackBuilder.addNextIntent(clickIntent);
			PendingIntent clickPendingIntent = 
					stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			
			// give more info on the item going bad 
			// if we only have on bad item
			if(badPantryItems.size() == 1) {
				Notification n  = new NotificationCompat.Builder(this)
		        .setContentTitle("Food going bad")
		        .setContentText("Your " + badPantryItems.get(0).getProduct().getProductName()
		        		+ " are going bad")
		        .setContentIntent(clickPendingIntent)
		        .setAutoCancel(true)
		        .setSmallIcon(R.drawable.notification_icon).build();
		    
		  
				NotificationManager notificationManager = 
						(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

				notificationManager.notify(0, n); 
				
			} else if(badPantryItems.size() > 1){
				Notification n  = new NotificationCompat.Builder(this)
		        .setContentTitle("You have food going bad!")
		        .setContentText("You have " + badPantryItems.size() + " items going bad")
		        .setContentIntent(clickPendingIntent)
		        .setAutoCancel(true)
		        .setSmallIcon(R.drawable.notification_icon).build();
		    
		  
				NotificationManager notificationManager = 
						(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

				notificationManager.notify(0, n);
			}
		} catch(SQLException e) {
			
		}
		
		 
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (databaseHelper != null) {
	        OpenHelperManager.releaseHelper();
	        databaseHelper = null;
	    }
	}

	
}