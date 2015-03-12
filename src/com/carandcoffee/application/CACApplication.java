package com.carandcoffee.application;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogUtil;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SessionManager;
import com.carandcoffee.MainActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class CACApplication extends Application {
	public SessionManager session = null;
    public String MypeerID="";
    public  List<String> peerIds = new ArrayList<String>();
	@Override
	public void onCreate() {
		super.onCreate();

		AVOSCloud.initialize(this,
				"za9bsa07s9lwzxl6t1sp9ft3fi5ypo0d47ylo1f5bnze0m34",
				"0efztvcng6f5klnksu9syv4o55py3z9pypppjzxuzuwwqmtb");

		AVInstallation insta = AVInstallation.getCurrentInstallation();
		insta.saveInBackground();
		PushService.setDefaultPushCallback(getApplicationContext(),MainActivity.class); 
		// PushConnectionRetryController re=new PushConnectionRetryController()
		/*
		 * PushService.setDefaultPushCallback(getApplicationContext(),
		 * MainActivity.class); AVPushServiceAppManager ma = new
		 * AVPushServiceAppManager(this); AVPushConnectionManager con =
		 * AVPushConnectionManager.getInstance(this, ma,
		 * AVOSCloud.applicationId, insta.getInstallationId());
		 * 
		 * con.cleanupSocketConnection(); con.initConnection();
		 */
		Log.e("objeid", AVInstallation.getCurrentInstallation().getObjectId());
		try {
			Class<?> avosclass = Class.forName("com.avos.avoscloud.AVOSCloud");
			Method enableLogMethod = avosclass.getDeclaredMethod(
					"showInternalDebugLog", boolean.class);
			enableLogMethod.setAccessible(true);
			enableLogMethod.invoke(avosclass, true);
			LogUtil.avlog.i("successed enable avoscloud logs");
		} catch (Exception e) {
			LogUtil.avlog.i("failed enable avoscloud logs");
		}

		if (AVUser.getCurrentUser() != null) {
			MypeerID=AVUser.getCurrentUser()
					.getString("peerId");
			session = SessionManager.getInstance(MypeerID);
			List<String> peerIds = new ArrayList<String>();
			session.open(peerIds);
			saveInstallation(MypeerID);
		}
		else{
			MypeerID=getshar();
			boolean t=MypeerID.equals("");
		    if(!t)
		    {
		    	session = SessionManager.getInstance(MypeerID);
		    	List<String> peerIds = new ArrayList<String>();
				session.open(peerIds);
				saveInstallation(MypeerID);
		    }
		}
		Log.e("chengxu", "∆Ù∂Ø¡À≥Ã–Ú");

	}
	
	public  void writeshar(String str){
		
		SharedPreferences peerID=getSharedPreferences("peerID",Activity.MODE_PRIVATE);
		Editor edit=peerID.edit();
		edit.putString("peerID",str);
		edit.commit();
	}
    public  String getshar(){
		
		SharedPreferences peerID=getSharedPreferences("peerID",Activity.MODE_PRIVATE);
		
		return peerID.getString("peerID","");
	}
    public void  saveInstallation(String str) {
    	AVInstallation insta = AVInstallation.getCurrentInstallation();
		insta.put("peerID",str);
		insta.saveInBackground();
	}
	
}
