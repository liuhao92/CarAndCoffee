package com.carandcoffee;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SessionManager;
import com.carandcoffee.application.CACApplication;

public class MainActivity extends Activity {
	public static List<String> peerIds ;
	CACApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		app = (CACApplication) getApplication();
        peerIds=app.peerIds;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (AVUser.getCurrentUser() != null && app.session == null) {
			app.MypeerID=AVUser.getCurrentUser()
					.getString("peerId");
			app.session = SessionManager.getInstance(app.MypeerID);
			app.session.open(new ArrayList<String>());
			app.saveInstallation(app.MypeerID);
		}
	}

	public void itemClick(View v) {

		int id = v.getId();
		switch (id) {
		case R.id.rl_service:
			Intent intent = new Intent(this, ServiceActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_car:
			Intent intent1 = new Intent(this, MycarActivity.class);
			startActivity(intent1);
			break;
		case R.id.rl_order:
			if(AVUser.getCurrentUser()!=null){
			Intent intent2 = new Intent(this, MyorderActivity.class);
			startActivity(intent2);}
			else 
				Toast.makeText(this,getString(R.string.dont_have_login),Toast.LENGTH_SHORT).show();
			break;
		case R.id.rl_promotion:
			Intent i = new Intent(this,PromotionActivity.class);
			startActivity(i);
			break;
		default:
			break;
		}

	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	public void startActivity(Intent intent) {
		// TODO Auto-generated method stub
		super.startActivity(intent);
		overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//
//		if (app.session != null) {
//			app.session.close();
//			app.session=null;
//			peerIds.clear();
//		}

		Log.e("chengxu", "πÿ±’¡À≥Ã–Ú");
	}

}
