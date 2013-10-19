package com.example.byodassettracker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
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
import android.content.res.AssetManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class SamplingActivity extends FragmentActivity {
	
	private View SamplingStatusView;
	private DeviceInfo device;
	DatabaseHandler db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sampling);
		SamplingStatusView = findViewById(R.id.sampling_status);
		sample();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
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
		
		device = new DeviceInfo(this);
		device.scan();
		db = new DatabaseHandler(this);
		System.out.println(device.getRooted() + " " + device.getDebug() + " " + device.getUnknownSourcesAllowed() + " " + device.getAPILevel() + " " +  device.getApps().toString() + " " + device.getMACAddress() + " " +  device.getSerialNumber() + " " + device.getAndroidID());
		
	    String host = "www.mwr.com";
	    String stringUrl = "https://"+ host + ":8181/BYOD/scanResults";
	    ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask(this).execute(stringUrl);
        } else {
           System.out.println("No network connection available.");
        }
        

	}
	
	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		
		FragmentActivity activity;
		
		public DownloadWebpageTask(FragmentActivity act)
		{
			activity = act;
		}
		
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
            if (result.startsWith("allowed"))
            {
            	Intent intent = new Intent(activity, LoginActivity.class);
        		startActivity(intent);		
            }
            else if (result.startsWith("denied"))
            {
            	Intent intent = new Intent(activity, ScanResults.class);     		
        	    intent.putExtra("result", result);
        	    startActivity(intent);

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
	        String contentAsString = "";
	    try {
	    	try
	    	{
	    	CertificateFactory cf = CertificateFactory.getInstance("X.509");
			// From https://www.washington.edu/itconnect/security/ca/load-der.crt
			AssetManager assetManager = getAssets();
			InputStream caInput = assetManager.open("mwr.cer");		
			Certificate ca;
			try {
			    ca = cf.generateCertificate(caInput);
			    System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
			} finally {
			    caInput.close();
			}		
			// Create a KeyStore containing our trusted CAs
			String keyStoreType = KeyStore.getDefaultType();
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("s1as", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
			tmf.init(keyStore);

			// Create an SSLContext that uses our TrustManager
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);

			// Tell the URLConnection to use a SocketFactory from our SSLContext
			URL url = new URL(myurl);
			HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			conn.setSSLSocketFactory(context.getSocketFactory());
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("POST");
	        conn.setDoInput(true);
	        conn.setDoOutput(true);
	        conn.setRequestProperty("content-type","application/json; charset=utf-8"); 
	        conn.connect();
	        
	        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	        
        	JSONObject data = new JSONObject();
 	        try {
 	        	data.put("rooted", device.getRooted());
     	        data.put("debug", device.getDebug());
     	        data.put("unknown", device.getUnknownSourcesAllowed());
     	        data.put("os", device.getAPILevel());
     	        data.put("apps", device.getApps().toString());
     	        data.put("mac",device.getMACAddress());
     	        data.put("serial", device.getSerialNumber());
     	        data.put("android",device.getAndroidID());
     	        data.put("password",db.getPassword(1));
     	        
			} catch (JSONException e) {
				e.printStackTrace();
			}
 	        
 	        wr.write(data.toString());
 	        wr.flush();
	        int response = conn.getResponseCode();
	        System.out.println(response);
	        //Log.d("BYOD", "The response is: " + response);
	        is = conn.getInputStream();

	        // Convert the InputStream into a string
	        contentAsString = readIt(is, len);
	    	}
	    	catch (Exception e)
	    	{
	    		StringWriter sw = new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				String exceptionAsString = sw.toString();
				DialogFragment df = new TokenDialog();
				df.show(getSupportFragmentManager(), "MyDF");
				Bundle args = new Bundle();
				args.putString("token", exceptionAsString);
				df.setArguments(args);
				// TODO Auto-generated catch block
				e.printStackTrace();
	    	}
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
