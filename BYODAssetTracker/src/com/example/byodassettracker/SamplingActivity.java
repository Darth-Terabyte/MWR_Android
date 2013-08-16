package com.example.byodassettracker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;

public class SamplingActivity extends FragmentActivity {
	
	private View SamplingStatusView;
	private boolean rooted;
	private String apps;
	private boolean debug;
	private boolean unknown;
	private boolean avg;
	private int os;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sampling);
		SamplingStatusView = findViewById(R.id.sampling_status);
		//showProgress(true);
		rooted = false;
		debug = false;
		unknown = false;
		avg = false;
		sample();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sampling, menu);
		return true;
	}
	
	
	
	
	
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
			
			SamplingStatusView.animate().setDuration(shortAnimTime)
				.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							SamplingStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
						}
					});

		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			SamplingStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
		}
	}
	
	public void sample()
	{		
	    checkDebug();
	    getApps();   	    
	    if (!rooted)
	    	checkRooted();
	    checkUnknown();
	    //System.out.println(debug + " " + rooted + " " + apps + " " + unknown);
	    //showProgress(false);
	    String host = "192.168.0.4";
	    
	    if (debug)
	    {
	    	DialogFragment df = new DebugDialog();
			df.show(getSupportFragmentManager(), "MyDF");
	    }
	    
	    
	    System.out.println("http://"+ host + ":8080/BYOD/scanResults?rooted="+ Boolean.toString(rooted) + "&debug="+Boolean.toString(debug)+"&unknown="+Boolean.toString(unknown)+"&apps="+apps + "&submit=Register");
	    String stringUrl = "http://"+ host + ":8080/BYOD/scanResults?rooted="+ rooted+ "&debug="+debug+"&unknown="+unknown+"&apps="+apps+"&submit=Register";
	    ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
           System.out.println("No network connection available.");
        }

	}
	
	public void checkRooted()
	{
	    
			File file = new File("/system/xbin/su");  
	        rooted = file.exists();  	                                  
	     
	}
	
	public void getApps()
	{
		PackageManager pm = this.getPackageManager();
	    Intent intent = new Intent(Intent.ACTION_MAIN, null);
	    intent.addCategory(Intent.CATEGORY_LAUNCHER);
	    List<ResolveInfo> list = pm.queryIntentActivities(intent,PackageManager.PERMISSION_GRANTED);
	    String str;
	    apps = "";
	    for (ResolveInfo rInfo : list) {
	        str = rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
	        apps += str + ",";
	    //results.add(rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
	        if (str.toLowerCase().equals("superuser"))
	        {
	        	rooted = true;
	        }      
	        else if (str.equals("AVG Antivirus"))
	        	avg = true;
	        		
	        //System.out.println(str + " in package " + rInfo.activityInfo.applicationInfo.packageName);
	    }
	    apps = apps.replaceAll(" ","_");
	    
        if (!avg)
        {
        	ScanDialog df = new ScanDialog();
    		df.show(getSupportFragmentManager(), "MyDF2");
        }
	    

	}
	
	
	public void checkDebug()
	{
		int allowed = 0;
		try {
			allowed = Settings.Secure.getInt(getContentResolver(), Settings.Secure.ADB_ENABLED);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (allowed == 1)
			debug = true;
	}
	
	public void checkUnknown()
	{
		int allowed = 0;
		try {
			allowed = Settings.Secure.getInt(getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (allowed == 1)
			unknown = true;
    	
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
	    // Only display the first 500 characters of the retrieved
	    // web page content.
	    int len = 500;
	        
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("POST");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        int response = conn.getResponseCode();
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
