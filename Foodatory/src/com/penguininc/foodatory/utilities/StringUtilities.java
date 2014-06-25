package com.penguininc.foodatory.utilities;

public class StringUtilities {
	
	public static boolean checkBasicString(String s) {
		if(s == null || s == "" || s.isEmpty()) {
			return false;
		}
		return true;
	}
	
}