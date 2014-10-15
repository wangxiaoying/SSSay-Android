package com.momoz.sssay;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.momoz.sssay.handlers.GetFollowingHandler;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;

public class GetFollowingActivity extends Activity {
	
	private static final String TAG = "GETFOLLOWINGACTIVITY";
	
	private ListView mFollowingList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_following);
		
		getActionBar().setDisplayShowHomeEnabled(false);
		
		mFollowingList = (ListView) findViewById(R.id.following_list);
		mFollowingList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				// TODO Auto-generated method stub
				TextView tv = (TextView)view.findViewById(R.id.following_id);
				Intent intent = new Intent(GetFollowingActivity.this, UserMainPageActivity.class);
				intent.putExtra("user_id", tv.getText().toString());
				startActivity(intent);
			}
			
		});
		
	}
	
	@Override
	public void onResume(){
		
		HashMap<String,String> params = new HashMap<String, String>();
		params.put("user_token", TokenService.getToken(getApplicationContext()));
		final String user_id = this.getIntent().getExtras().getString("user_id");
		params.put("user_id", user_id);
		CallAPIHandlerInterface get_following_handler = new GetFollowingHandler(getApplicationContext(), new Handler(), mFollowingList, TokenService.getToken(getApplicationContext()));
		CallAPIAsyncTask get_following_task = new CallAPIAsyncTask(get_following_handler, SSSayConfig.HOST_URL+"user/getallheros", params);
		get_following_task.execute();
		
		super.onResume();
	}

}
