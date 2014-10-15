package com.momoz.sssay.handlers;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayMessage;

public class ChangePasswordHandler implements CallAPIHandlerInterface {

	private final static String TAG = "ChangePasswordHandler";

	private Context mContext;
	private Handler mHandler;
	private TextView mHintText;
	private List<EditText> mEts;

	public ChangePasswordHandler(Context context, Handler handler, TextView tv, List<EditText> ets) {
		mContext = context;
		mHandler = handler;
		mHintText = tv;
		mEts = ets;
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
			
			mHintText.setVisibility(View.VISIBLE);
			mHintText.setText(result);
			
			if(result.equals(SSSayMessage.Success)){
				for(int i = 0; i < 3; ++i){
					mEts.get(i).setText("");
				}
			}
			
		} catch (Exception e) {
			if (response_map == null) {
				e.printStackTrace();
				return ;
			}
		}
	}
}
