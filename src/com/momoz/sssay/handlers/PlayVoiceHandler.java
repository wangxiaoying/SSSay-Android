package com.momoz.sssay.handlers;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONValue;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.momoz.sssay.PlayVoiceActivity;
import com.momoz.sssay.UserMainPageActivity;
import com.momoz.sssay.services.CurrentDoingService;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayMessage;

public class PlayVoiceHandler implements CallAPIHandlerInterface {

	private final static String TAG = "AllDoingHandler";

	private Context mContext;
	private Handler mHandler;
	private TextView mUsernameText;
	private TextView mDoingNameText;
	private String mDoingId;
	private ImageView mUserPortrait;
	
	private String user_id;

	public PlayVoiceHandler(Context context, Handler handler, TextView u, TextView d, String doing_id, ImageView iv) {
		mContext = context;
		mHandler = handler;
		mUsernameText = u;
		mDoingNameText = d;
		mDoingId = doing_id;
		mUserPortrait = iv;
	}
	
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
				Log.i(TAG, "fail " + result);
				if (result.equals(SSSayMessage.InvalidToken)) {
					// TODO: need to relogin
				}
				return;
			}
			
			Log.i(TAG, "audio src " + SSSayConfig.MEDIA_URL + (String) result_map.get("voice_url"));
			
			user_id = result_map.get("user_id").toString();
			
			mUsernameText.setText((String)result_map.get("username"));
			mDoingNameText.setText((String)result_map.get("doingname"));
			
			HashMap<String, String> params = new HashMap<String ,String>();
			params.put("user_id", user_id);
			
			//get user portrait
			CallAPIHandlerInterface get_portrait_handler = new GetPortraitHandler(mContext, new Handler(), mUserPortrait, null);
			CallAPIAsyncTask get_portrait_task = new CallAPIAsyncTask(get_portrait_handler, SSSayConfig.HOST_URL+"user/getportraiturl", params);
			get_portrait_task.execute();
			
			OnClickListener listener = new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent(mContext, UserMainPageActivity.class);
					intent.putExtra("user_id", user_id);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					mContext.startActivity(intent);
				}
			};
			
			mUsernameText.setOnClickListener(listener);
			mUserPortrait.setOnClickListener(listener);
			
			PlayVoiceActivity.killMediaPlayer();
			PlayVoiceActivity.media_player = new MediaPlayer();
			PlayVoiceActivity.media_player.setDataSource(SSSayConfig.MEDIA_URL + (String) result_map.get("voice_url"));
			PlayVoiceActivity.media_player.prepare();
			PlayVoiceActivity.media_player.start();
			PlayVoiceActivity.media_player.setOnCompletionListener(new PlayCompleteListener());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	class PlayCompleteListener implements MediaPlayer.OnCompletionListener {
		@Override
		public void onCompletion(MediaPlayer mp) {
			Log.i(TAG, PlayVoiceActivity.continue_play + "");
			if (PlayVoiceActivity.continue_play == false) {
				return ;
			}
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("user_token", TokenService.getToken(mContext));
			params.put("doing_id", mDoingId);
			CallAPIHandlerInterface play_voice_handler = new PlayVoiceHandler(mContext, new Handler(), mUsernameText, mDoingNameText, mDoingId, mUserPortrait);
			if (CurrentDoingService.current_lifelog_id != -1) {
				CallAPIAsyncTask play_voice_task = new CallAPIAsyncTask(play_voice_handler, SSSayConfig.HOST_URL + "voice/random_doing_voice", params);
				play_voice_task.execute();
			} else {
				CallAPIAsyncTask play_voice_task = new CallAPIAsyncTask(play_voice_handler, SSSayConfig.HOST_URL + "voice/random_all_voice", params);
				play_voice_task.execute();
			}
		}
	}
}
