package com.momoz.sssay;

import java.util.HashMap;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.momoz.sssay.handlers.AllDoingHandler;
import com.momoz.sssay.handlers.LogoutHandler;
import com.momoz.sssay.services.AllDoingService;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;

public class AllDoingActivity extends Activity {

	private static final String TAG = "ALLDOINGACTIVITY";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_all_doing);
		
		getActionBar().setDisplayShowHomeEnabled(false);

		setupActionBar();
//		setupComponents();
	}
	
	@Override
	public void onResume(){
		setupComponents();
		super.onResume();
	}
	
	private void setupComponents() {
		ExpandableListView list_view = (ExpandableListView) findViewById(R.id.all_doing_listview);

		if (AllDoingService.all_category != null && AllDoingService.all_category.size() > 0) {
			AllDoingService.refreshAllDoing(getApplicationContext(), new Handler(), list_view);
		}

		// get the request ready
		Log.i(TAG, "token is " + TokenService.getToken(getApplicationContext()));
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_token", TokenService.getToken(getApplicationContext()));
		CallAPIHandlerInterface all_doing_handler = new AllDoingHandler(getApplicationContext(),
				new Handler(), list_view);
		CallAPIAsyncTask all_doing_task = new CallAPIAsyncTask(all_doing_handler, SSSayConfig.HOST_URL
				+ "doing/getalldoing", params);
		all_doing_task.execute();
		
		// set up onChildItemClick
		list_view.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent_view, View v, int group_pos,
					int child_pos, long row_id) {
				try {
					String category_name = AllDoingService.all_category.get(group_pos);
					String doing_full_name = AllDoingService.all_doing.get(category_name).get(child_pos);
					String doing_id = doing_full_name.split(";")[0];
					String doing_name = doing_full_name.split(";")[1];

					Intent intent = new Intent(AllDoingActivity.this, CurrentDoingActivity.class);
					intent.putExtra(SSSayConfig.EXTRA_CURRENT_DOING_ID, doing_id);
					intent.putExtra(SSSayConfig.EXTRA_CURRENT_DOING_NAME, doing_name);
					startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return false;
			}
		});
	}

	private void setupActionBar() {
		// setup action bar for tabs
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setIcon(R.drawable.invisible_icon);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_bar_all_doing, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
		case R.id.all_doing_action_person:
			Intent intent = new Intent(AllDoingActivity.this, UserMainPageActivity.class);
			intent.putExtra(SSSayConfig.EXTRA_IS_MYSELF, true);
			intent.putExtra("user_id", TokenService.getUserId(getApplicationContext()));
			startActivity(intent);
			return true;
		case R.id.all_doing_action_logout:
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("user_id", TokenService.getUserId(getApplicationContext()));
			CallAPIHandlerInterface logout_handler = new LogoutHandler(getApplicationContext(), new Handler());
			CallAPIAsyncTask logout_task = new CallAPIAsyncTask(logout_handler, SSSayConfig.HOST_URL+"user/logout", params);
			logout_task.execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onBackPressed(){
		
	}
	
}
