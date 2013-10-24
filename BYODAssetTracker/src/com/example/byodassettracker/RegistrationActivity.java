package com.example.byodassettracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

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

public class RegistrationActivity extends FragmentActivity {

	private String model;
	private String man;
	private String username = "";
	private String password = "";
	private String password2 = "";
	private String name = "";
	private String surname = "";
	private String id = "";
	DeviceInfo device;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}

	public void register(View view) throws IOException,
			NoSuchAlgorithmException, JSONException {
		device = new DeviceInfo(this);
		man = android.os.Build.MANUFACTURER;
		model = android.os.Build.MODEL;
		EditText editText = (EditText) findViewById(R.id.name);
		name = editText.getText().toString();
		editText = (EditText) findViewById(R.id.surname);
		surname = editText.getText().toString();
		editText = (EditText) findViewById(R.id.id);
		id = editText.getText().toString();
		editText = (EditText) findViewById(R.id.username);
		username = editText.getText().toString();
		editText = (EditText) findViewById(R.id.password1);
		password = editText.getText().toString();
		editText = (EditText) findViewById(R.id.password2);
		password2 = editText.getText().toString();
		System.out.println(name);
		if (id.length() != 13) {
			DialogFragment df = new ErrorDialog();
			df.show(getSupportFragmentManager(), "MyDF");
			Bundle args = new Bundle();
			args.putString("message", "Please enter a valid ID number");
			df.setArguments(args);
		} else if (name == null || surname == null || id == null
				|| username == null || password == null || password2 == null) {
			DialogFragment df = new ErrorDialog();
			df.show(getSupportFragmentManager(), "MyDF");
			Bundle args = new Bundle();
			args.putString("message", "One or more fields were left empty");
			df.setArguments(args);
		} else if (!password.equals(password2)) {
			DialogFragment df = new PasswordDialog();
			df.show(getSupportFragmentManager(), "MyDF");
		} else if (name.equals("") || surname.equals("") || id.equals("")
				|| username.equals("") || password.equals("")
				|| password2.equals("")) {
			DialogFragment df = new ErrorDialog();
			df.show(getSupportFragmentManager(), "MyDF");
			Bundle args = new Bundle();
			args.putString("message", "One or more fields were left empty");
			df.setArguments(args);

		} else {
			// md5 hash password
			try {
				MessageDigest digester = MessageDigest.getInstance("MD5");
				byte[] hash = digester.digest(password.getBytes());
				StringBuilder builder = new StringBuilder(2 * hash.length);
				for (byte b : hash) {
					builder.append(String.format("%02x", b & 0xff));
				}

				password = builder.toString();
				System.out.println(password);
				String host = "www.mwr.com";
				String stringUrl = "https://" + host
						+ ":8181/BYOD/requestRegistration";

				ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					new DownloadWebpageTask(this).execute(stringUrl);
				} else {
					System.out.println("No network connection available. Connect to WiFi network.");
				}

				// save password in db
				getApplicationContext().openOrCreateDatabase("user_db",
						MODE_PRIVATE, null);
				getApplicationContext().deleteDatabase("user_db");
				DatabaseHandler db = new DatabaseHandler(this);
				db.addUser(password);
				// System.out.println(db.getUserCount());
				// generate token
				TokenGenerator tokenGen = new TokenGenerator();
				String token = tokenGen.generateToken(device.getMACAddress(),
						device.getAndroidID(), device.getSerialNumber(),
						password);
				DialogFragment df = new TokenDialog();
				df.show(getSupportFragmentManager(), "MyDF");
				Bundle args = new Bundle();
				token += ". Please contact the system adminstrator to finalise registration.";
				args.putString("token", token);
				df.setArguments(args);
			} catch (Exception e) {
				DialogFragment df = new ErrorDialog();
				df.show(getSupportFragmentManager(), "MyDF");
				Bundle args = new Bundle();
				args.putString("message", "A connection error has occured");
				df.setArguments(args);
			}

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
			} catch (IOException e) {
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
			Intent intent = new Intent(activity, MainActivity.class);
			startActivity(intent);
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
	private String downloadUrl(String myurl) throws IOException {
		InputStream is = null;
		// Only display the first 500 characters of the retrieved
		// web page content.
		int len = 20;
		String contentAsString = "";

		try {

			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			// From
			// https://www.washington.edu/itconnect/security/ca/load-der.crt
			AssetManager assetManager = getAssets();
			InputStream caInput = assetManager.open("mwr.cer");
			Certificate ca;
			try {
				ca = cf.generateCertificate(caInput);
				System.out.println("ca="
						+ ((X509Certificate) ca).getSubjectDN());
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

			// Tell the URLConnection to use a SocketFactory from our
			// SSLContext
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

			JSONObject data = new JSONObject();
			try {
				// data.put("make", man);
				data.put("make", man);
				data.put("model", model);
				data.put("mac", device.getMACAddress());
				// data.put("mac", "mac");
				data.put("serial", device.getSerialNumber());
				data.put("android", device.getAndroidID());
				data.put("username", username);
				data.put("password", password);
				data.put("name", name);
				data.put("surname", surname);
				data.put("id", id);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			wr.write(data.toString());
			wr.flush();
			is = conn.getInputStream();

			// Convert the InputStream into a string
			contentAsString = readIt(is, len);

			return contentAsString;
			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} catch (Exception e) {

			DialogFragment df = new ErrorDialog();
			df.show(getSupportFragmentManager(), "MyDF");
			Bundle args = new Bundle();
			args.putString("message", "A connection error has occured");
			df.setArguments(args);

		} finally {

			if (is != null) {
				is.close();
			}

		}
		return contentAsString;
	}

}
