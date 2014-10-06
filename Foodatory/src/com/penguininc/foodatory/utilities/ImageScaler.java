package com.penguininc.foodatory.utilities;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageScaler {
	
	public static final String DEBUG_TAG = "ImageScaler";
	
	/**
	 * Function that takes bitmap in, and scales the image 
	 * to width x height and returns the scaled bitmap
	 * @param in bitmap to scale down, doesn't recycle so can
	 * continue to use it
	 * @param width 
	 * @param height
	 * @param outFile
	 * @return
	 */
	public static Bitmap scaleImage(Bitmap in, 
			int width, int height, String outFile) {
		
		// get our aspect
		float targetAspect = (float)height/(float)width;
		float sourceAspect = (float)in.getHeight() / (float)in.getWidth();
		Bitmap cropped;
		// our image is too tall
		if(sourceAspect >= targetAspect) {
			Log.d(DEBUG_TAG, "sourceAspect is larger, image too tall");
			Log.d(DEBUG_TAG, "source = " + in.getWidth() + " x " + in.getHeight());
			Log.d(DEBUG_TAG, "target = " + width + " x " + height);
			int targetHeight = Math.round(targetAspect * in.getWidth());
			int y = (in.getHeight() - targetHeight) / 2;
			Log.d(DEBUG_TAG, "targetHeight = " + targetHeight + " y = " + y);
			cropped = Bitmap.createBitmap(in, 0, y, in.getWidth(), targetHeight);
		} else {
			Log.d(DEBUG_TAG, "targetAspect is larger, image is too wide");
			// our source is too wide
			int targetWidth = (int)Math.floor((width/height) + in.getHeight());
			int x = (in.getWidth() - targetWidth) /2;
			cropped = Bitmap.createBitmap(in, x, 0, x + targetWidth, in.getHeight());
		}
		// scale image last
		Bitmap out = Bitmap.createScaledBitmap(cropped, width, height, false);
		if(outFile != null && !outFile.equals("")) {
			File file = new File(outFile);
			FileOutputStream fOut;
			try {
				fOut = new FileOutputStream(file);
				Log.d(DEBUG_TAG, "outFile = " + outFile);
			    out.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
			    fOut.flush();
			    fOut.close();
			    out.recycle();
			    Log.d(DEBUG_TAG, "scaled image");

			} catch (Exception e) { // TODO
				Log.d(DEBUG_TAG, "didn't scale image");
			}
		}
		return out;
	}
	
}