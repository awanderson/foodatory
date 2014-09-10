package com.penguininc.foodatory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
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

import com.penguininc.foodatory.sqlite.helper.RecipeHelper;
import com.penguininc.foodatory.sqlite.loader.GenericLoaderCallbacks;
import com.penguininc.foodatory.sqlite.model.Recipe;
import com.penguininc.foodatory.templates.BasicFragment;

@SuppressLint("NewApi")
public class EditRecipeFragment extends BasicFragment {
	
	EditText mRecipeName;
	//EditText mRecipeDescription;
	ImageView mImageView, mDefaultImage;
	long mRecipeId;
	RecipeDirectionFragment mDirectionFrag;
	String mCurrentPhotoPath;
	String mOldPhotoPath;
	Recipe mCurrentRecipe;
	Uri fileUri;
	
	static final int REQUEST_TAKE_PHOTO = 1;
	
	private final static String DEBUG_TAG = "EditRecipeFragment";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = super.onCreateView(inflater, container, savedInstanceState);
		
		//keep keyboard down
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		Bundle bundle = getArguments();
		mRecipeId = bundle.getLong(Recipe.RECIPE_ID);
		
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
		
		View mSpacer = (View)view.findViewById(R.id.spacer);
		//make extra spacer launch picture intent too
		mSpacer.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				dispatchTakePictureIntent();
			}
		});
		
		mDefaultImage = (ImageView)view.findViewById(R.id.recipe_capture_image);
		
		//load recipe data
		LoaderCallbacks<Recipe> callbacks = (new GenericLoaderCallbacks<Long, Recipe>(getActivity(), mRecipeId) {

			@Override
			protected Recipe doInBackground(Long data) {
				return (new RecipeHelper(context)).getRecipe(data);
			}

			@Override
			protected void loadFinished(Recipe r) {
				mCurrentRecipe = r;
				mRecipeName.setText(r.getName());
				mCurrentPhotoPath = r.getImage();
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
				//mRecipeDescription.setText(r.getDescription());
						
			}

			@Override
			protected void resetLoader(Loader<Recipe> args) {
				
			}
					
		});
				
		getLoaderManager().initLoader(0, null, callbacks);
				
		return view;
		
	}

	@Override
	public void onResume() {
		super.onResume();
		// load directions
		if(mDirectionFrag == null) {
			Log.d("EditRecipeFragment", "direction frag");
			mDirectionFrag = new RecipeDirectionFragment();
			mDirectionFrag.setArguments(getArguments());
			getFragmentManager().beginTransaction()
				.add(R.id.recipe_product_manager, mDirectionFrag).commit();
		} else {
			getFragmentManager().beginTransaction().attach(mDirectionFrag).commit();
		}
				
	}
	
	@Override
	protected int getLayout() {
		return R.layout.fragment_new_recipe;
	}

	
	@Override
	public void onPause() {
		super.onPause();
		String recipe_name = mRecipeName.getText().toString();
		//String recipe_description = mRecipeDescription.getText().toString();
		Recipe r = new Recipe();
		r.setName(recipe_name);
		//r.setDescription(recipe_description);
		r.setId(mRecipeId);
		r.setImage(mCurrentPhotoPath);
		LoaderCallbacks<Long> callbacks = 
			(new GenericLoaderCallbacks<Recipe, Long>(getActivity(), r) {

				@Override
				protected Long doInBackground(Recipe data) {
					(new RecipeHelper(context)).updateRecipe(data);
					return null;
				}

				@Override
				protected void loadFinished(Long output) {
						
				}

				@Override
				protected void resetLoader(Loader<Long> args) {
						
				}
					
			});
		getLoaderManager().initLoader(1, null, callbacks);
		getFragmentManager().beginTransaction().detach(mDirectionFrag).commit();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
	    	
	    	if(data == null) {
	    		Log.d(DEBUG_TAG, "data null");
	    	}
	    	
	    	setPic();
	    	/*
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        mImageView.setImageBitmap(imageBitmap);
	    	*/
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
	
}