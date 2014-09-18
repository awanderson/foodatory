package com.penguininc.foodatory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.penguininc.foodatory.framework.BasicFragment;
import com.penguininc.foodatory.orm.object.Recipe;

@SuppressLint("NewApi")
public class EditRecipeFragment extends BasicFragment {
	
	EditText mRecipeName;
	//EditText mRecipeDescription;
	ImageView mImageView, mDefaultImage;
	RecipeDirectionFragment mDirectionFrag;
	String mCurrentPhotoPath;
	String mOldPhotoPath;
	Uri fileUri;
	Recipe mRecipe;
	RuntimeExceptionDao<Recipe, Integer> mRecipeDao;
	
	static final int REQUEST_TAKE_PHOTO = 1;
	
	private final static String DEBUG_TAG = "EditRecipeFragment";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		//keep keyboard down
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		Bundle bundle = getArguments();
		mRecipe = (Recipe)bundle.getSerializable(Recipe.KEY);
		Log.d(DEBUG_TAG, "color = " + mRecipe.getColor());
		mRecipeName = (EditText)view.findViewById(R.id.recipe_name);
		
		
		
		//mRecipeDescription = (EditText)view.findViewById(R.id.recipe_description);
		mImageView = (ImageView)view.findViewById(R.id.recipe_image);
		mImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dispatchTakePictureIntent();
				Log.d("EditRecipeFragment", "view clicked");
			}
		});
		
		/*
		 * Change the color of our background to match
		 * the color for a given recipe
		 */
		if(mRecipe.getColor() == Recipe.BLUE) {
			mImageView.setBackgroundColor(
					getResources().getColor(R.color.blue_button_pressed));
		} else if(mRecipe.getColor() == Recipe.GREEN) {
			mImageView.setBackgroundColor(
					getResources().getColor(R.color.green_button_pressed));
		} else if(mRecipe.getColor() == Recipe.RED) {
			mImageView.setBackgroundColor(
					getResources().getColor(R.color.red_button));
		} else {
			mImageView.setBackgroundColor(
					getResources().getColor(R.color.black_button));
		}
		
		View mSpacer = (View)view.findViewById(R.id.spacer);
		//make extra spacer launch picture intent too
		mSpacer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dispatchTakePictureIntent();
			}
		});
		
		mDefaultImage = (ImageView)view.findViewById(R.id.recipe_capture_image);
		mRecipeName.setText(mRecipe.getName());
		mCurrentPhotoPath = mRecipe.getImage();
		//Have to add an observer so we don't change the image 
		//until after it has a width and height set
		ViewTreeObserver vto = mImageView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

		    @Override
		    public void onGlobalLayout() {
		    	Log.d("EditRecipeFragment", "image height = " + mImageView.getHeight());
		    	setPic();
		        ViewTreeObserver obs = mImageView.getViewTreeObserver();

		        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
		            obs.removeOnGlobalLayoutListener(this);
		        } else {
		            obs.removeGlobalOnLayoutListener(this);
		        }
		    }

		});
		
		return view;
		
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(DEBUG_TAG, "in on resume");
		// load directions
		mDirectionFrag = new RecipeDirectionFragment();
		mDirectionFrag.setArguments(getArguments());
		getFragmentManager().beginTransaction()
			.replace(R.id.recipe_product_manager, mDirectionFrag).commit();
				
	}
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_new_recipe;
	}

	
	@Override
	public void onPause() {
		super.onPause();
		String recipeName = mRecipeName.getText().toString();
		mRecipe.setName(recipeName);
		mRecipe.setImage(mCurrentPhotoPath);
		Log.d(DEBUG_TAG, "recipe color = " + mRecipe.getColor());
		if(mRecipeDao == null) {
			mRecipeDao = getHelper().getRecipeRuntimeExceptionDao();
		}
		mRecipeDao.update(mRecipe);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
	    	
	    	if(data == null) {
	    		// revert back to our old photo
	    		mCurrentPhotoPath = mOldPhotoPath;
	    		Log.d(DEBUG_TAG, "data is null");
	    	} else{
	    		setPic();
	    	}
	    	
	    	/*
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        mImageView.setImageBitmap(imageBitmap);
	    	*/
	    } else {
	    	mCurrentPhotoPath = mOldPhotoPath;
	    }
	}
	
	private File createImageFile() throws IOException {
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(
	            Environment.DIRECTORY_PICTURES);
	    Log.d("EditRecipeFragment", "Got storageDir");
	    File image = File.createTempFile(
	        imageFileName,  /* prefix */
	        ".jpg",         /* suffix */
	        storageDir      /* directory */
	    );
	    Log.d("EditRecipeFragment", "couldn't create temp file");
	    //Save the old path incase we don't get a new valid picture file
	    mOldPhotoPath = mCurrentPhotoPath;
	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = image.getAbsolutePath();
	    return image;
	}
	

	private void dispatchTakePictureIntent() {
	    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    // Ensure that there's a camera activity to handle the intent
	    if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
	        // Create the File where the photo should go
	        File photoFile = null;
	        try {
	            photoFile = createImageFile();
	        } catch (IOException ex) {
	        	Log.d("EditRecipeFragment", "Couldn't create photo");
	            // Error occurred while creating the File
	        }
	        // Continue only if the File was successfully created
	        if (photoFile != null) {
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            takePictureIntent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
	        }
	    }
	}
	
	private void setPic() {
		if(mCurrentPhotoPath != null && mCurrentPhotoPath != "") {
			// Get the dimensions of the View
		    int targetW = mImageView.getWidth();
		    int targetH = mImageView.getHeight();
	
		    // Get the dimensions of the bitmap
		    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		    bmOptions.inJustDecodeBounds = true;
		    BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		    int photoW = bmOptions.outWidth;
		    int photoH = bmOptions.outHeight;
	
		    // Determine how much to scale down the image
		    int scaleFactor = Math.max(photoW/targetW, photoH/targetH);
	
		    // Decode the image file into a Bitmap sized to fill the View
		    bmOptions.inJustDecodeBounds = false;
		    bmOptions.inSampleSize = scaleFactor;
		    bmOptions.inPurgeable = true;
		    
		    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		    if(bitmap == null || bitmap.getByteCount() == 0) {
		    	Log.d(DEBUG_TAG, "Bitmap no good");
		    	mCurrentPhotoPath = mOldPhotoPath;
		    	mOldPhotoPath = null;
		    	setPic();
		    } else {
		    	mDefaultImage.setVisibility(View.GONE);
		    }
		    mImageView.setImageBitmap(bitmap);
		}
		    
	}
	
	private void setColor() {
		
	}
	
}