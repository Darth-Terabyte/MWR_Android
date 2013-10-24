package com.example.byodassettracker.test;

import com.example.byodassettracker.MainActivity;
import com.example.byodassettracker.RegistrationActivity;
import com.example.byodassettracker.SamplingActivity;

import android.content.Intent;
import android.test.*;
import android.test.suitebuilder.annotation.MediumTest;
import android.view.View;
import android.widget.Button;

public class MainActivityTest extends ActivityUnitTestCase<MainActivity> {
	
	private MainActivity mActivity;
	Intent mLaunchIntent;
	private Button registerButton;
	private Button scanButton;
	private Button logoutButton;
	private Button tokenButton;
	
	public MainActivityTest()
	{
		 super(MainActivity.class);
	}
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchIntent = new Intent(getInstrumentation().getTargetContext(), MainActivity.class);
        startActivity(mLaunchIntent, null, null);
        mActivity = getActivity();
        registerButton = (Button) getActivity().findViewById(com.example.byodassettracker.R.id.register);
        scanButton = (Button) getActivity().findViewById(com.example.byodassettracker.R.id.scan);
        logoutButton = (Button) getActivity().findViewById(com.example.byodassettracker.R.id.logout);
        tokenButton = (Button) getActivity().findViewById(com.example.byodassettracker.R.id.token);
    }
	
	@MediumTest
	public void testNextActivityWasLaunchedWithIntent() {
	    //startActivity(mLaunchIntent, null, null);
	    registerButton.performClick();
	    final Intent launchIntentRegister = getStartedActivityIntent();
	    assertNotNull("Intent was null", launchIntentRegister);
	    assertEquals(launchIntentRegister.getComponent().getClassName(), RegistrationActivity.class.getName());	  
	    
	    scanButton.performClick();
	    final Intent launchIntentScan = getStartedActivityIntent();
	    assertNotNull("Intent was null", launchIntentScan);	    
	    assertEquals(launchIntentScan.getComponent().getClassName(), SamplingActivity.class.getName());	 
	    
//	    logoutButton.performClick();
//	    final Intent launchIntentLogout = getStartedActivityIntent();
//	    assertNotNull("Intent was null", launchIntentLogout);
//	    assertEquals(launchIntentLogout.getComponent().getClassName(), MainActivity.class.getName());	 
	  

	    
	}
	
	@MediumTest
	public void testButtonVisibility() {
	    final View decorView = mActivity.getWindow().getDecorView();
	    ViewAsserts.assertOnScreen(decorView, registerButton);
	    assertTrue(View.INVISIBLE == registerButton.getVisibility());
	    assertTrue(View.INVISIBLE == scanButton.getVisibility());
	    assertTrue(View.INVISIBLE == tokenButton.getVisibility());
	    assertTrue(View.INVISIBLE == logoutButton.getVisibility());
	}

}
