package com.example.byodassettracker;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ScanResults extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_results);
		Intent intent = getIntent();
	    String result = intent.getExtras().getString("result");
	    TextView textView = (TextView)findViewById(R.id.result);		
	    textView.setText(result);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scan_results, menu);
		return true;
	}
	
	public void rescan(View view)
	{
		Intent intent = new Intent(this, SamplingActivity.class);
		startActivity(intent);
	}


}
