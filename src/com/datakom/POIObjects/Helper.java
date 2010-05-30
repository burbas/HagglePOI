package com.datakom.POIObjects;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import android.util.Log;

public class Helper {

	public static String createMD5(String s) {
		//compute md5-hash for comparison
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Log.e("HelperClass", "MD5 Algorithm not found!");
		}
		byte[] bArr = md.digest(s.getBytes());

		Formatter formatter = new Formatter();
        for (byte b : bArr) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
	}
	
	
}
