package com.example.byodassettracker.test;

import com.example.byodassettracker.MainActivity;
import com.example.byodassettracker.SamplingActivity;
import com.example.byodassettracker.ScanResults;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.test.*;
import android.test.suitebuilder.annotation.MediumTest;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ScanResultsTest extends ActivityUnitTestCase<ScanResults> {
	

	Intent mLaunchIntent;
	private Button rescanButton;
	private TextView caption;
	private TextView list;
	
	public ScanResultsTest()
	{
		 super(ScanResults.class);
	}
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchIntent = new Intent(getInstrumentation().getTargetContext(), ScanResults.class);
        mLaunchIntent.putExtra("result", "denied;This is the result");
        startActivity(mLaunchIntent, null, null);
        rescanButton = (Button) getActivity().findViewById(com.example.byodassettracker.R.id.rescan);         
        caption = (TextView) getActivity().findViewById(com.example.byodassettracker.R.id.accessDeniedLabel);           
        list = (TextView) getActivity().findViewById(com.example.byodassettracker.R.id.list);   
        assert(list.getText().equals(System.getProperty("line.separator")+ Html.fromHtml("&#8226" + "This is the result")));
    }
	
	@MediumTest
	public void testNextActivityWasLaunchedWithIntent() {
		rescanButton.performClick();
	    final Intent launchIntentRegister = getStartedActivityIntent();
	    assertNotNull("Intent was null", launchIntentRegister);		
	    assertEquals(launchIntentRegister.getComponent().getClassName(), SamplingActivity.class.getName());	    
		
//		rescanButton.performClick();
//		getInstrumentation().waitForIdleSync();
//		Fragment dialog = getActivity().getSupportFragmentManager().findFragmentByTag("Rescan");
//		assertNotNull(dialog);
//		assertTrue(dialog instanceof DialogFragment);
//		assertTrue(((DialogFragment) dialog).getShowsDialog());

	    
	}
	

	

	


}
