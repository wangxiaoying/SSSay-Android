package com.momoz.sssay;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.momoz.sssay.handlers.GetPortraitHandler;
import com.momoz.sssay.handlers.StartDoingHandler;
import com.momoz.sssay.handlers.StopDoingHandler;
import com.momoz.sssay.services.CurrentDoingService;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;

public class CurrentDoingActivity extends Activity {

	private static final String TAG = "CURRENT_DOING_ACTIVITY";

	private String mDoingName = "";
	private String mDoingId = "";

	private TextView mDoingNameText;
	private TextView mTimeSpanText;
	private ImageView mPortraitImage;
	private Button mRecordButton;
	private Button mOtherVoiceButton;
	private Button mStopDoingButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_doing);
		
		getActionBar().setDisplayShowHomeEnabled(false);

		Intent intent = getIntent();
		mDoingName = intent.getStringExtra(SSSayConfig.EXTRA_CURRENT_DOING_NAME);
		mDoingId = intent.getStringExtra(SSSayConfig.EXTRA_CURRENT_DOING_ID);

		if (mDoingName == "" || mDoingId == "") {
			// TODO: give use some hint
			finish();
		}

		setupComponents();
		startDoingRequest();
		fetchPortrait();
	}

	private void setupComponents() {
		mDoingNameText = (TextView) findViewById(R.id.current_doing_text_name);
		mTimeSpanText = (TextView) findViewById(R.id.current_doing_text_time);
		mPortraitImage = (ImageView) findViewById(R.id.current_doing_image_portrait);
		mRecordButton = (Button)findViewById(R.id.current_doing_group_record);
		mOtherVoiceButton = (Button)findViewById(R.id.current_doing_button_listen);
		mStopDoingButton = (Button)findViewById(R.id.current_doing_group_finish);

		mDoingNameText.setText(mDoingName);

		mStopDoingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				stopDoingRequest(false);
			}
		});

		mRecordButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CurrentDoingActivity.this, RecordActivity.class);
				startActivity(intent);
			}
		});

		mOtherVoiceButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CurrentDoingActivity.this, PlayVoiceActivity.class);
				intent.putExtra(SSSayConfig.EXTRA_CURRENT_DOING_NAME, mDoingName);
				intent.putExtra(SSSayConfig.EXTRA_CURRENT_DOING_ID, mDoingId);
				startActivity(intent);
			}
		});
	}

	private void fetchPortrait() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_id", TokenService.getUserId(getApplicationContext()));
		CallAPIHandlerInterface get_portrait_handler = new GetPortraitHandler(getApplicationContext(),
				new Handler(), mPortraitImage, null);
		CallAPIAsyncTask get_portrait_task = new CallAPIAsyncTask(get_portrait_handler, SSSayConfig.HOST_URL
				+ "user/getportraiturl", params);
		get_portrait_task.execute();
	}

	private void startDoingRequest() {
		CurrentDoingService.is_continue_doing = true;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_token", TokenService.getToken(getApplicationContext()));
		params.put("doing_id", mDoingId);
		CallAPIHandlerInterface current_doing_handler = new StartDoingHandler(getApplicationContext(),
				new Handler(), mTimeSpanText);
		CallAPIAsyncTask current_doing_task = new CallAPIAsyncTask(current_doing_handler, SSSayConfig.HOST_URL
				+ "doing/startdoing", params);
		current_doing_task.execute();
	}

	private void stopDoingRequest(boolean has_next_doing) {
		CurrentDoingService.is_continue_doing = false;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_token", TokenService.getToken(getApplicationContext()));
		params.put("doing_id", mDoingId);
		CurrentDoingService.current_lifelog_id = (long)(-1);
		CallAPIHandlerInterface current_doing_handler = new StopDoingHandler(getApplicationContext(),
				new Handler(), mTimeSpanText);
		CallAPIAsyncTask current_doing_task = new CallAPIAsyncTask(current_doing_handler, SSSayConfig.HOST_URL
				+ "doing/stopdoing", params);
		current_doing_task.execute();
		finish();
	}
	
	@Override
	public void onBackPressed(){
		stopDoingRequest(false);
		super.onBackPressed();
	}
}
