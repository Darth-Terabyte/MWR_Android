package com.example.byodassettracker;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.FragmentActivity;


public class DeviceInfo {
	
	private boolean rooted;
	private boolean avInstalled;
	private ArrayList<String> installedApps;
	private int apiLevel;
	private boolean debuggingEnabled;
	private boolean unknownSourcesAllowed;
	private String macAddress;
	String serialNumber;
	String androidID;	
	FragmentActivity activity;
	
	
public DeviceInfo(FragmentActivity act)
{
	activity = act;
	setAppList();
	checkRooted();
	checkDebug();
	checkUnknown();
	setMAC();
	setSerial();
	setAndroidID();
}

	public ArrayList<String> getApps()
	{
		return installedApps;
	}
	
	public boolean getRooted()
	{
		return rooted;
	}
	
	public boolean getDebug()
	{
		return debuggingEnabled;
	}
	
	public boolean getUnknownSourcesAllowed()
	{
		return unknownSourcesAllowed;
	}
	
	public String getMACAddress()
	{
		return macAddress;
	}
	
	public String getSerialNumber()
	{
		return serialNumber;
	}
	
	public String getAndroidID()
	{
		return androidID;
	}
	
	public int getAPILevel()
	{
		return apiLevel = Build.VERSION.SDK_INT;
	}
	
	private void checkRooted()
	{
	    
			File file = new File("/system/xbin/su");  
	        rooted = file.exists();
	       
	        if (!rooted)
	        {
	        	for (int i=0;i<installedApps.size();i++)
	        	{
	        		if (installedApps.get(i).toLowerCase().equals("superuser"))
			        {
			        	rooted = true;
			        }      
			        else if (installedApps.get(i).contains("AVG"))
			        	avInstalled = true;
			        else avInstalled = false;
	        	}
	        	
	        }
	     
	}
	
	private void setAppList()
	{
		PackageManager pm = activity.getPackageManager();
	    Intent intent = new Intent(Intent.ACTION_MAIN, null);
	    intent.addCategory(Intent.CATEGORY_LAUNCHER);
	    List<ResolveInfo> list = pm.queryIntentActivities(intent,PackageManager.PERMISSION_GRANTED);
	    String str;
	    installedApps = new ArrayList<String>();
	    for (ResolveInfo rInfo : list) {
	        str = rInfo.activityInfo.applicationInfo.loadLabel(pm).toString();
	        installedApps.add(str);
	    }

	
	}
	
	private void checkDebug()
	{
		int allowed = 0;
		try {
			allowed = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.ADB_ENABLED);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (allowed == 1)
			debuggingEnabled = true;
		else debuggingEnabled = false;
	}
	
	private void checkUnknown()
	{
		int allowed = 0;
		try {
			allowed = Settings.Secure.getInt(activity.getContentResolver(), Settings.Secure.INSTALL_NON_MARKET_APPS);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (allowed == 1)
			unknownSourcesAllowed =  true;
		else unknownSourcesAllowed =  false;
    	
	}
	
	
	private void setMAC()
	{
		WifiManager wifiMan = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInf = wifiMan.getConnectionInfo();
		macAddress = wifiInf.getMacAddress();
	}
	
	private void setSerial()
	{
		
		 String serialnum = null;      
		 try {         
		   Class<?> c = Class.forName("android.os.SystemProperties");        	           	      
		   Method get = c.getMethod("get", String.class, String.class );                 
	               serialnum = (String)(   get.invoke(c, "ro.serialno", "unknown" )  );
	              
	        	} catch (Exception ignored) {       
	           }
		 
		 serialNumber =  serialnum;
	}
	
	private void setAndroidID()
	{
		androidID = Settings.Secure.getString(activity.getContentResolver(),Settings.Secure.ANDROID_ID);			       
	}
}