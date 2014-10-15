package com.momoz.sssay.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;

public class CallAPIAsyncTask {

	private CallAPIHandlerInterface mAPIHandler;
	private String mRequestURL;
	private HashMap<String, String> mRequestParameters;

	public CallAPIAsyncTask(CallAPIHandlerInterface api_handler, String api_url, HashMap<String, String> api_params) {
		mAPIHandler = api_handler;
		mRequestURL = api_url;
		mRequestParameters = api_params;
	}
	
	public void execute() {
		new ActualAsyncTask().execute(mRequestURL);
	}

	class ActualAsyncTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... url) {
			try {
				// construct the http client, preparing for connection
				HttpClient http_client = new DefaultHttpClient();
				HttpPost http_post = new HttpPost(mRequestURL);

				// set post parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				Iterator it = mRequestParameters.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry pair = (Map.Entry) it.next();
					params.add(new BasicNameValuePair((String)pair.getKey(), (String)pair.getValue()));
				}
				http_post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
				
				// send the post!
				HttpResponse http_response = http_client.execute(http_post);
				HttpEntity http_entity = http_response.getEntity();

				return EntityUtils.toString(http_entity);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		@Override
		protected void onPostExecute(String response_string) {
			mAPIHandler.handleResponse(response_string);
		}
	}
}
