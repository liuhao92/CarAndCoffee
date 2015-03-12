package com.carandcoffee;

import java.io.File;
import java.util.ArrayList;

import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SessionManager;
import com.carandcoffee.application.CACApplication;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

public class MyaccountActivity extends BaseActivity {
	final static int EDIT = 0;
	AVUser user;
	CACApplication app;
	Handler post;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		app = (CACApplication) getApplication();

		user = AVUser.getCurrentUser();
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_acount_activity);

		setBarTitle(R.string.my_account);

		TextView text = (TextView) findViewById(R.id.yonghuming);
		text.setText(user.getString("nickname"));
		post = new Handler();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {

		case EDIT:
			if (resultCode == RESULT_OK) {

				TextView text = (TextView) findViewById(R.id.yonghuming);
				text.setText(user.getString("nickname"));
			}
			break;
		}
	}

	public void edit_account(View v) {
		Intent it = new Intent(this, EditaccountActivity.class);
		startActivityForResult(it, EDIT);
	}

	public void exit(View v) {
		if (app.session != null) {
			app.session.close();
			app.session = null;
			MainActivity.peerIds.clear();
			app.MypeerID = "";
			app.saveInstallation(app.MypeerID);
			post.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					app.MypeerID = app.getshar();
					if (!app.MypeerID.equals("")) {
						app.session = SessionManager.getInstance(app.MypeerID);
						app.session.open(new ArrayList<String>());
						app.saveInstallation(app.MypeerID);
					}
				}
			}, 500);

		}
		File dir = getCacheDir();
		File icon = new File(dir, AVUser.getCurrentUser().getUsername()
				+ ".jpg");
		if (icon.exists()) {
			icon.delete();
		}
		AVUser.logOut();
		Intent it = new Intent(this, MycarActivity.class);
		setResult(RESULT_OK, it);
		finish();
	}
}
