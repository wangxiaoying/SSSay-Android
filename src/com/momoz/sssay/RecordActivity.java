package com.momoz.sssay;

import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.momoz.sssay.services.CurrentDoingService;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.UploadFileAsyncTask;

public class RecordActivity extends Activity {

	private static final String TAG = "RECORD_ACTIVITY";

	private TextView mRecorderStatus;
	private TextView mStatusText;
	private TextView mTimeText;
	private Button mStartStopButton;

	private String mOutputFile;
	private MediaRecorder mMediaRecorder;
	
	private Handler handler;
	private Boolean Flag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);
		getActionBar().setDisplayShowHomeEnabled(false);

		handler = new Handler();
		Flag = true;

		setupComponents();

		startRecording();
	}

	private void setupComponents() {
		mRecorderStatus = (TextView) findViewById(R.id.record_text_record_status);
		mStatusText = (TextView) findViewById(R.id.record_text_status);
		mStartStopButton = (Button) findViewById(R.id.record_button_start_stop);
		mTimeText = (TextView) findViewById(R.id.record_text_time);

		mRecorderStatus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startRecording();
			}
		});

		mStartStopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = mStartStopButton.getText().toString();
				String recoding_text = getApplicationContext().getResources().getText(R.string.record_start).toString();
				String stop_text = getApplicationContext().getResources().getText(R.string.record_stop).toString();
				if (text.equals(stop_text)) {
					mStartStopButton.setText(recoding_text);
					stopRecording();
				} else {
					mStartStopButton.setText(stop_text);
					startRecording();
				}
			}
		});
	}

	private void startRecording() {
		try {
			(findViewById(R.id.record_group_top)).setBackgroundResource(R.drawable.record_bg);
			mRecorderStatus.setText(getApplication().getResources().getString(R.string.record_status_recording));
			mStartStopButton.setText(getApplicationContext().getResources().getString(R.string.record_stop));
			
			Time time = new Time();
			time.setToNow();
			String file_name = Long.toString(time.toMillis(false)) + ".3gp";
			mOutputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + file_name;
			Log.i(TAG, mOutputFile);
			
			Flag = true;
			handler.post(new MyRunnable(0, mTimeText));

			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
			mMediaRecorder.setOutputFile(mOutputFile);

			mMediaRecorder.prepare();
			mMediaRecorder.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		try {
			(findViewById(R.id.record_group_top)).setBackgroundResource(R.drawable.record_bg_wait);
			mRecorderStatus.setText("");
			mStartStopButton.setText(getApplicationContext().getResources().getString(R.string.record_start));
			
			Flag = false;
			
			mStatusText.setText("saving...");
			if (mMediaRecorder != null) {
				mMediaRecorder.stop();
				mMediaRecorder.release();
				mMediaRecorder = null;
			}
			mStatusText.setText("uploading...");

			new UploadFileAsyncTask().execute(SSSayConfig.HOST_URL + "voice/upload_voice", mOutputFile, TokenService.getToken(getApplicationContext()));
			mStatusText.setText("success");
		} catch (Exception e) {
			mStatusText.setText("fail...");
			e.printStackTrace();
			Log.e(TAG, "record fail!");
		}
	}
	
	@Override
	public void onBackPressed(){
		
		stopRecording();
		
		super.onBackPressed();
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
			
			if (Flag) {
				
				handler.postDelayed(new MyRunnable(mSecond, mTextView), 1000);
			}
		}
	}

}
