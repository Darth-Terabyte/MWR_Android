package com.example.byodassettracker;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;

public class RegistrationActivity extends FragmentActivity {
	
	 public final static String NAME = "com.example.myfirstapp.NAME";
	 public final static String SURNAME = "com.example.myfirstapp.SURNAME";
	 public final static String EMAIL = "com.example.myfirstapp.EMAIL";
	 public final static String ID = "com.example.myfirstapp.ID";
	 public final static String PASSWORD = "com.example.myfirstapp.PASSWORD";
	 private String model;
	 private String man;
	 private String androidID;
	 private String serial;
	 private String mac;
	 private String username;
	 private String password;
	 private String name;
	 private String surname;
	 private String id;
	 DeviceInfo device;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}

	public void register(View view)  throws IOException, NoSuchAlgorithmException, JSONException 
	{	
		device = new DeviceInfo(this);
		man = android.os.Build.MANUFACTURER;
		model = android.os.Build.MODEL;		
		EditText editText = (EditText) findViewById(R.id.name);
		name =  editText.getText().toString();
		editText = (EditText) findViewById(R.id.surname);		
		surname =  editText.getText().toString();
		editText = (EditText) findViewById(R.id.id);
		id =  editText.getText().toString();		
		editText = (EditText) findViewById(R.id.username);
		username = editText.getText().toString();		
		editText = (EditText) findViewById(R.id.password1);
		password = editText.getText().toString();
		editText = (EditText) findViewById(R.id.password2);		
		String password2 = editText.getText().toString();
		System.out.println(name);
		if (!password.equals(password2))
		{
			DialogFragment df = new PasswordDialog();
			df.show(getSupportFragmentManager(), "MyDF");
		}
		else
		{
			//md5 hash password
			MessageDigest digester = MessageDigest.getInstance("MD5");
	        byte[] hash = digester.digest(password.getBytes());
	        StringBuilder builder = new StringBuilder(2*hash.length);
	        for (byte b : hash)
	        {
	            builder.append(String.format("%02x",b&0xff));
	        }	
	        
	        password = builder.toString();
	        System.out.println(password);
	        String host = "192.168.1.100";	     
	        String stringUrl = "http://"+ host + ":8080/BYOD/requestRegistration";
			
	        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected()) {
	            new DownloadWebpageTask().execute(stringUrl);
	        } else {
	           System.out.println("No network connection available.");
	        }
	        
	        //generate token
	        String composite =device.getMACAddress()+device.getAndroidID()+device.getSerialNumber();
	        hash = digester.digest(composite.getBytes());
	        builder = new StringBuilder(2*hash.length);
	        for (byte b : hash)
	        {
	            builder.append(String.format("%02x",b&0xff));
	        }
	        String hashkey = builder.toString();
	        int skip = Math.round((float)hashkey.length()/5);
	        int index = 0;
	        String token = "";
	        for (int i=0;i<5;i++)
	        {
	        	token += hashkey.charAt(index);
	        	index += skip;
	        }	        
	        
	        DialogFragment df = new TokenDialog();
			df.show(getSupportFragmentManager(), "MyDF");
			Bundle args = new Bundle();
			args.putString("token", token);
			df.setArguments(args);
    
		}

		
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
	    int i =0;
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
	    String contentAsString = "";
	        
	    try {
	    	try
	    	{
	        	URL url = new URL(myurl);	        
		        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		        conn.setReadTimeout(10000 /* milliseconds */);
		        conn.setConnectTimeout(15000 /* milliseconds */);
		        conn.setRequestMethod("POST");
		        conn.setDoInput(true);
		        conn.setDoOutput(true);
		        conn.setRequestProperty("content-type","application/json; charset=utf-8"); 
		        // Starts the query
		        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

	        
	    	
	      
        	JSONObject data = new JSONObject();
 	        try {
				//data.put("make", man);
 	        	data.put("make", man);
     	        data.put("model", model);
     	        data.put("mac", device.getMACAddress());
     	       //data.put("mac", "mac");
     	        data.put("serial", device.getSerialNumber());
     	        data.put("android", device.getAndroidID());
     	        data.put("username", username);
     	        data.put("password",password);
     	        data.put("name", name);
     	        data.put("surname", surname);
     	        data.put("id", id);
     	
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

 	        wr.write(data.toString());
            wr.flush();
	        int response = conn.getResponseCode();
	        
	        //Log.d(DEBUG_TAG, "The response is: " + response);
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

