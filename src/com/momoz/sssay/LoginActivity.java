package com.momoz.sssay;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.momoz.sssay.handlers.LoginHandler;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayTools;

public class LoginActivity extends Activity {

	private final static String TAG = "LoginActivity";
	
	private EditText mUsername, mPassword;
	private Button mLoginButton;
	private TextView mHintText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		getActionBar().setDisplayShowHomeEnabled(false);
				
		setupComponents();
		testToken();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar_login, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	     switch (item.getItemId()) {
	        case android.R.id.home:
	            this.finish();
	        case R.id.login_action_try:
	        	Intent intent = new Intent(LoginActivity.this, PlayVoiceActivity.class);
	        	startActivity(intent);
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	// if there is token already, then goto AllDoingActivity
	private void testToken() {
		String token = TokenService.getToken(getApplicationContext());
		if (token != null) {
			Intent intent = new Intent(LoginActivity.this, AllDoingActivity.class);
			startActivity(intent);
			finish();
		}
	}
	
	private void setupComponents() {
		mUsername = (EditText) findViewById(R.id.login_edit_username);
		mPassword = (EditText) findViewById(R.id.login_edit_password);
		mLoginButton = (Button) findViewById(R.id.login_button_submit);
		mHintText = (TextView) findViewById(R.id.login_text_hint);
		
		mLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = mUsername.getText().toString();
				String password = mPassword.getText().toString();
				
				if (username == "" || password == "") {
					mHintText.setText(getResources().getString(R.string.login_hint_illegal));
					mHintText.setVisibility(View.VISIBLE);
				} else {
					mHintText.setVisibility(View.INVISIBLE);
				}
				
				// send the request
				HashMap<String,String> params = new HashMap<String, String>();
				params.put("username", username);
				params.put("password", SSSayTools.getMD5(password));
				CallAPIHandlerInterface login_handler = new LoginHandler(getApplicationContext(), new Handler(), mHintText);
				CallAPIAsyncTask login_task = new CallAPIAsyncTask(login_handler, SSSayConfig.HOST_URL+"user/login", params);
				login_task.execute();
			}
		});
	}
}
