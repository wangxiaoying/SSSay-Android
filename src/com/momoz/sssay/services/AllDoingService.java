package com.momoz.sssay.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.momoz.sssay.R;
import com.momoz.sssay.handlers.AllDoingHandler;
import com.momoz.sssay.utils.CallAPIAsyncTask;
import com.momoz.sssay.utils.CallAPIHandlerInterface;
import com.momoz.sssay.utils.SSSayConfig;
import com.momoz.sssay.utils.SSSayTools;

public class AllDoingService {

	private static final String TAG = "AllDoingService";
	
	private static ImageView mIv;

	private static ImageView mBg;

	/**
	 * Categories are stored like: cat1,cat2,... 
	 * Doings are stored like id1;doing1;people_count1,id2;doing2;people_count2,... with the key of belonging category
	 * 
	 * @param context
	 * @param doing_data
	 */
	public static void saveAllDoing(Context context, String doing_data) {
		try {
			JSONArray all_array = (JSONArray) JSONValue.parse(doing_data);
			String all_category = "";
			for (int i = 0; i < all_array.size(); ++i) {
				JSONObject category = (JSONObject) all_array.get(i);
				Set keys = category.keySet();

				String current_category = "";
				// get category
				Iterator it = keys.iterator();
				while (it.hasNext()) {
					if (!all_category.equals(""))
						all_category += ",";
					current_category = (String) it.next();
					all_category += current_category;
				}

				// all doings under specific category
				JSONArray category_doings = (JSONArray) category.get(current_category);
				String all_doings = "";
				for (int k = 0; k < category_doings.size(); ++k) {
					if (k > 0)
						all_doings += ",";
					all_doings += (String) category_doings.get(k);
				}
				// Log.d(TAG, current_category + " : " + all_doings);
				SSSayTools.savePreference(context, current_category, all_doings);
			}
			// Log.d(TAG, "category list " + all_category);
			SSSayTools.savePreference(context, SSSayConfig.PREF_KEY_CATEGORY, all_category);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: response format wrong
		}
	}

	public static void getAllDoing(Context context, Handler handler, ExpandableListView listview) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("user_token", TokenService.getToken(context));
		CallAPIHandlerInterface all_doing_handler = new AllDoingHandler(context, handler, listview);
		CallAPIAsyncTask all_doing_task = new CallAPIAsyncTask(all_doing_handler, SSSayConfig.HOST_URL
				+ "doing/getalldoing", params);
		all_doing_task.execute();
	}

	public static List<String> all_category;
	public static HashMap<String, List<String>> all_doing;

	public static void refreshAllDoing(Context context, Handler handler, final ExpandableListView listview) {
		try {
			all_category = new ArrayList<String>();
			all_doing = new HashMap<String, List<String>>();

			String[] category_array = SSSayTools.getPreference(context, SSSayConfig.PREF_KEY_CATEGORY).split(
					",");
			for (int i = 0; i < category_array.length; ++i) {
				all_category.add(category_array[i]);

				List<String> doing_list = new ArrayList<String>();
				String[] doing_array = SSSayTools.getPreference(context, category_array[i]).split(",");
				for (int j = 0; j < doing_array.length; ++j) {
					doing_list.add(doing_array[j]);
				}

				all_doing.put(category_array[i], doing_list);
			}

			ExpandableListAdapter adapter = new ExpandableListAdapter(context, all_category, all_doing);
			listview.setAdapter(adapter);
			
//			Log.i("Categoty size", all_category.size()+"");
//			Log.i("Category",listview.getChildCount()+"");
//			for(int i = 0; i < all_category.size(); ++i){
//				View v = listview.getChildAt(i);
////				mIv = (ImageView)v.findViewById(R.id.doing_category_icon);
////				String url = all_category.get(i)
//				Log.i("icon name!", all_category.get(i).toString());
//				
//			}
			
			listview.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Log.i("Category",listview.getChildCount()+"");
					for(int i = 0; i < listview.getChildCount(); ++i){
						View v = listview.getChildAt(i);
						mIv = (ImageView)v.findViewById(R.id.doing_category_icon);
						String url = "icons/"+all_category.get(i).toString()+".png";
						new DownloadImage(mIv, mBg).execute(url);
					}
				}
			});
	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
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
			
//			Log.i(TAG, "async task url " + portrait_url);

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
//				Log.i(TAG, "response ok");
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
