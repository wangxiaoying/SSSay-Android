package com.momoz.sssay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.momoz.sssay.handlers.ChangePasswordHandler;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayTools;

public class ChangePasswordActivity extends Activity {
	
	private static final String TAG = "CHANGEPASSWORDACTIVITY";
	
	private EditText mOldPassword, mNewPassword, mConfirmPassword;
	private Button mChangeButton;
	private TextView mHint;
	private String old_password, new_password, confirm_password;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		
		getActionBar().setDisplayShowHomeEnabled(false);
		
		mOldPassword = (EditText)findViewById(R.id.change_passowrd_old);
		mNewPassword = (EditText)findViewById(R.id.change_password_new);
		mConfirmPassword = (EditText)findViewById(R.id.change_password_new_confirm);
		mChangeButton = (Button)findViewById(R.id.change_password_change);
		mHint = (TextView)findViewById(R.id.change_password_hint);
		
		
		mChangeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				old_password = mOldPassword.getText().toString();
				new_password = mNewPassword.getText().toString();
				confirm_password = mConfirmPassword.getText().toString();
				
				if(!checkInput(new_password, confirm_password)){
					mHint.setVisibility(View.VISIBLE);
					mHint.setText(getResources().getString(R.string.change_password_not_fit));
					return;
				}
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("user_token", TokenService.getToken(getApplicationContext()));
				params.put("old_password", SSSayTools.getMD5(old_password));
				params.put("new_password", SSSayTools.getMD5(new_password));
				
				List<EditText> ets = new ArrayList<EditText>();
				ets.add(mOldPassword);
				ets.add(mNewPassword);
				ets.add(mConfirmPassword);
				
				CallAPIHandlerInterface change_password_handler = new ChangePasswordHandler(getApplicationContext(), new Handler(), mHint, ets);
				CallAPIAsyncTask change_password_task = new CallAPIAsyncTask(change_password_handler, SSSayConfig.HOST_URL + "user/changepassword", params);
				change_password_task.execute();
			}
		});
		
		
		
		
	}
	
	private Boolean checkInput(String pass1, String pass2){
		if(!pass1.equals(pass2)){
			return false;
		}
		return true;
	}
	
}
