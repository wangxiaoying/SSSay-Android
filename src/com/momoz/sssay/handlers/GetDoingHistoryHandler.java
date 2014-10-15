package com.momoz.sssay.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.momoz.sssay.R;
import com.momoz.sssay.services.ImageService;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayMessage;

public class GetDoingHistoryHandler implements CallAPIHandlerInterface {

	private final static String TAG = "GetProfileHandler";

	private Context mContext;
	private Handler mHandler;
	private ListView mLv;
	
	private List<Map<String, Object>> mList;

	public GetDoingHistoryHandler(Context context, Handler handler, ListView lv) {
		mContext = context;
		mHandler = handler;
		mLv = lv;
	}

	@Override
	public void handleResponse(String str) {
		Log.i(TAG, "return: " + str);
		if(str == null){
			return;
		}
		
		try{
			Map result_map = (Map) JSONValue.parse(str);
			final String result = (String) result_map.get("result");

			if (result != null) {
				Log.i(TAG, result);
				if (result.equals(SSSayMessage.InvalidToken)) {
					// TODO: need to relogin
				}
				return;
			}
			
		}catch(Exception e){
			JSONArray a_temp = (JSONArray)JSONValue.parse(str);
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			mList = list;
			
			int length = a_temp.size();
			for (int i=0; i<length; ++i) {
				Map item = (Map)a_temp.get(i);
				list.add(item);
			}
			
			SimpleAdapter adapter = new SimpleAdapter(mContext, list, R.layout.list_item_history_doing, 
					new String[]{"doing", "start_time", "end_time", "category"}, 
					new int[]{R.id.doing_his_lv_doing, R.id.doing_his_lv_starttime, R.id.doing_his_lv_endtime, R.id.doing_category});
			mLv.setAdapter(adapter);
		}
		
		mLv.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(int i = 0; i < mLv.getChildCount(); ++i){
					View v = mLv.getChildAt(i);
					ImageView iv = (ImageView)v.findViewById(R.id.doing_category_icon);
					String url = "icons/" + mList.get(i).get("category").toString() + ".png";
					new DownloadImage(iv, null).execute(url);
				}
			}
		});
		
	}
	
	class DownloadImage extends AsyncTask<String, Void, Bitmap> {
		
		private ImageView mIv, mBg;

		public DownloadImage(ImageView mIv, ImageView mBg) {
			super();
			this.mIv = mIv;
			this.mBg = mBg;
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			
			Bitmap bm = null;
					
			try {
				String portrait_url = params[0];
				
//				Log.i(TAG, "async task url " + portrait_url);

				InputStream in = null;
				URL url = new URL(SSSayConfig.MEDIA_URL + portrait_url);
				Log.i("url:", url.toString());
				URLConnection conn = url.openConnection();
				if (!(conn instanceof HttpURLConnection))
					throw new IOException("Not an http connection");
				HttpURLConnection httpConn = (HttpURLConnection) conn;

				httpConn.connect();

				int response = -1;
				response = httpConn.getResponseCode();
				if (response == HttpURLConnection.HTTP_OK) {
					in = httpConn.getInputStream();
					bm = BitmapFactory.decodeStream(in);
//					Log.i(TAG, "response ok");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap bm) {
			if (bm != null) {
				Bitmap round_bmp = ImageService.getCroppedBitmap(bm, bm.getWidth());
				mIv.setImageBitmap(round_bmp);
				if (mBg != null) {
					Bitmap blur_bmp = ImageService.fastblur(bm, 5);
					mBg.setImageBitmap(blur_bmp);
				}
			} else {
				Log.i("lala", "bitmap null 555");
			}
		}
	}
}
