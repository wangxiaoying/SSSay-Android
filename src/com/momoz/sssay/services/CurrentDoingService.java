package com.momoz.sssay.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ExpandableListView;

import com.momoz.sssay.handlers.AllDoingHandler;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayTools;

public class CurrentDoingService {

	private static final String TAG = "AllDoingService";

	public static boolean is_continue_doing = false;
	
	public static Long current_lifelog_id = -1L;
}
