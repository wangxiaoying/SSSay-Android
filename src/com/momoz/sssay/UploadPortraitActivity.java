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
		if (resultCode != RESULT_OK) { //�˴��� RESULT_OK ��ϵͳ�Զ����һ������ 
			Log.e(TAG,"ActivityResult resultCode error"); 
			return; 
		} 
		Bitmap bm = null; 
		ContentResolver resolver = getContentResolver(); 
		if (requestCode == IMAGE_CODE) { 
			try { 
				Uri originalUri = data.getData(); //���ͼƬ��uri 
				bm = MediaStore.Images.Media.getBitmap(resolver, originalUri); //�Եõ�bitmapͼƬ 
				//���ú�ѹ��
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
				// ���￪ʼ�ĵڶ����֣���ȡͼƬ��·���� 
				String[] proj = {MediaStore.Images.Media.DATA}; 
				Cursor cursor = managedQuery(originalUri, proj, null, null, null); 
				//���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ 
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
				cursor.moveToFirst(); 
				//����������ֵ��ȡͼƬ·�� 
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
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//����ѹ������������100��ʾ��ѹ������ѹ��������ݴ�ŵ�baos��  
        int options = 100;  
        while ( baos.toByteArray().length / 1024>100) {  //ѭ���ж����ѹ����ͼƬ�Ƿ����100kb,���ڼ���ѹ��         
            baos.reset();//����baos�����baos  
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//����ѹ��options%����ѹ��������ݴ�ŵ�baos��  
            options -= 10;//ÿ�ζ�����10  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//��ѹ���������baos��ŵ�ByteArrayInputStream��  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//��ByteArrayInputStream��������ͼƬ  
        return bitmap;  
    }
	
private Bitmap comp(Bitmap image) {  
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();         
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);  
        if( baos.toByteArray().length / 1024>1024) {//�ж����ͼƬ����1M,����ѹ������������ͼƬ��BitmapFactory.decodeStream��ʱ���    
            baos.reset();//����baos�����baos  
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//����ѹ��50%����ѹ��������ݴ�ŵ�baos��  
        }  
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());  
        BitmapFactory.Options newOpts = new BitmapFactory.Options();  
        //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��  
        newOpts.inJustDecodeBounds = true;  
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        newOpts.inJustDecodeBounds = false;  
        int w = newOpts.outWidth;  
        int h = newOpts.outHeight;  
        float hh = 100f;//�������ø߶�Ϊ100f  
        float ww = 100f;//�������ÿ��Ϊ100f  
        int be = 1;//be=1��ʾ������  
        if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����  
            be = (int) (newOpts.outWidth / ww);  
        } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����  
            be = (int) (newOpts.outHeight / hh);  
        }  
        if (be <= 0)  
            be = 1;  
        newOpts.inSampleSize = be;//�������ű���  
        //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��  
        isBm = new ByteArrayInputStream(baos.toByteArray());  
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);  
        return compressImage(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��  
    }
}
