package com.momoz.sssay.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

import org.json.simple.JSONValue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.momoz.sssay.services.ImageService;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayMessage;

public class GetPortraitHandler implements CallAPIHandlerInterface {

	private final static String TAG = "GetPortraitHandler";

	private Context mContext;
	private Handler mHandler;
	private ImageView mIv, mBg;
	private Map mResponseData;

	public GetPortraitHandler(Context context, Handler handler, ImageView iv, ImageView bg) {
		mContext = context;
		mHandler = handler;
		mIv = iv;
		mBg = bg;
	}

	@Override
	public void handleResponse(String str) {

		try {
			Log.i(TAG, "return: " + str);

			if (str == null) {
				return;
			}

			Map a_temp = (Map) JSONValue.parse(str);
			mResponseData = a_temp;
			final String result = (String) a_temp.get("result");

			if (result.equals(SSSayMessage.Success)) {

				String portrait_url = (String) a_temp.get("portrait_url");
				new DownloadImage().execute(portrait_url);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	class DownloadImage extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			
			Bitmap bm = null;
					
			try {
				String portrait_url = params[0];
				
				Log.i(TAG, "async task url " + portrait_url);

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
					Log.i(TAG, "response ok");
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
					Bitmap blur_bmp = ImageService.fastblur(bm, 7);
					mBg.setImageBitmap(blur_bmp);
				}
			} else {
				Log.i(TAG, "bitmap null 555");
			}
		}
	}
}
