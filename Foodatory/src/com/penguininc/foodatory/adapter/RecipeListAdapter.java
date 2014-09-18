package com.penguininc.foodatory.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
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

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;
import com.penguininc.foodatory.R;
import com.penguininc.foodatory.orm.object.Recipe;
import com.penguininc.foodatory.utilities.ImageRounder;

public class RecipeListAdapter extends ArrayAdapter<Recipe> {
	
	public final static String DEBUG_TAG = "RecipeListAdapter";
	
	private Context context;
	private ImageLoader imageLoader;
	private List<Recipe> recipes;
	private ArrayList<ImageView> thumbnails;
	private ArrayList<String> imageNames;
	private ArrayList<Bitmap> imageBitmaps;
	
	public RecipeListAdapter(Context context, List<Recipe> recipes) {
		super(context, R.layout.list_item_recipe, recipes);
		this.context = context;
		this.recipes = recipes;
		thumbnails = new ArrayList<ImageView>();
		imageNames = new ArrayList<String>();
		imageBitmaps = new ArrayList<Bitmap>(recipes.size());
		for(int i = 0; i < recipes.size(); i++) {
			imageBitmaps.add(i, null);
		}
		imageLoader = ImageLoader.getInstance();
		// Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration
        		.Builder(context).build();
        imageLoader.init(config);
		
	}
	
	
	@SuppressLint("NewApi")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder mHolder = null;
		if (convertView == null) {
			mHolder = new ViewHolder();
	        LayoutInflater mInflater = (LayoutInflater)
	        		  context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
	        convertView = mInflater.inflate(R.layout.list_item_recipe, null);
	        mHolder.recipe_name = (TextView) convertView.findViewById(R.id.recipe_name);
	        mHolder.thumbnail = (ImageView) convertView.findViewById(R.id.recipe_thumbnail);
	        convertView.setTag(mHolder);
		} else {
			mHolder = (ViewHolder)convertView.getTag();
		}
		 
		String imageName = recipes.get(position).getImage();
		thumbnails.add(position, mHolder.thumbnail);
		imageNames.add(position, imageName);
		final int p = position;
		ViewTreeObserver vto = mHolder.thumbnail.getViewTreeObserver();
		if(mHolder.thumbnail.getHeight() == 0) {
			Log.d(DEBUG_TAG, "No height");
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			    @Override
			    public void onGlobalLayout() {
			    	Log.d(DEBUG_TAG, "color in global layout = " + recipes.get(position).getColor());
			    	setPic(p);
			        ViewTreeObserver obs = thumbnails.get(p).getViewTreeObserver();

			        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			            obs.removeOnGlobalLayoutListener(this);
			        } else {
			            obs.removeGlobalOnLayoutListener(this);
			        }
			    }

			});
		} else {
			setPic(p);
		}
		
		mHolder.recipe_name.setText(recipes.get(position).getName());
		return convertView;
	}
	
	private void setPic(final int position) {

		final ImageView imageView = thumbnails.get(position);
		final int targetW = imageView.getWidth();
	    final int targetH = imageView.getHeight();
	    ImageSize targetSize = new ImageSize(targetW, targetH);
	    Recipe recipe = recipes.get(position);
	    
	    BitmapProcessor postProcessor = new BitmapProcessor() {
			
			@Override
			public Bitmap process(Bitmap bitmap) {
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
			    dstBmp = ImageRounder.getRoundedCornerBitmap(dstBmp, dstBmp.getHeight());
			    return dstBmp;
			}
		};
		DisplayImageOptions options = new DisplayImageOptions.Builder()
	    		.postProcessor(postProcessor)
	    		.showImageForEmptyUri(new BitmapDrawable(context.getResources(),
	    				getColorBitmap(targetW, targetH, recipe.getColor())))
	    		.cacheInMemory(true)
	    		.showImageOnFail(new BitmapDrawable(context.getResources(),
	    				getColorBitmap(targetW, targetH, recipe.getColor())))
	    		.build();
	    
		if(imageNames.get(position) == null) {
			imageView.setImageBitmap(getColorBitmap(targetW, targetH, recipe.getColor()));
		} else {
			//bitmap = getColorBitmap(targetW, targetH);
			imageLoader.loadImage("file://"+imageNames.get(position), 
					targetSize,
					options,
					new SimpleImageLoadingListener() {
				
				@Override
			    public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
					thumbnails.get(position).setImageBitmap(bitmap);
				}
				
				@Override
				public void onLoadingFailed(String iamgeUri, View view, FailReason failReason) {
				}
				
			});
		}
		
	}
	
	private Bitmap getColorBitmap(int width, int height, int color) {
		// CGRect rect = CGRectMake(0.0f, 0.0f, 1.0f, 1.0f);
		Rect rect = new Rect(0, 0, width, height);
		//UIGraphicsBeginImageContext(rect.size);
		//CGContextRef context = UIGraphicsGetCurrentContext();
		Bitmap image = Bitmap.createBitmap(rect.width(), rect.height(), Config.ARGB_8888);
		Canvas canvas = new Canvas(image);

		//UIColor* color = [UIColor colorWithRed:255/255.0 green:245/255.0 blue:238/255.0 alpha:1.0];
		int colorResource = getColorResource(color);

		//CGContextSetFillColorWithColor(context, [color CGColor]);
		Paint paint = new Paint();
		paint.setColor(colorResource);

		//CGContextFillRect(context, rect);
		canvas.drawRect(rect, paint);
		Bitmap dstBmp = ImageRounder.getRoundedCornerBitmap(image, width);
		return dstBmp;
	}
	
	private int getColorResource(int color) {
		if(color == Recipe.BLUE) {
			return context.getResources().getColor(R.color.blue_button);
		} else if (color == Recipe.GREEN) {
			return context.getResources().getColor(R.color.green_button);
		} else if (color == Recipe.RED) {
			return context.getResources().getColor(R.color.red_button);
		} else {
			return context.getResources().getColor(R.color.black_button);
		}
	}
	
	public static class ViewHolder {
		TextView recipe_name;
		ImageView thumbnail;
		int color;
	}
	
}