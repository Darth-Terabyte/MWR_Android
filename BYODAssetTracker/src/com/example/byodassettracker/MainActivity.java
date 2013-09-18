package com.example.byodassettracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

public class MainActivity extends FragmentActivity {
	
	boolean isRegistered;
	boolean isWaiting;
	DeviceInfo device;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		

		//check status of device
		String host = "197.175.59.185";
	    String stringUrl = "http://"+ host + ":8080/BYOD/status";
	    ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
           System.out.println("No network connection available.");
        }
        if (!isRegistered)
		{
			Button button = (Button) findViewById(R.id.scan);		
			button.setVisibility(View.INVISIBLE);
		}
		else if (isRegistered)
		{
			Button button = (Button) findViewById(R.id.register);		
			button.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		String host = "197.175.59.185";
	    String stringUrl = "http://"+ host + ":8080/BYOD/status";
	    ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
           System.out.println("No network connection available.");
        }
        if (!isRegistered)
		{
			Button button = (Button) findViewById(R.id.scan);		
			button.setVisibility(View.INVISIBLE);
			Button button2 = (Button) findViewById(R.id.register);		
			button2.setVisibility(View.VISIBLE);
		}
		else if (isRegistered)
		{
			Button button = (Button) findViewById(R.id.register);		
			button.setVisibility(View.INVISIBLE);
			Button button2 = (Button) findViewById(R.id.scan);		
			button2.setVisibility(View.VISIBLE);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void register(View view)
	{				
		Intent intent = new Intent(this, RegistrationActivity.class);
		startActivity(intent);		
	}

	public void sample(View view)
	{
		Intent intent = new Intent(this, SamplingActivity.class);
		startActivity(intent);
	}

	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
              
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            System.out.println("result: " + result);
            if (result.startsWith("registered"))
            {
            	isRegistered = true;
            	isWaiting = false;
            }
            else if (result.startsWith("not registered"))
            {
            	isRegistered = false;
            	isWaiting = false;
            }
            else if (result.startsWith("waiting"))
            {
            	isRegistered = false;
            	isWaiting = true;
            }            	
       }
    }
	
	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
	    Reader reader = null;
	    reader = new InputStreamReader(stream, "UTF-8");        
	    char[] buffer = new char[len];
	    reader.read(buffer);
	    return new String(buffer);
    }
	
	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private String downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	    int len = 500;
	        
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("POST");
	        conn.setDoInput(true);
	        conn.setRequestProperty("content-type","application/json; charset=utf-8"); 
	        conn.connect();
	        
	        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	        device = new DeviceInfo(this);
        	JSONObject data = new JSONObject();
 	        try {
     	        //data.put("mac",device.getMACAddress());
 	        	data.put("mac","mac");
     	        data.put("serial", device.getSerialNumber());
     	        data.put("android",device.getAndroidID());
     	        
			} catch (JSONException e) {
				e.printStackTrace();
			}
 	        
 	        wr.write(data.toString());
 	        wr.flush();
	        int response = conn.getResponseCode();
	        System.out.println(response);
	        //Log.d(DEBUG_TAG, "The response is: " + response);
	        is = conn.getInputStream();

	        // Convert the InputStream into a string
	        String contentAsString = readIt(is, len);
	        return contentAsString;
	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}

	


}
