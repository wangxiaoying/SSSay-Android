package com.momoz.sssay.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.momoz.sssay.R;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayMessage;

public class GetFollowingHandler implements CallAPIHandlerInterface {

	private final static String TAG = "GetFollowlingHandler";

	private Context mContext;
	private Handler mHandler;
	private ListView mLv;
	private String user_token;
	private List<Map<String, Object>> mList;

	public GetFollowingHandler(Context context, Handler handler, ListView lv, String token) {
		mContext = context;
		mHandler = handler;
		mLv = lv;
		user_token = token;
	}

	@Override
	public void handleResponse(String str) {
		Log.i(TAG, "return: " + str);
		if(str == null){
			return;
		}
		
		try{
			Map result_map = (Map) JSONValue.parse(str);
			final String result = (String) result_map.get("result");

			if (result != null) {
				Log.i(TAG, result);
				if (result.equals(SSSayMessage.InvalidToken)) {
					// TODO: need to relogin
				}
				return;
			}
			
		}catch(Exception e){
			JSONArray a_temp = (JSONArray)JSONValue.parse(str);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			mList = list;
			
			int length = a_temp.size();
			for (int i=0; i<length; ++i) {
				Map item = (Map)a_temp.get(i);
				item.put("temp_image", R.drawable.logo);
				list.add(item);
			}
			
			SimpleAdapter adapter = new SimpleAdapter(mContext, list, R.layout.list_item_following, 
					new String[]{"name", "id", "temp_image"}, 
					new int[]{R.id.following_name, R.id.following_id, R.id.following_avatar});
			
			mLv.setAdapter(adapter);
			
			mLv.post(new Runnable(){

				@Override
				public void run() {
					int c = mLv.getChildCount();
					Log.i(TAG, "list view count " + c);
					for (int i=0; i<c; ++i) {
						View v = mLv.getChildAt(i);
						ImageView portrait = (ImageView) v.findViewById(R.id.following_avatar);
						
						HashMap<String, String> params = new HashMap<String, String>();
						params.put("user_token", user_token);
						params.put("user_id", mList.get(i).get("id").toString());
						
						CallAPIHandlerInterface get_portrait_handler = new GetPortraitHandler(mContext, new Handler(), portrait, null);
						CallAPIAsyncTask get_portrait_task = new CallAPIAsyncTask(get_portrait_handler, SSSayConfig.HOST_URL+"user/getportraiturl", params);
						get_portrait_task.execute();
						
					}
				}
			});
			
			
		}
		
	}
}
