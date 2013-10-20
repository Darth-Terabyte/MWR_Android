package com.example.byodassettracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends FragmentActivity {

	private String password;
	private String username;
	private DeviceInfo device;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.

		return true;
	}

	public void login(View view) {
		try {

			EditText editText = (EditText) findViewById(R.id.username);
			username = editText.getText().toString();
			editText = (EditText) findViewById(R.id.password);
			String plainPassword = editText.getText().toString();
			try {
				MessageDigest digester = MessageDigest.getInstance("MD5");
				byte[] hash = digester.digest(plainPassword.getBytes());
				StringBuilder builder = new StringBuilder(2 * hash.length);
				for (byte b : hash) {
					builder.append(String.format("%02x", b & 0xff));
				}
				password = builder.toString();
			} catch (Exception e) {

			}

			String host = "www.mwr.com";
			String stringUrl = "https://" + host + ":8181/BYOD/login";
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

			if (networkInfo != null && networkInfo.isConnected()) {
				new DownloadWebpageTask(this).execute(stringUrl);
			} else {
				DialogFragment df = new ErrorDialog();
				df.show(getSupportFragmentManager(), "MyDF");
				Bundle args = new Bundle();
				args.putString("message", "No network connection available");
				df.setArguments(args);
			}

		} catch (Exception e) {
			DialogFragment df = new ErrorDialog();
			df.show(getSupportFragmentManager(), "MyDF");
			Bundle args = new Bundle();
			args.putString("message", "Connection error");
			df.setArguments(args);
		}

	}

	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		FragmentActivity activity;

		public DownloadWebpageTask(FragmentActivity act) {
			activity = act;
		}

		@Override
		protected String doInBackground(String... urls) {

			// params comes from the execute() call: params[0] is the url.
			try {
				return downloadUrl(urls[0]);
			} catch (Exception e) {
				DialogFragment df = new ErrorDialog();
				df.show(getSupportFragmentManager(), "MyDF");
				Bundle args = new Bundle();
				args.putString("message", "A connection error has occured");
				df.setArguments(args);
				return "Unable to retrieve web page. URL may be invalid.";
			} 
			
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// System.out.println("result: " + result);

			if (result.startsWith("allowed")) {
				Intent intent = new Intent(activity, MainActivity.class);
				startActivity(intent);
			} else if (result.startsWith("denied")) {
				DialogFragment df = new AccessDeniedDialog();
				df.show(getSupportFragmentManager(), "MyDF");
			} else {
				DialogFragment df = new ErrorDialog();
				df.show(getSupportFragmentManager(), "MyDF");
				Bundle args = new Bundle();
				args.putString("message", "Unknown response");
				df.setArguments(args);
			}
		}
	}

	public String readIt(InputStream stream, int len) throws IOException,
			UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}

	// Given a URL, establishes an HttpUrlConnection and retrieves
	// the web page content as a InputStream, which it returns as
	// a string.
	private String downloadUrl(String myurl) throws IOException,
			CertificateException, KeyStoreException, NoSuchAlgorithmException,
			KeyManagementException {
		InputStream is = null;
		int len = 20;

		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			// From
			// https://www.washington.edu/itconnect/security/ca/load-der.crt
			AssetManager assetManager = getAssets();
			InputStream caInput = assetManager.open("mwr.cer");
			Certificate ca;
			try {
				ca = cf.generateCertificate(caInput);
				// System.out.println("ca=" + ((X509Certificate)
				// ca).getSubjectDN());
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
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(tmfAlgorithm);
			tmf.init(keyStore);

			// Create an SSLContext that uses our TrustManager
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);

			// Tell the URLConnection to use a SocketFactory from our SSLContext
			URL url = new URL(myurl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(context.getSocketFactory());
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("content-type",
					"application/json; charset=utf-8");
			conn.connect();

			OutputStreamWriter wr = new OutputStreamWriter(
					conn.getOutputStream());
			device = new DeviceInfo(this);
			JSONObject data = new JSONObject();
			try {
				// data.put("mac",device.getMACAddress());
				data.put("mac", device.getMACAddress());
				data.put("serial", device.getSerialNumber());
				data.put("android", device.getAndroidID());
				data.put("password", password);
				data.put("username", username);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			wr.write(data.toString());
			wr.flush();
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
