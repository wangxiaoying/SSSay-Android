package com.momoz.sssay;

import java.util.HashMap;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.momoz.sssay.handlers.PlayVoiceHandler;
import com.momoz.sssay.services.CurrentDoingService;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;

public class PlayVoiceActivity extends Activity {

	private static final String TAG = "PLAY_VOICE_ACTIVITY";

	private TextView mUsernameText;
	private TextView mDoingNameText;
	private Button mNextButton;
	private Button mStopPlayButton;
	private ImageView mUserPortrait;
	
	private String mDoingId;
	private String mDoingName;
	
	public static boolean continue_play = false;
	public static MediaPlayer media_player;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_voice);
		
		getActionBar().setDisplayShowHomeEnabled(false);
		
		mDoingName = getIntent().getStringExtra(SSSayConfig.EXTRA_CURRENT_DOING_NAME);
		mDoingId = getIntent().getStringExtra(SSSayConfig.EXTRA_CURRENT_DOING_ID);

		setupComponents();
		nextVoice();
	}
	
	private void setupComponents() {
		mUsernameText = (TextView) findViewById(R.id.play_voice_text_username);
		mDoingNameText = (TextView) findViewById(R.id.play_voice_text_doing);
		mNextButton = (Button) findViewById(R.id.play_voice_button_next);
		mStopPlayButton = (Button) findViewById(R.id.play_voice_button_stop);
		mUserPortrait = (ImageView) findViewById(R.id.play_voice_user_portrait);
		
		mDoingNameText.setText(mDoingName);
		
		
		
		
		mNextButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				nextVoice();
				mStopPlayButton.setBackgroundColor(Color.parseColor("#72d254"));
			}
		});
		
		mStopPlayButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				continue_play = false;
				mStopPlayButton.setBackgroundColor(Color.GRAY);
				killMediaPlayer();
			}
		});
	}
	
	private void nextVoice() {
		continue_play = true;
		killMediaPlayer();
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_token", TokenService.getToken(getApplicationContext()));
		params.put("doing_id", mDoingId);
		CallAPIHandlerInterface play_voice_handler = new PlayVoiceHandler(getApplicationContext(), new Handler(), mUsernameText, mDoingNameText, mDoingId, mUserPortrait);
		if (CurrentDoingService.current_lifelog_id != -1) {
			CallAPIAsyncTask play_voice_task = new CallAPIAsyncTask(play_voice_handler, SSSayConfig.HOST_URL + "voice/random_doing_voice", params);
			play_voice_task.execute();
		} else {
			CallAPIAsyncTask play_voice_task = new CallAPIAsyncTask(play_voice_handler, SSSayConfig.HOST_URL + "voice/random_all_voice", params);
			play_voice_task.execute();
		}
	}
	
	public static void killMediaPlayer() {
        if(media_player!=null) {
            try {
            	media_player.release();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
