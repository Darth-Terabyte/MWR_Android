package com.example.byodassettracker.test;

import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import com.example.byodassettracker.DatabaseHandler;

public class DatabaseHandlerTest extends AndroidTestCase {
	DatabaseHandler db;
	
	public void setUp(){
        RenamingDelegatingContext context 
        = new RenamingDelegatingContext(getContext(), "test_");
        db = new DatabaseHandler(context);
    }

    public void testAddEntry(){
       db.addUser("234abc");
       String expected = "234abc";
       String actual = db.getPassword(1);
       assertEquals(expected,actual);
       
    }

    public void tearDown() throws Exception{
        db.close(); 
        super.tearDown();
    }
}
