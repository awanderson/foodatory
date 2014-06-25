package com.penguininc.foodatory.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.penguininc.foodatory.R;
import com.penguininc.foodatory.sqlite.model.Recipe;
import com.penguininc.foodatory.utilities.ImageRounder;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {
	
	public final static String DEBUG_TAG = "RecipeListAdapter";
	
	private Context context;
	private ArrayList<Recipe> recipes;
	private ArrayList<ImageView> thumbnails;
	private ArrayList<String> imageNames;
	
	public RecipeListAdapter(Context context, ArrayList<Recipe> recipes) {
		super(context, R.layout.list_item_recipe, recipes);
		this.context = context;
		this.recipes = recipes;
		thumbnails = new ArrayList<ImageView>();
		imageNames = new ArrayList<String>();
	}
	
	
	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
	           LayoutInflater mInflater = (LayoutInflater)
	                   context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	           convertView = mInflater.inflate(R.layout.list_item_recipe, null);
	    }
		 
		TextView mRecipeName = (TextView) convertView.findViewById(R.id.recipe_name);
		ImageView mThumbnail = (ImageView) convertView.findViewById(R.id.recipe_thumbnail);
		String imageName = recipes.get(position).getImage();
		thumbnails.add(position, mThumbnail);
		imageNames.add(position, imageName);
		final int p = position;
		ViewTreeObserver vto = mThumbnail.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

		    @Override
		    public void onGlobalLayout() {
		    	setPic(p);
		        ViewTreeObserver obs = thumbnails.get(p).getViewTreeObserver();

		        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		            obs.removeOnGlobalLayoutListener(this);
		        } else {
		            obs.removeGlobalOnLayoutListener(this);
		        }
		    }

		});
		mRecipeName.setText(recipes.get(position).getName());
		return convertView;
	}
	
	private void setPic(int position) {
		int targetW = thumbnails.get(position).getWidth();
	    int targetH = thumbnails.get(position).getHeight();

	    Log.d(DEBUG_TAG, "targetH = " + targetH + " targetW = "+ targetW);
	    
	    Bitmap bitmap;
	    if(imageNames.get(position) == null || imageNames.get(position) == "") {
	    	bitmap = getColorBitmap(targetW, targetH);
	    } else {
	    	// Get the dimensions of the bitmap
		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		    bmOptions.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(imageNames.get(position), bmOptions);
		    int photoW = bmOptions.outWidth;
		    int photoH = bmOptions.outHeight;

		    // Determine how much to scale down the image
		    int scaleFactor = Math.max(photoW/targetW, photoH/targetH);

		    // Decode the image file into a Bitmap sized to fill the View
		    bmOptions.inJustDecodeBounds = false;
		    bmOptions.inSampleSize = scaleFactor;
		    bmOptions.inPurgeable = true;

		    bitmap = BitmapFactory.decodeFile(imageNames.get(position), bmOptions);
	    }
	    if(bitmap == null) {
	    	bitmap = getColorBitmap(targetW, targetH);
	    }
	    
	    // crop from center of image to form a square
	    Bitmap dstBmp;
	    if (bitmap.getWidth() >= bitmap.getHeight()){

	    	  dstBmp = Bitmap.createBitmap(
	    	     bitmap, 
	    	     bitmap.getWidth()/2 - bitmap.getHeight()/2,
	    	     0,
	    	     bitmap.getHeight(), 
	    	     bitmap.getHeight()
	    	     );

	    	}else{

	    	  dstBmp = Bitmap.createBitmap(
	    	     bitmap,
	    	     0, 
	    	     bitmap.getHeight()/2 - bitmap.getWidth()/2,
	    	     bitmap.getWidth(),
	    	     bitmap.getWidth() 
	    	     );
	    	}
	    //dstBmp = Bitmap.createBitmap(dstBmp, photoW/2, photoH/2, targetW, targetH);
	    dstBmp = ImageRounder.getRoundedCornerBitmap(dstBmp, dstBmp.getHeight());
	    thumbnails.get(position).setImageBitmap(dstBmp);
	}
	
	private Bitmap getColorBitmap(int width, int height) {
		// CGRect rect = CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
		Rect rect = new Rect(0, 0, width, height);
		//UIGraphicsBeginImageContext(rect.size);
		//CGContextRef context = UIGraphicsGetCurrentContext();
		Bitmap image = Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
		Canvas canvas = new Canvas(image);

		//UIColor* color = [UIColor colorWithRed:255/255.0 green:245/255.0 blue:238/255.0 alpha:1.0];
		int color = getRandomColor();

		//CGContextSetFillColorWithColor(context, [color CGColor]);
		Paint paint = new Paint();
		paint.setColor(color);

		//CGContextFillRect(context, rect);
		canvas.drawRect(rect, paint);
		
		return image;
	}
	
	private int getRandomColor() {
		int rand = (int)(Math.random() * 4);
		Log.d(DEBUG_TAG, "random = " + rand);
		if(rand == 0) {
			return context.getResources().getColor(R.color.blue_button);
		} else if (rand == 1) {
			return context.getResources().getColor(R.color.green_button);
		} else if (rand == 2) {
			return context.getResources().getColor(R.color.red_button);
		} else {
			return context.getResources().getColor(R.color.black_button);
		}
	}
	
}