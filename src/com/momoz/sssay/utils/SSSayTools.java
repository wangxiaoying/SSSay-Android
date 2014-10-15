package com.momoz.sssay.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SSSayTools {
	public static String getMD5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
	
	public static void savePreference(Context context, String key, String value) {
		SharedPreferences pref = context.getSharedPreferences(SSSayConfig.PREF_NAME, SSSayConfig.PREF_MODE);
		Editor e = pref.edit();
		e.putString(key, value);
		e.commit();
	}
	
	/**
	 * 
	 * @param context
	 * @param key
	 * @return null if key does not exist
	 */
	public static String getPreference(Context context, String key) {
		SharedPreferences pref = context.getSharedPreferences(SSSayConfig.PREF_NAME, SSSayConfig.PREF_MODE);
		return pref.getString(key, null);
	}
}
