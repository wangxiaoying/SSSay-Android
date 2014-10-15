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
import com.momoz.sssay.LoginActivity;
import com.momoz.sssay.RegisterActivity;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayMessage;

public class LogoutHandler implements CallAPIHandlerInterface {

	private final static String TAG = "LogoutHandler";

	private Context mContext;
	private Handler mHandler;

	public LogoutHandler(Context context, Handler handler) {
		mContext = context;
		mHandler = handler;
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
			String result = (String) response_map.get("result");

			Log.i(TAG, result);

			Log.i("logout", "logout");
			TokenService.saveToken(mContext, null);
			TokenService.saveUserId(mContext, null);
			TokenService.saveUserName(mContext, null);
			Intent intent = new Intent(mContext, RegisterActivity.class);
			intent.putExtra("FLAG", SSSayConfig.FLAG_NOT_RETURN);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);

		} catch (Exception e) {

		}
	}
}
