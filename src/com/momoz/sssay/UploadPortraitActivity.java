package com.momoz.sssay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.momoz.sssay.handlers.GetPortraitHandler;
import com.momoz.sssay.services.TokenService;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.UploadPortraitAsyncTask;

public class UploadPortraitActivity extends Activity {

	private final static String TAG = "UploadPortraitActivity";
	
	private Button mUploadButton;
	private ImageView mPortrait;
	
	private final String IMAGE_TYPE = "image/*";
	private final int IMAGE_CODE = 0;

	private File saveCatalog;
	private File saveFile;
	String path=Environment.getExternalStorageDirectory().getPath()+"/SSSay"; 
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_upload_portrait);
		
		getActionBar().setDisplayShowHomeEnabled(false);

		mUploadButton = (Button)findViewById(R.id.uploadportrait_btn);
		mPortrait = (ImageView)findViewById(R.id.uploadportrait_image);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_id", TokenService.getUserId(getApplicationContext()));
		
		CallAPIHandlerInterface get_portrait_handler = new GetPortraitHandler(getApplicationContext(), new Handler(), mPortrait, null);
		CallAPIAsyncTask get_portrait_task = new CallAPIAsyncTask(get_portrait_handler, SSSayConfig.HOST_URL+"user/getportraiturl", params);
		get_portrait_task.execute();
		
		mUploadButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT); 
				getAlbum.setType(IMAGE_TYPE); 
				startActivityForResult(getAlbum, IMAGE_CODE); 
			}
		});
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
				
				/*
				// 这里开始的第二部分，获取图片的路径： 
				String[] proj = {MediaStore.Images.Media.DATA}; 
				Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
				//按我个人理解 这个是获得用户选择的图片的索引值 
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
				cursor.moveToFirst(); 
				//最后根据索引值获取图片路径 
				String path = cursor.getString(column_index); 
				Log.e("Lostinai",path); 
				*/
				
				String filePath = path + "/portrait.jpg";
				Log.i("file path:", filePath);
				
				new UploadPortraitAsyncTask().execute(SSSayConfig.HOST_URL+"user/uploadportrait", filePath, TokenService.getToken(getApplicationContext()));
				mPortrait.setImageBitmap(bm);

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
        float hh = 100f;//这里设置高度为100f  
        float ww = 100f;//这里设置宽度为100f  
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
