package com.example.byodassettracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class ScanActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan);
		// Show the Up button in the action bar.
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void scan(View view)
	{		
		ArrayList results = new ArrayList();
        PackageManager pm = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(intent,PackageManager.PERMISSION_GRANTED);
        String str;
        boolean found = false;
        for (ResolveInfo rInfo : list) {
            str = rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
        //results.add(rInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
            if (str.equals("AVG Antivirus"))
            {
            	found = true;
            	break;
            }           
            System.out.println(str + " in package " + rInfo.activityInfo.applicationInfo.packageName);
        }	
        if (!found)
        {
        	ScanDialog df = new ScanDialog();
    		df.show(getSupportFragmentManager(), "MyDF2");
        }
		
	}
	
//	public void scan(View view) throws CertificateException, FileNotFoundException, IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
//	   
//	        // TODO code application logic here
//	        
//	        // Load CAs from an InputStream
//	// (could be from a resource or ByteArrayInputStream or ...)
//	        //System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jre7\\lib\\security\\cacerts");
//	         //System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
//		
//		AssetManager assetManager = getAssets();
//
//		
//	        CertificateFactory cf = CertificateFactory.getInstance("X.509");
//	        // From https://www.washington.edu/itconnect/security/ca/load-der.crt
//	        InputStream caInput = assetManager.open("server.cer");
//
//	        Certificate ca;
//	        try {
//	            ca = cf.generateCertificate(caInput);
//	            System.out.println("ca=" + ((X509Certificate)ca).getSubjectDN());
//	        } finally {
//	            caInput.close();
//	        }
//
//	        // Create a KeyStore containing our trusted CAs
//	        String keyStoreType = KeyStore.getDefaultType();
//	        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//	        keyStore.load(null, null);
//	        keyStore.setCertificateEntry("ca", ca);
//
//	        // Create a TrustManager that trusts the CAs in our KeyStore
//	        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//	        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//	        tmf.init(keyStore);
//
//	        // Create an SSLContext that uses our TrustManager
//	        SSLContext context = SSLContext.getInstance("TLS");
//	        context.init(null,tmf.getTrustManagers(), null);
//
//	        // Tell the URLConnection to use a SocketFactory from our SSLContext
//	        URL url = new URL("https://192.168.1.103:8181/AffableBean/");
//	        HttpsURLConnection urlConnection = (HttpsURLConnection)url.openConnection();
//	        urlConnection.setSSLSocketFactory(context.getSocketFactory());
//	        InputStream in = urlConnection.getInputStream();
//	        copyInputStreamToOutputStream(in,System.out);
//	    
//	    }

	    private static void copyInputStreamToOutputStream(InputStream in, PrintStream out) throws IOException {
	   //To change body of generated methods, choose Tools | Templates.
	        byte[] buffer = new byte[1024];
	        int len;
	        while ((len = in.read(buffer)) != -1) {
	             out.write(buffer, 0, len);
	}
	        
	        
	    }

}
