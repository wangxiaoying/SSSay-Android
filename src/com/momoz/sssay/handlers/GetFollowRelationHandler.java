package com.momoz.sssay.handlers;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.momoz.sssay.UploadPortraitActivity;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayMessage;

public class GetFollowRelationHandler implements CallAPIHandlerInterface {

	private final static String TAG = "AllDoingHandler";

	private Context mContext;
	private Handler mHandler;
	private Button mRelationButton;
	private String mUserId;

	public GetFollowRelationHandler(Context context, Handler handler, Button btn, String user_id) {
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
			Map result_map = (Map) JSONValue.parse(str);
			final String result = (String) result_map.get("result");
			
			if(result.equals(SSSayMessage.Fail)){
				return;
			}
			
			

			if(result.equals(SSSayMessage.SamePerson)){
				mRelationButton.setText("Change Password");
			}else if(result.equals(SSSayMessage.FollowingRelation) || result.equals(SSSayMessage.FriendRelation)){
				mRelationButton.setText("Unfollow");
				mRelationButton.setBackgroundColor(Color.RED);
			}else{
				mRelationButton.setText("Follow+");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	}
