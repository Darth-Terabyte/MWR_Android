package com.example.byodassettracker;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import java.security.cert.X509Certificate;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

public class MainActivity extends FragmentActivity {
	
	boolean isRegistered;
	boolean isWaiting;
	DeviceInfo device;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		

		
		try{

		//check status of device
		String host = "www.mwr.com";
	    String stringUrl = "https://"+ host + ":8181/BYOD/status";
	    ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Button button = (Button) findViewById(R.id.scan);		
		button.setVisibility(View.INVISIBLE);
		button = (Button) findViewById(R.id.register);		
		button.setVisibility(View.INVISIBLE);
		button = (Button) findViewById(R.id.token);		
		button.setVisibility(View.INVISIBLE);
		button = (Button) findViewById(R.id.logout);		
		button.setVisibility(View.INVISIBLE);
        
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
           System.out.println("No network connection available.");
        }

		}
		catch (Exception e){			
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		DialogFragment df = new TokenDialog();
		df.show(getSupportFragmentManager(), "MyDF");
		Bundle args = new Bundle();
		args.putString("token", exceptionAsString);
		df.setArguments(args);
	}
	}
	
	@Override
    public void onBackPressed() {
		Intent main = new Intent(Intent.ACTION_MAIN);
		main.addCategory(Intent.CATEGORY_HOME);
		main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(main);	
    }
	
    private static void copyInputStreamToOutputStream(InputStream in, PrintStream out) throws IOException {
   //To change body of generated methods, choose Tools | Templates.
        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
             out.write(buffer, 0, len);
        }
}
    
    
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		try{

		//check status of device
		String host = "www.mwr.com";
	    String stringUrl = "https://"+ host + ":8181/BYOD/status";
	    ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
           System.out.println("No network connection available.");
        }
        Button button = (Button) findViewById(R.id.scan);		
        button.setVisibility(View.INVISIBLE);
		button = (Button) findViewById(R.id.register);		
		button.setVisibility(View.INVISIBLE);
		button = (Button) findViewById(R.id.token);		
		button.setVisibility(View.INVISIBLE);
		button = (Button) findViewById(R.id.logout);		
		button.setVisibility(View.INVISIBLE);
		}
		catch (Exception e){			
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		DialogFragment df = new TokenDialog();
		df.show(getSupportFragmentManager(), "MyDF");
		Bundle args = new Bundle();
		args.putString("token", exceptionAsString);
		df.setArguments(args);
	}
		
	}
	
	@Override
	public void onRestart()
	{
		super.onRestart();
		try{

		//check status of device
		String host = "www.mwr.com";
	    String stringUrl = "https://"+ host + ":8181/BYOD/status";
	    ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
           System.out.println("No network connection available.");
        }
        Button button = (Button) findViewById(R.id.scan);		
        button.setVisibility(View.INVISIBLE);
		button = (Button) findViewById(R.id.register);		
		button.setVisibility(View.INVISIBLE);
		button = (Button) findViewById(R.id.token);		
		button.setVisibility(View.INVISIBLE);
		button = (Button) findViewById(R.id.logout);		
		button.setVisibility(View.INVISIBLE);
		}
		catch (Exception e){			
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		String exceptionAsString = sw.toString();
		DialogFragment df = new TokenDialog();
		df.show(getSupportFragmentManager(), "MyDF");
		Bundle args = new Bundle();
		args.putString("token", exceptionAsString);
		df.setArguments(args);
	}
		
	}
	
	public void refresh()
	{
		
		System.out.println("Refresh");
			//check status of device
			String host = "www.mwr.com";
		    String stringUrl = "https://"+ host + ":8181/BYOD/status";
		    ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        
	        if (networkInfo != null && networkInfo.isConnected()) {
	            new DownloadWebpageTask().execute(stringUrl);
	        } else {
	           System.out.println("No network connection available.");
	        }
	        Button button = (Button) findViewById(R.id.scan);		
	        button.setVisibility(View.INVISIBLE);
			button = (Button) findViewById(R.id.register);		
			button.setVisibility(View.INVISIBLE);
			button = (Button) findViewById(R.id.token);		
			button.setVisibility(View.INVISIBLE);

	}
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.refreshView:
	            refresh();
	            return true;
	        case R.id.viewToken:
	            viewToken();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	        
	    }
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
	
	public void logout(View view)
	{
		String host = "www.mwr.com";
	    String stringUrl = "https://"+ host + ":8181/BYOD/mobileLogout";
	    ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
           System.out.println("No network connection available.");
        }
        refresh();
	}
	
	
	
	public void viewToken(View view)
	{
		 viewToken();
	}
	
	public void viewToken()
	{
		 TokenGenerator   tokenGen = new TokenGenerator();
		 DatabaseHandler db = new DatabaseHandler(this);
	     String token = "";
		try {
			token = tokenGen.generateToken(device.getMACAddress(), device.getAndroidID(),device.getSerialNumber(), db.getPassword(1));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	        
	     DialogFragment df = new TokenDialog();
	     df.show(getSupportFragmentManager(), "MyDF");
	     Bundle args = new Bundle();
	     args.putString("token", token);
	     df.setArguments(args);
	}

	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
              
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
            	StringWriter sw = new StringWriter();
        		e.printStackTrace(new PrintWriter(sw));
        		String exceptionAsString = sw.toString();
        		DialogFragment df = new TokenDialog();
        		df.show(getSupportFragmentManager(), "MyDF");
        		Bundle args = new Bundle();
        		args.putString("token", exceptionAsString);
        		df.setArguments(args);
                return "Unable to retrieve web page. URL may be invalid.";
            } catch (KeyManagementException e) {
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
			} catch (CertificateException e) {
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
			} catch (KeyStoreException e) {
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
			} catch (NoSuchAlgorithmException e) {
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
            return "";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	
            System.out.println("result: " + result);       
           
            if (result.startsWith("registered"))
            {
            	Button button = (Button) findViewById(R.id.scan);		
                button.setVisibility(View.VISIBLE);
        		button = (Button) findViewById(R.id.register);		
        		button.setVisibility(View.INVISIBLE);
        		button = (Button) findViewById(R.id.token);		
        		button.setVisibility(View.INVISIBLE);
        		button = (Button) findViewById(R.id.logout);		
        		button.setVisibility(View.INVISIBLE);
            }
            else if (result.startsWith("not registered"))
            {
            	Button button = (Button) findViewById(R.id.scan);		
                button.setVisibility(View.INVISIBLE);
        		button = (Button) findViewById(R.id.register);		
        		button.setVisibility(View.VISIBLE);
        		button = (Button) findViewById(R.id.token);		
        		button.setVisibility(View.INVISIBLE);
        		button = (Button) findViewById(R.id.logout);		
        		button.setVisibility(View.INVISIBLE);
            }
            else if (result.startsWith("waiting"))
            {
            	Button button = (Button) findViewById(R.id.scan);		
                button.setVisibility(View.INVISIBLE);
        		button = (Button) findViewById(R.id.register);		
        		button.setVisibility(View.INVISIBLE);
        		button = (Button) findViewById(R.id.token);		
        		button.setVisibility(View.VISIBLE);
        		button = (Button) findViewById(R.id.logout);		
        		button.setVisibility(View.INVISIBLE);
            }    
            else if (result.startsWith("loggedIn"))
            {
            	Button button = (Button) findViewById(R.id.scan);		
                button.setVisibility(View.INVISIBLE);
        		button = (Button) findViewById(R.id.register);		
        		button.setVisibility(View.INVISIBLE);
        		button = (Button) findViewById(R.id.token);		
        		button.setVisibility(View.INVISIBLE);
        		button = (Button) findViewById(R.id.logout);		
        		button.setVisibility(View.VISIBLE);
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
	private String downloadUrl(String myurl) throws IOException, CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
	    InputStream is = null;
	    int len = 500;
	        
	    try {
	    	
	    	
	    	
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
	        device = new DeviceInfo(this);
        	JSONObject data = new JSONObject();
 	        try {
     	        //data.put("mac",device.getMACAddress());
 	        	data.put("mac",device.getMACAddress());
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
