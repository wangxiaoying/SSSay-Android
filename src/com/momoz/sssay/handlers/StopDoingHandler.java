package com.momoz.sssay.handlers;

import java.util.Map;

import org.json.simple.JSONValue;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.momoz.sssay.services.AllDoingService;
import com.momoz.sssay.services.CurrentDoingService;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayMessage;

public class StopDoingHandler implements CallAPIHandlerInterface {

	private final static String TAG = "StopDoingHandler";

	private Context mContext;
	private Handler mHandler;
	private TextView mTimeSpanText;

	public StopDoingHandler(Context context, Handler handler, TextView tv) {
		mContext = context;
		mHandler = handler;
		mTimeSpanText = tv;
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

			if (!result.equals(SSSayMessage.Success)) {
				mTimeSpanText.setText(result);
				return;
			}
			
			mTimeSpanText.setText(result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
