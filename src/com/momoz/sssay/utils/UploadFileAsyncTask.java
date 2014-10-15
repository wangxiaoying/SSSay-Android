package com.momoz.sssay.utils;

import java.io.File;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

import com.momoz.sssay.services.CurrentDoingService;
import com.momoz.sssay.services.TokenService;

/**
 * 
 * @author zijing
 * 
 * Usage: new UploadFileAsyncTask().execute(str_web_url, str_abs_file_path, user_token);
 */
public class UploadFileAsyncTask extends AsyncTask<String, Void, String> {

	private final static String TAG = "UploadFileUtils";

	protected String doInBackground(String... urls) {
		try {
			String server_url = urls[0];
			String file_path = urls[1];
			String token = urls[2];
			
			// send the upload request!
			String result = post(file_path, server_url, token);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected void onPostExecute(String result) {
		// TODO: check invalid token
		Log.i(TAG, "" + result);
	}

	private String post(String pathToOurFile, String urlServer, String token) throws ClientProtocolException, IOException, JSONException {
		HttpClient httpclient = new DefaultHttpClient();

		httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		HttpPost httppost = new HttpPost(urlServer);
		File file = new File(pathToOurFile);

		MultipartEntity mpEntity = new MultipartEntity();
		ContentBody cbFile = new FileBody(file);
		mpEntity.addPart("voicefile", cbFile); // <input type="file" name="voicefile" />
		mpEntity.addPart("user_token", new StringBody(token));
		mpEntity.addPart("lifelog_id", new StringBody(CurrentDoingService.current_lifelog_id.toString()));

		httppost.setEntity(mpEntity);
		Log.i(TAG, "executing request " + httppost.getRequestLine());

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();

		Log.i(TAG, "" + response.getStatusLine()); // connectino valid
		
		httpclient.getConnectionManager().shutdown();
		return EntityUtils.toString(resEntity);
	}
}