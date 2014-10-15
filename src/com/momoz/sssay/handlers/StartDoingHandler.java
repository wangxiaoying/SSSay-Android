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

public class StartDoingHandler implements CallAPIHandlerInterface {

	private final static String TAG = "StartDoingHandler";

	private Context mContext;
	private Handler mHandler;
	private TextView mTimeSpanText;

	public StartDoingHandler(Context context, Handler handler, TextView tv) {
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

			if (CurrentDoingService.is_continue_doing) {
				CurrentDoingService.current_lifelog_id = (Long) result_map.get("lifelog_id");
				Log.i(TAG, "current life log id " + CurrentDoingService.current_lifelog_id);
				mHandler.post(new MyRunnable(0, mTimeSpanText));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class MyRunnable implements Runnable {
		private int mSecond;
		private TextView mTextView;
		
		public MyRunnable(int s, TextView tv) {
			mSecond = s + 1;
			mTextView = tv;
		}

		@Override
		public void run() {
			int hour = mSecond / 3600;
			int minute = (mSecond - hour * 3600) / 60;
			int second = mSecond -hour * 3600 - minute * 60;
			
			String h = String.format("%02d", hour);
			String m = String.format("%02d", minute);
			String s = String.format("%02d", second);
			
			mTextView.setText(h + " : " + m + " : " + s);
			
			if (CurrentDoingService.is_continue_doing) {
				mHandler.postDelayed(new MyRunnable(mSecond, mTextView), 1000);
			}
		}
	}
}
