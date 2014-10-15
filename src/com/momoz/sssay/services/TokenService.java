package com.momoz.sssay.services;

import android.content.Context;

import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayTools;

public class TokenService {
	public static void saveToken(Context context, String token) {
		SSSayTools.savePreference(context, SSSayConfig.PREF_KEY_TOKEN, token);
	}
	
	public static String getToken(Context context) {
		return SSSayTools.getPreference(context, SSSayConfig.PREF_KEY_TOKEN);
	}
	
	public static void saveUserName(Context context, String username) {
		SSSayTools.savePreference(context, SSSayConfig.PREF_KEY_USERNAME, username);
	}
	
	public static String getUserName(Context context) {
		return SSSayTools.getPreference(context, SSSayConfig.PREF_KEY_USERNAME);
	}
	
	public static String getUserId(Context context){
		return SSSayTools.getPreference(context, SSSayConfig.PREF_KEY_USERID);
	}
	
	public static void saveUserId(Context context, String userid){
		SSSayTools.savePreference(context, SSSayConfig.PREF_KEY_USERID, userid);
	}
}
