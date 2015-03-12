package com.carandcoffee;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditaccountActivity extends BaseActivity {
	EditText text;
	AVUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		user = AVUser.getCurrentUser();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_account_activity);
		text = (EditText) findViewById(R.id.editText1);
		text.setText(user.getString("nickname"));
	    setBarTitle(R.string.edit_account);

	}

	public void confirm(View v) {
		user.put("nickname", text.getText().toString());
		user.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException arg0) {
				// TODO Auto-generated method stub
				if (arg0 == null) {
					Toast.makeText(getApplicationContext(), "ÐÞ¸Ä³É¹¦",
							Toast.LENGTH_SHORT).show();
					Intent it = new Intent();
					it.setClass(getApplicationContext(), MycarActivity.class);
					setResult(RESULT_OK, it);
					finish();
				}
			}
		});

	}

}
