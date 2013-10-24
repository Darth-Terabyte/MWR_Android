package com.example.byodassettracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ScanResults extends FragmentActivity {
	
	private static final String BULLET_SYMBOL = "&#8226";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_results);
		TextView tv = (TextView) findViewById(R.id.list);	
		Intent intent = this.getIntent();
	    String result = intent.getExtras().getString("result");
	    String[] categories = result.split(";");

        for(int i=1;i<categories.length;i++) {
        	tv.append(System.getProperty("line.separator")+ Html.fromHtml(BULLET_SYMBOL + categories[i]));
        }
	    
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.scan_results, menu);
		return true;
	}
	
	@Override
    public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
    }
	
	public void rescan(View view)
	{
		Intent intent = new Intent(this, SamplingActivity.class);
		startActivity(intent);
	}


}
