package com.momoz.sssay;

import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.SSSayConfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TestPageActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_page);
		
		getActionBar().setDisplayShowHomeEnabled(false);

		Intent intent = getIntent();
		String message = intent.getExtras().getString(SSSayConfig.TESTPAGE_EXTRA_KEY);
		((TextView) findViewById(R.id.test_page_message)).setText(message);
		
		Button mProfileButton = (Button)findViewById(R.id.go_user_main_page);
		
		mProfileButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TestPageActivity.this, UserMainPageActivity.class);
//				Bundle bundle = new Bundle();
//				bundle.putString("user_id", TokenService.getToken(getApplicationContext()).split(":")[0]);
				intent.putExtra("user_id", TokenService.getToken(getApplicationContext()).split(":")[0]);
				startActivity(intent);
			}
		});
		
		Button mHistoryButton = (Button)findViewById(R.id.get_doing_history);
		mHistoryButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TestPageActivity.this, DoingHistoryActivity.class);
				intent.putExtra("user_id", TokenService.getToken(getApplicationContext()).split(":")[0]);
				startActivity(intent);
			}
		});
		
		Button mGetFollowingButton = (Button)findViewById(R.id.get_following);
		mGetFollowingButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TestPageActivity.this, GetFollowingActivity.class);
				intent.putExtra("user_id", TokenService.getUserId(getApplicationContext()));
				startActivity(intent);
			}
		});
		
		Button mGetFollowedButton = (Button)findViewById(R.id.get_followed);
		mGetFollowedButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(TestPageActivity.this, GetFollowedActivity.class);
				intent.putExtra("user_id", TokenService.getUserId(getApplicationContext()));
				startActivity(intent);
			}
		});
		
	}
}
