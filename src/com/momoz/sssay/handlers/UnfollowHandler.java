package com.momoz.sssay.handlers;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayMessage;

public class UnfollowHandler implements CallAPIHandlerInterface {

	private final static String TAG = "FollowHandler";

	private Context mContext;
	private Handler mHandler;
	private Button mRelationButton;
	private String mUserId;

	public UnfollowHandler(Context context, Handler handler, Button btn, String user_id) {
		mContext = context;
		mHandler = handler;
		mRelationButton = btn;
		mUserId = user_id;
	}

	/**
	 * @reponse_message: - {result: xxx} if fail - pure json if success
	 */
	@Override
	public void handleResponse(String str) {
		Log.i(TAG, "return: " + str);

		if (str == null) {
			return;
		}

		try {
			final HashMap<String,String> params = new HashMap<String, String>();
			params.put("user_fan_token", TokenService.getToken(mContext));
			params.put("user_hero_id", mUserId);
			
			Map result_map = (Map) JSONValue.parse(str);
			final String result = (String) result_map.get("result");

			if (result.equals(SSSayMessage.Success)) {
				mRelationButton.setText("Follow+");
				mRelationButton.setBackgroundColor(Color.parseColor("#72d254"));
				mRelationButton.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						CallAPIHandlerInterface follow_handler = new FollowHandler(mContext, new Handler(), mRelationButton, mUserId);
						CallAPIAsyncTask follow_task = new CallAPIAsyncTask(follow_handler, SSSayConfig.HOST_URL + "user/follow", params);
						follow_task.execute();
						
					}
				});
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
