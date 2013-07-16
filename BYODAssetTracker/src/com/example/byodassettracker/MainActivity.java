package com.example.byodassettracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class MainActivity extends Activity {
	
	
	 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void register(View view)
	{				
		Intent intent = new Intent(this, RegistrationActivity.class);
		startActivity(intent);		
	}
	
	public void login(View view)
	{
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
	}
	
	public void sample(View view)
	{
		Intent intent = new Intent(this, SamplingActivity.class);
		startActivity(intent);
	}
	
	public void scan(View view)
	{
		Intent intent = new Intent(this, ScanActivity.class);
		startActivity(intent);
	}

}
