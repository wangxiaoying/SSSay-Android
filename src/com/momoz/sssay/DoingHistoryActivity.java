package com.momoz.sssay;

import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import com.momoz.sssay.handlers.GetDoingHistoryHandler;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;

public class DoingHistoryActivity extends Activity {

	private static final String TAG = "DOINGHISTORYACTIVITY";

	private ListView mDoingHistoryList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doing_history);
		getActionBar().setDisplayShowHomeEnabled(false);
		
		mDoingHistoryList = (ListView) findViewById(R.id.doing_history_list);

	}

	@Override
	public void onResume() {

		// send request
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_token", TokenService.getToken(getApplicationContext()));
		final String user_id = this.getIntent().getExtras()
				.getString("user_id");
		params.put("user_id", user_id);
		CallAPIHandlerInterface doing_history_handler = new GetDoingHistoryHandler(
				getApplicationContext(), new Handler(), mDoingHistoryList);
		CallAPIAsyncTask all_doing_task = new CallAPIAsyncTask(
				doing_history_handler, SSSayConfig.HOST_URL
						+ "doing/getdoinghistory", params);
		all_doing_task.execute();

		super.onResume();
	}

}
