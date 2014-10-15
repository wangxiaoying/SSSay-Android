package com.momoz.sssay.handlers;

import java.util.Map;

import org.json.simple.JSONValue;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.momoz.sssay.AllDoingActivity;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;

public class LoginHandler implements CallAPIHandlerInterface {

	private final static String TAG = "LoginHandler";

	private Context mContext;
	private Handler mHandler;
	private TextView mHintText;

	public LoginHandler(Context context, Handler handler, TextView tv) {
		mContext = context;
		mHandler = handler;
		mHintText = tv;
	}

	@Override
	public void handleResponse(String str) {
		Map response_map = null;
		try {
			Log.i(TAG, "return: " + str);

			if (str == null) {
				return;
			}
			response_map = (Map) JSONValue.parse(str);
			Map result_map = (Map) response_map.get("result");
			String username = (String) result_map.get("name");
			String token = (String) result_map.get("token");

			Log.i(TAG, "name " + username + " , token " + token);

			// save token to preference
			TokenService.saveToken(mContext, token);
			TokenService.saveUserName(mContext, username);
			TokenService.saveUserId(mContext, token.split(":")[0]);

			Intent intent = new Intent(mContext, AllDoingActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.putExtra(SSSayConfig.TESTPAGE_EXTRA_KEY, "token is\n" + token);
			mContext.startActivity(intent);
		} catch (Exception e) {
			if (response_map == null) {
				e.printStackTrace();
				return ;
			}
			// display error message
			final String result = (String) response_map.get("result");
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mHintText.setText(result);
					mHintText.setVisibility(View.VISIBLE);
				}
			});
		}
	}
}
