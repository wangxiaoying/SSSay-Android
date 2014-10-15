package com.momoz.sssay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.momoz.sssay.handlers.FollowHandler;
import com.momoz.sssay.handlers.GetFollowRelationHandler;
import com.momoz.sssay.handlers.GetPortraitHandler;
import com.momoz.sssay.handlers.GetProfileHandler;
import com.momoz.sssay.handlers.LogoutHandler;
import com.momoz.sssay.handlers.UnfollowHandler;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.UploadPortraitAsyncTask;

public class UserMainPageActivity extends Activity {

	private final static String TAG = "UserMainPageActivity";

	private TextView mUsernameText, mFollowingNumText, mFollowedNumText, mDoingNumText;
	private Button mRelationBtn;
	private ImageView mUserPortrait, mPortraitBackground;
	private String user_id;
	private ArrayList<TextView> mTvs;
	
	
	private final String IMAGE_TYPE = "image/*";
	private final int IMAGE_CODE = 0;

	private File saveCatalog;
	private File saveFile;
	String path=Environment.getExternalStorageDirectory().getPath()+"/SSSay"; 
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_main_page);

		getActionBar().setDisplayShowHomeEnabled(false);
		
		setupComponents();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.action_bar_user_main_page, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.user_main_action_logout:
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("user_id", TokenService.getUserId(getApplicationContext()));
			
			CallAPIHandlerInterface logout_handler = new LogoutHandler(getApplicationContext(), new Handler());
			CallAPIAsyncTask logout_task = new CallAPIAsyncTask(logout_handler, SSSayConfig.HOST_URL+"user/logout", params);
			logout_task.execute();
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setupComponents() {

		mUsernameText = (TextView) findViewById(R.id.user_main_page_username);
		mFollowingNumText = (TextView) findViewById(R.id.user_main_page_following);
		mFollowedNumText = (TextView) findViewById(R.id.user_main_page_followed);
		mDoingNumText = (TextView) findViewById(R.id.user_main_page_doing);
		mRelationBtn = (Button) findViewById(R.id.user_main_page_btn_relation);
		
		mUserPortrait = (ImageView) findViewById(R.id.user_avatar);
		
		user_id = this.getIntent().getExtras().getString("user_id");
		
		
		(findViewById(R.id.user_main_page_ll_following)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserMainPageActivity.this, GetFollowingActivity.class);
				intent.putExtra("user_id", user_id);
				startActivity(intent);
			}
		});
		
		(findViewById(R.id.user_main_page_ll_followed)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserMainPageActivity.this, GetFollowedActivity.class);
				intent.putExtra("user_id", user_id);
				startActivity(intent);
			}
		});
		
		(findViewById(R.id.user_main_page_ll_records)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(UserMainPageActivity.this, DoingHistoryActivity.class);
				intent.putExtra("user_id", user_id);
				startActivity(intent);
			}
		});
		
		//TODO: goto all my records page


		mTvs = new ArrayList<TextView>();
		mTvs.add(mUsernameText);
		mTvs.add(mFollowingNumText);
		mTvs.add(mFollowedNumText);
		mTvs.add(mDoingNumText);
		
		mRelationBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				final HashMap<String,String> params = new HashMap<String, String>();
				params.put("user_fan_token", TokenService.getToken(getApplicationContext()));
				params.put("user_hero_id", user_id);
				
				String text = mRelationBtn.getText().toString();
				if(text.equals("Change Password")){
					Intent intent = new Intent(UserMainPageActivity.this, ChangePasswordActivity.class);
					startActivity(intent);
					
				}else if(text.equals("Unfollow")){
					CallAPIHandlerInterface unfollow_handler = new UnfollowHandler(getApplicationContext(), new Handler(), mRelationBtn, user_id);
					CallAPIAsyncTask unfollow_task = new CallAPIAsyncTask(unfollow_handler, SSSayConfig.HOST_URL+"user/unfollow", params);
					unfollow_task.execute();
				}else if(text.equals("Follow+")){
					CallAPIHandlerInterface follow_handler = new FollowHandler(getApplicationContext(), new Handler(), mRelationBtn, user_id);
					CallAPIAsyncTask follow_task = new CallAPIAsyncTask(follow_handler, SSSayConfig.HOST_URL + "user/follow", params);
					follow_task.execute();
				}
				
			}
		});
		
		mUserPortrait.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT); 
				getAlbum.setType(IMAGE_TYPE); 
				startActivityForResult(getAlbum, IMAGE_CODE);
			}
		});

		// send the request
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_id", user_id);
		params.put("user_token", TokenService.getToken(getApplicationContext()));

		// get the relation between two users
		CallAPIHandlerInterface get_relation_handler = new GetFollowRelationHandler(getApplicationContext(),
				new Handler(), mRelationBtn, user_id);
		CallAPIAsyncTask get_relation_task = new CallAPIAsyncTask(get_relation_handler, SSSayConfig.HOST_URL
				+ "user/getfollowrelation", params);
		get_relation_task.execute();

	}

	@Override
	public void onResume() {

		HashMap<String, String> params = new HashMap<String, String>();
		// String token = TokenService.getToken(getApplicationContext());
		params.put("user_id", user_id);
		params.put("user_token", TokenService.getToken(getApplicationContext()));

		// get the portrait
		mUserPortrait = (ImageView) findViewById(R.id.user_avatar);
		mPortraitBackground = (ImageView) findViewById(R.id.user_avatar_bg);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
//		System.out.println("heigth : " + dm.heightPixels);
//		System.out.println("width : " + dm.widthPixels);
		RelativeLayout rl = (RelativeLayout)findViewById(R.id.user_main_page_group_portrat);
		rl.getLayoutParams().height = (int)Math.round(dm.heightPixels*0.55);
		
		CallAPIHandlerInterface get_portrait_handler = new GetPortraitHandler(getApplicationContext(),
				new Handler(), mUserPortrait, mPortraitBackground);
		CallAPIAsyncTask get_portrait_task = new CallAPIAsyncTask(get_portrait_handler, SSSayConfig.HOST_URL
				+ "user/getportraiturl", params);
		get_portrait_task.execute();

		// get all profiles
		CallAPIHandlerInterface get_profile_handler = new GetProfileHandler(getApplicationContext(),
				new Handler(), mTvs);
		CallAPIAsyncTask get_profile_task = new CallAPIAsyncTask(get_profile_handler, SSSayConfig.HOST_URL
				+ "user/getprofile", params);
		get_profile_task.execute();

		super.onResume();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){ 
		if (resultCode != RESULT_OK) { //此处的 RESULT_OK 是系统自定义得一个常量 
			Log.e(TAG,"ActivityResult resultCode error"); 
			return; 
		} 
		Bitmap bm = null; 
		ContentResolver resolver = getContentResolver(); 
		if (requestCode == IMAGE_CODE) { 
			try { 
				Uri originalUri = data.getData(); //获得图片的uri 
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); //显得到bitmap图片 
				//剪裁和压缩
				bm = comp(bm);
				bm = compressImage(bm);
				
				saveCatalog=new File(path);
				if(!saveCatalog.exists()){
					saveCatalog.mkdirs();
				}
				
				saveFile = new File(saveCatalog, "portrait.jpg");
				if(!saveFile.exists()){
					saveFile.createNewFile();
				}
				
				FileOutputStream fOut = null;
				fOut = new FileOutputStream(saveFile);
				bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
				fOut.flush();
				fOut.close();
				
				String filePath = path + "/portrait.jpg";
				Log.i("file path:", filePath);
				
				new UploadPortraitAsyncTask().execute(SSSayConfig.HOST_URL+"user/uploadportrait", filePath, TokenService.getToken(getApplicationContext()));
				mUserPortrait.setImageBitmap(bm);

			}catch (IOException e) { 
				Log.e("Lostinai",e.toString()); 
			} 	
		}
	}
	
	private Bitmap compressImage(Bitmap image) {  
		  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();  
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩         
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中  
            options -= 10;//每次都减少10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片  
        return bitmap;  
    }
	
	private Bitmap comp(Bitmap image) {  
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();         
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
        if( baos.toByteArray().length / 1024>1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出    
            baos.reset();//重置baos即清空baos  
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        float hh = 200f;//这里设置高度为200f  
        float ww = 200f;//这里设置宽度为200f  
        int be = 1;//be=1表示不缩放  
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;//设置缩放比例  
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了  
        isBm = new ByteArrayInputStream(baos.toByteArray());  
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩  
    }
	

}
