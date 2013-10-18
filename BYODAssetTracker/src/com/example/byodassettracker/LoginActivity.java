package com.example.byodassettracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
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
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONException;
import org.json.JSONObject;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
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
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}
	
	public void login(View view)
	{
		
		try{

		EditText editText = (EditText) findViewById(R.id.username);
		username =  editText.getText().toString();
		editText = (EditText) findViewById(R.id.password);		
		String plainPassword  =  editText.getText().toString();
		try
		{
			MessageDigest digester = MessageDigest.getInstance("MD5");
        byte[] hash = digester.digest(plainPassword.getBytes());
        StringBuilder builder = new StringBuilder(2*hash.length);
        for (byte b : hash)
        {
            builder.append(String.format("%02x",b&0xff));
        }	
        password = builder.toString();
		}
		catch (Exception e)
		{
			
		}
		
        
		String host = "www.mwr.com";
	    String stringUrl = "https://"+ host + ":8181/BYOD/login";
	    ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        
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
	
	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
              
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            } catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CertificateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return "";
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            System.out.println("result: " + result);       
           
            if (result.startsWith("allowed"))
            {
            	DialogFragment df = new TokenDialog();
				df.show(getSupportFragmentManager(), "MyDF");
				Bundle args = new Bundle();
				args.putString("token", "allowed");
				df.setArguments(args);
            }
            else if (result.startsWith("denied"))
            {
            	DialogFragment df = new TokenDialog();
				df.show(getSupportFragmentManager(), "MyDF");
				Bundle args = new Bundle();
				args.putString("token", "denied");
				df.setArguments(args);
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
     	        data.put("password", password);
     	        data.put("username",username);     	        
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


//package com.example.byodassettracker;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.Menu;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.widget.EditText;
//import android.widget.TextView;
//
///**
// * Activity which displays a login screen to the user, offering registration as
// * well.
// */
//public class LoginActivity extends Activity {
//	/**
//	 * A dummy authentication store containing known user names and passwords.
//	 * TODO: remove after connecting to a real authentication system.
//	 */
//	private static final String[] DUMMY_CREDENTIALS = new String[] {
//			"foo@example.com:hello", "bar@example.com:world" };
//
//	/**
//	 * The default email to populate the email field with.
//	 */
//	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";
//
//	/**
//	 * Keep track of the login task to ensure we can cancel it if requested.
//	 */
//	private UserLoginTask mAuthTask = null;
//
//	// Values for email and password at the time of the login attempt.
//	private String mEmail;
//	private String mPassword;
//
//	// UI references.
//	private EditText mEmailView;
//	private EditText mPasswordView;
//	private View mLoginFormView;
//	private View mLoginStatusView;
//	private TextView mLoginStatusMessageView;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		setContentView(R.layout.activity_login);
//
//		// Set up the login form.
//		mEmail = getIntent().getStringExtra(EXTRA_EMAIL);
//		mEmailView = (EditText) findViewById(R.id.email);
//		mEmailView.setText(mEmail);
//
//		mPasswordView = (EditText) findViewById(R.id.password);
//		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//					@Override
//					public boolean onEditorAction(TextView textView, int id,KeyEvent keyEvent) {
//						if (id == R.id.login || id == EditorInfo.IME_NULL) {
//							attemptLogin();
//							return true;
//						}
//						return false;
//					}
//				});
//
//		mLoginFormView = findViewById(R.id.login_form);
//		mLoginStatusView = findViewById(R.id.login_status);
//		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
//
//		findViewById(R.id.sign_in_button).setOnClickListener(
//				new View.OnClickListener() {
//					@Override
//					public void onClick(View view) {
//						attemptLogin();
//					}
//				});
//	}
//
//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		super.onCreateOptionsMenu(menu);
//		getMenuInflater().inflate(R.menu.login, menu);
//		return true;
//	}
//
//	/**
//	 * Attempts to sign in or register the account specified by the login form.
//	 * If there are form errors (invalid email, missing fields, etc.), the
//	 * errors are presented and no actual login attempt is made.
//	 */
//	public void attemptLogin() {
//		if (mAuthTask != null) {
//			return;
//		}
//
//		// Reset errors.
//		mEmailView.setError(null);
//		mPasswordView.setError(null);
//
//		// Store values at the time of the login attempt.
//		mEmail = mEmailView.getText().toString();
//		mPassword = mPasswordView.getText().toString();
//
//		boolean cancel = false;
//		View focusView = null;
//
//		// Check for a valid password.
//		if (TextUtils.isEmpty(mPassword)) {
//			mPasswordView.setError(getString(R.string.error_field_required));
//			focusView = mPasswordView;
//			cancel = true;
//		} else if (mPassword.length() < 4) {
//			mPasswordView.setError(getString(R.string.error_invalid_password));
//			focusView = mPasswordView;
//			cancel = true;
//		}
//
//		// Check for a valid email address.
//		if (TextUtils.isEmpty(mEmail)) {
//			mEmailView.setError(getString(R.string.error_field_required));
//			focusView = mEmailView;
//			cancel = true;
//		} else if (!mEmail.contains("@")) {
//			mEmailView.setError(getString(R.string.error_invalid_email));
//			focusView = mEmailView;
//			cancel = true;
//		}
//
//		if (cancel) {
//			// There was an error; don't attempt login and focus the first
//			// form field with an error.
//			focusView.requestFocus();
//		} else {
//			// Show a progress spinner, and kick off a background task to
//			// perform the user login attempt.
//			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
//			showProgress(true);
//			mAuthTask = new UserLoginTask();
//			mAuthTask.execute((Void) null);
//		}
//	}
//
//	/**
//	 * Shows the progress UI and hides the login form.
//	 */
//	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
//	private void showProgress(final boolean show) {
//		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
//		// for very easy animations. If available, use these APIs to fade-in
//		// the progress spinner.
//		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
//			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
//
//			mLoginStatusView.setVisibility(View.VISIBLE);
//			mLoginStatusView.animate().setDuration(shortAnimTime)
//				.alpha(show ? 1 : 0)
//					.setListener(new AnimatorListenerAdapter() {
//						@Override
//						public void onAnimationEnd(Animator animation) {
//							mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
//						}
//					});
//
//			mLoginFormView.setVisibility(View.VISIBLE);
//			mLoginFormView.animate().setDuration(shortAnimTime)
//					.alpha(show ? 0 : 1)
//					.setListener(new AnimatorListenerAdapter() {
//						@Override
//						public void onAnimationEnd(Animator animation) {
//							mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//						}
//					});
//		} else {
//			// The ViewPropertyAnimator APIs are not available, so simply show
//			// and hide the relevant UI components.
//			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
//			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
//		}
//	}
//
//	/**
//	 * Represents an asynchronous login/registration task used to authenticate
//	 * the user.
//	 */
//	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			// TODO: attempt authentication against a network service.
//
//			try {
//				// Simulate network access.
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				return false;
//			}
//
//			for (String credential : DUMMY_CREDENTIALS) {
//				String[] pieces = credential.split(":");
//				if (pieces[0].equals(mEmail)) {
//					// Account exists, return true if the password matches.
//					return pieces[1].equals(mPassword);
//				}
//			}
//
//			// TODO: register the new account here.
//			return true;
//		}
//
//		@Override
//		protected void onPostExecute(final Boolean success) {
//			mAuthTask = null;
//			showProgress(false);
//
//			if (success) {
//				finish();
//			} else {
//				mPasswordView
//						.setError(getString(R.string.error_incorrect_password));
//				mPasswordView.requestFocus();
//			}
//		}
//
//		@Override
//		protected void onCancelled() {
//			mAuthTask = null;
//			showProgress(false);
//		}
//	}
//}
