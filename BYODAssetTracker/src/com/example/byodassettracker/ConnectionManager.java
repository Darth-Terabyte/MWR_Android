package com.example.byodassettracker;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;


class ConnectionManager
{

	public void doPost(String myurl,JSONObject data) throws IOException, JSONException {
	    InputStream is = null;
	    // Only display the first 500 characters of the retrieved
	    // web page content.
	    int len = 500;
	        
	    
	        URL url = new URL(myurl);	        
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("POST");
	        conn.setDoInput(true);
	        conn.setRequestProperty("content-type","application/json; charset=utf-8"); 
	        // Starts the query
	        conn.connect();
	        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
 	        wr.write(data.toString());
 	        wr.flush();
 	        
 	        
//	        int response = conn.getResponseCode();
//	        //Log.d(DEBUG_TAG, "The response is: " + response);
//	        is = conn.getInputStream();
//
//	        // Convert the InputStream into a string
//	        String contentAsString = readIt(is, len);
//	        return contentAsString;
	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
//	    finally {
//	        if (is != null) {
//	            is.close();
//	        } 
//	    }
	}
}
	
	
	
