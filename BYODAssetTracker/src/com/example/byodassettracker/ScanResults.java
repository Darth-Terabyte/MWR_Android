package com.example.byodassettracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ScanResults extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_results);
		Intent intent = getIntent();
	    String result = intent.getExtras().getString("result");
	    String[] categories = result.split(";");
	    TableLayout ll=new TableLayout(this);
        ScrollView sv = new ScrollView(this);

        for(int i=1;i<categories.length;i++) {
            TableRow tbrow=new TableRow(this);
            String[] parts = categories[i].split(":");
            for(int j=0;j<parts.length;j++) {
                TextView tv1=new TextView(this);
                tv1.setText(parts[j]);
                tbrow.addView(tv1);
            }
            ll.addView(tbrow);
        }
        sv.addView(ll);
        setContentView(sv);
	    
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.scan_results, menu);
		return true;
	}
	
	@Override
    public void onBackPressed() {
		Intent main = new Intent(Intent.ACTION_MAIN);
		main.addCategory(Intent.CATEGORY_HOME);
		main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(main);	
    }
	
	public void rescan(View view)
	{
		Intent intent = new Intent(this, SamplingActivity.class);
		startActivity(intent);
	}


}
