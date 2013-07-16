package com.example.byodassettracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;

public class RegistrationActivity extends FragmentActivity {
	
	 public final static String NAME = "com.example.myfirstapp.NAME";
	 public final static String SURNAME = "com.example.myfirstapp.SURNAME";
	 public final static String EMAIL = "com.example.myfirstapp.EMAIL";
	 public final static String ID = "com.example.myfirstapp.ID";
	 public final static String PASSWORD = "com.example.myfirstapp.PASSWORD";

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
	
	public void register(View view)  throws IOException 
	{		
		// Gets the URL from the UI's text field.
        String stringUrl = "http://197.111.134.76:8080/AffableBean/";
        ConnectivityManager connMgr = (ConnectivityManager) 
            getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
           System.out.println("No network connection available.");
        }
		
		
		//InputStream is = null;
	    // Only display the first 500 characters of the retrieved
	    // web page content.
	    //int len = 500;
	    //String server = "http://10.203.19.50:8080/AffableBean/";
	    //String server = "http://www.google.com";
	    /*try {
	        URL url = new URL(server);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        //conn.setReadTimeout(10000);
	        //conn.setConnectTimeout(15000);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        
	        	conn.connect();*/
	        

	        //int response = conn.getResponseCode();
	        //Log.d(DEBUG_TAG, "The response is: " + response);
	        //is = conn.getInputStream();
	        	        // Convert the InputStream into a string
	        //String contentAsString = readIt(is, len);
	        //System.out.println("response " + contentAsString);
	        
	        
	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.

			
		/*EditText editText = (EditText) findViewById(R.id.password1);
		String password = editText.getText().toString();
		editText = (EditText) findViewById(R.id.password2);		
		String password2 = editText.getText().toString();
		if (!password.equals(password2))
		{
			DialogFragment df = new PasswordDialog();
			df.show(getSupportFragmentManager(), "MyDF");
		}
		else
		{
		
		
		
			Intent intent = new Intent(this, ScanActivity.class);
			
			editText = (EditText) findViewById(R.id.name);
			String name = editText.getText().toString();
			editText = (EditText) findViewById(R.id.surname);
			String surname = editText.getText().toString();
			editText = (EditText) findViewById(R.id.email);
			String email = editText.getText().toString();
			editText = (EditText) findViewById(R.id.idnumber);
			String id = editText.getText().toString();
			editText = (EditText) findViewById(R.id.password1);	
			intent.putExtra(NAME, name);
			intent.putExtra(SURNAME, surname);
			intent.putExtra(EMAIL, email);
			intent.putExtra(ID, id);
			intent.putExtra(PASSWORD, password);
			startActivity(intent);
		}*/
		
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
	        conn.setRequestMethod("GET");
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

