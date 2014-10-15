package com.momoz.sssay.handlers;

import java.util.Map;

import org.json.simple.JSONValue;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.ExpandableListView;

import com.momoz.sssay.services.AllDoingService;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayMessage;

public class AllDoingHandler implements CallAPIHandlerInterface {

	private final static String TAG = "AllDoingHandler";

	private Context mContext;
	private Handler mHandler;
	private ExpandableListView mListView;

	public AllDoingHandler(Context context, Handler handler, ExpandableListView listview) {
		mContext = context;
		mHandler = handler;
		mListView = listview;
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

			if (result != null) {
				Log.i(TAG, result);
				if (result.equals(SSSayMessage.InvalidToken)) {
					// TODO: need to relogin
				}
				return;
			}

		} catch (Exception e) {
			// the json cannot be casted to Map
			AllDoingService.saveAllDoing(mContext, str);
			AllDoingService.refreshAllDoing(mContext, mHandler, mListView);
		}
	}
}
