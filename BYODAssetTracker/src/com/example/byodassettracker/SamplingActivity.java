package com.example.byodassettracker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Build;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;

public class SamplingActivity extends FragmentActivity {
	
	private View SamplingStatusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sampling);
		SamplingStatusView = findViewById(R.id.sampling_status);
		//showProgress(true);
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
	        if (str.toLowerCase().equals("superuser"))
	        {
	        	found = true;
	        	break;
	        }           
	        System.out.println(str + " in package " + rInfo.activityInfo.applicationInfo.packageName);
	    }	
	    
		File file = new File("/system/xbin/su");  
        boolean exists = file.exists();  
          
        if (exists) {  
        	RootedDialog df = new RootedDialog();
			df.show(getSupportFragmentManager(), "MyDF2");
                                  
        }

	}
	
	

}
