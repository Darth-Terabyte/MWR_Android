package com.example.byodassettracker.test;

import com.example.byodassettracker.MainActivity;
import com.example.byodassettracker.RegistrationActivity;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.test.*;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.Button;
import android.widget.EditText;

public class RegistrationActivityTest extends ActivityUnitTestCase<RegistrationActivity> {
	

	Intent mLaunchIntent;
	private Button registerButton;
	private EditText name;
	private EditText surname;
	private EditText id;
	private EditText username;
	private EditText password1;
	private EditText password2;
	
	public RegistrationActivityTest()
	{
		 super(RegistrationActivity.class);
	}
	
	@Override
    protected void setUp() throws Exception {
        super.setUp();
        mLaunchIntent = new Intent(getInstrumentation().getTargetContext(), RegistrationActivity.class);
        startActivity(mLaunchIntent, null, null);
        registerButton = (Button) getActivity().findViewById(com.example.byodassettracker.R.id.registerUser);      
        name = (EditText) getActivity().findViewById(com.example.byodassettracker.R.id.name);      
        surname = (EditText)  getActivity().findViewById(com.example.byodassettracker.R.id.surname);      
        id = (EditText)  getActivity().findViewById(com.example.byodassettracker.R.id.id);  
        username = (EditText)  getActivity().findViewById(com.example.byodassettracker.R.id.username);      
        password1 = (EditText)  getActivity().findViewById(com.example.byodassettracker.R.id.password1);
        password2 = (EditText)  getActivity().findViewById(com.example.byodassettracker.R.id.password2);      
    }
	
	@MediumTest
	public void testNextActivityWasLaunchedWithIntent() {
	    //startActivity(mLaunchIntent, null, null);
		
		name.setText("Patrick");
		surname.setText("Jones");
		id.setText("1234");
		username.setText("madene");
		password1.setText("cat");
		password2.setText("cat");	
		
		registerButton.performClick();
	    final Intent launchIntentRegister = getStartedActivityIntent();
	    assertNotNull("Intent was null", launchIntentRegister);
		
	    assertEquals(launchIntentRegister.getComponent().getClassName(), MainActivity.class.getName());	    
		
//		registerButton.performClick();
//		getInstrumentation().waitForIdleSync();
//		Fragment dialog = getActivity().getSupportFragmentManager().findFragmentByTag("MyDF");
//
//		//assertTrue(dialog instanceof DialogFragment);
//		assertTrue(((DialogFragment) dialog).getShowsDialog());

	    
	   
	    

	    
	}

}
