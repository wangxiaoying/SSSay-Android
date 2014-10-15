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

import com.momoz.sssay.handlers.RegisterHandler;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayTools;

public class RegisterActivity extends Activity {

	private final static String TAG = "RegisterActivity";

	private EditText mUsername, mPassword, mConfirm;
	private Button mRegisterButton;
	private Button mGoLoginButton;
	private TextView mHintText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		getActionBar().setDisplayShowHomeEnabled(false);

		setupComponents();
		testToken();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.register_action_try:
			Intent intent = new Intent(RegisterActivity.this,
					PlayVoiceActivity.class);
			startActivity(intent);
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// if there is token already, then goto AllDoingActivity
	private void testToken() {
		String token = TokenService.getToken(getApplicationContext());
		if (token != null) {
			Intent intent = new Intent(RegisterActivity.this,
					AllDoingActivity.class);
			startActivity(intent);
			finish();
		}
	}

	private void setupComponents() {
		mUsername = (EditText) findViewById(R.id.register_edit_username);
		mPassword = (EditText) findViewById(R.id.register_edit_password);
		mConfirm = (EditText) findViewById(R.id.register_edit_password_confirm);
		mRegisterButton = (Button) findViewById(R.id.register_button_submit);
		mGoLoginButton = (Button) findViewById(R.id.register_button_go_login);
		mHintText = (TextView) findViewById(R.id.register_text_hint);

		mRegisterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String username = mUsername.getText().toString();
				String password = mPassword.getText().toString();
				String confirm = mConfirm.getText().toString();

				// TODO: need more detailed judge and hints
				// empty username or dismatch password
				if (username.equals("") || password.equals("")
						|| !password.equals(confirm)) {
					mHintText.setText(getResources().getString(
							R.string.register_hint_illegal));
					mHintText.setVisibility(View.VISIBLE);
					return;
				} else {
					mHintText.setVisibility(View.INVISIBLE);
				}

				// send the request
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("username", username);
				params.put("password", SSSayTools.getMD5(password));
				CallAPIHandlerInterface register_handler = new RegisterHandler(
						getApplicationContext(), new Handler(), mHintText);
				CallAPIAsyncTask register_task = new CallAPIAsyncTask(
						register_handler, SSSayConfig.HOST_URL
								+ "user/register", params);
				register_task.execute();
			}
		});

		mGoLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this,
						LoginActivity.class);
				// intent.putExtra("FLAG", SSSayConfig.FLAG_RETURN);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		try {
			String Flag = this.getIntent().getExtras().getString("FLAG");
			if (Flag.equals(SSSayConfig.FLAG_RETURN)) {
				super.onBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
