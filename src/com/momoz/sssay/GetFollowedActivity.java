package com.momoz.sssay;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.momoz.sssay.handlers.GetFollowedHandler;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;

public class GetFollowedActivity extends Activity {
	
	private static final String TAG = "GETFOLLOWEDACTIVITY";
	
	private ListView mFollowedList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_followed);
		mFollowedList = (ListView) findViewById(R.id.followed_list);

		getActionBar().setDisplayShowHomeEnabled(false);
		
		mFollowedList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				TextView tv = (TextView)view.findViewById(R.id.followed_id);
				Intent intent = new Intent(GetFollowedActivity.this, UserMainPageActivity.class);
				intent.putExtra("user_id", tv.getText().toString());
				startActivity(intent);
			}
		});
		

	}
	
	@Override
	public void onResume(){
		//send request
		HashMap<String,String> params = new HashMap<String, String>();
		params.put("user_token", TokenService.getToken(getApplicationContext()));
		final String user_id = this.getIntent().getExtras().getString("user_id");
		params.put("user_id", user_id);
		
		Log.i(TAG+" onResume", user_id);
		
		CallAPIHandlerInterface get_followed_handler = new GetFollowedHandler(getApplicationContext(), new Handler(), mFollowedList, TokenService.getToken(getApplicationContext()));
		CallAPIAsyncTask get_following_task = new CallAPIAsyncTask(get_followed_handler, SSSayConfig.HOST_URL+"user/getallfans", params);
		get_following_task.execute();
		super.onResume();
	}

}
