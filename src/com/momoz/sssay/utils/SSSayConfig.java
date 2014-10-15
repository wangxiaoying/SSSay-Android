package com.momoz.sssay.utils;

import android.content.Context;

public class SSSayConfig {
	// server info

	public final static String HOST_URL = "http://192.168.0.102:8000/";

	public final static String MEDIA_URL = HOST_URL+"media/";
	
	// preference info
	public final static String PREF_NAME = "sssay_pref_breaking_bad";
	public final static int PREF_MODE = Context.MODE_PRIVATE;
	
	//login return detail
	public final static String FLAG_RETURN = "return";
	public final static String FLAG_NOT_RETURN = "not reuturn";
	
	// token info
	public final static String PREF_KEY_TOKEN = "token_key_gustavo";
	public final static String PREF_KEY_USERNAME = "username_key_jesse_pinkman";
	public final static String PREF_KEY_USERID = "user_id_heissenberg";
	
	// doing info
	public final static String PREF_KEY_CATEGORY = "doing_key_walter_white";
	
	// current_doing_activity
	public final static String EXTRA_CURRENT_DOING_ID = "current_doing_id_olivia";
	public final static String EXTRA_CURRENT_DOING_NAME = "current_doing_name_walter";
	
	// user_main_page_activity
	public final static String EXTRA_IS_MYSELF = "is_this_myself_peter";
	
	/////////////////////////////////////////////////////////////////////////
	// DEBUG use, delete in the future
	public final static String TESTPAGE_EXTRA_KEY = "message_muamua";
}
