package com.momoz.sssay.handlers;

import java.util.ArrayList;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.momoz.sssay.utils.CallAPIHandlerInterface;

public class GetProfileHandler implements CallAPIHandlerInterface {

	private final static String TAG = "GetProfileHandler";

	private Context mContext;
	private Handler mHandler;
	private ArrayList<TextView> mTvs;

	public GetProfileHandler(Context context, Handler handler, ArrayList<TextView> tvs) {
		mContext = context;
		mHandler = handler;
		mTvs = tvs;
	}

	@Override
	public void handleResponse(String str) {
		
		try{
		Log.i(TAG, "return: " + str);

		if (str == null) {
			return;
		}
		JSONArray a_temp = (JSONArray)JSONValue.parse(str);
		final Map result_map = (Map) a_temp.get(0);
		
		System.out.println((String)result_map.get("name"));
		System.out.println((String)result_map.get("hero_num").toString());
		System.out.println((String)result_map.get("fans_num").toString());
		
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mTvs.get(0).setText((String)result_map.get("name"));
				mTvs.get(1).setText((String)result_map.get("hero_num").toString());
				mTvs.get(2).setText((String)result_map.get("fans_num").toString());
				mTvs.get(3).setText((String)result_map.get("doing_num").toString());

			}
		});
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
