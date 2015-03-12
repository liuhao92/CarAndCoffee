package com.carandcoffee;

import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class AddAddressActivity extends BaseActivity {
boolean flag=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_address_edit);
		setBarTitle(R.string.add_address);
		setBarButton(R.string.save, new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (checkValid()) {
					//±£¥Êµÿ÷∑
					AVObject address = new AVObject("Address");
					address.put("user", AVUser.getCurrentUser());
					address.put("detail", tv_add_address.getText().toString());
					address.put("contact", tv_add_constacts.getText().toString());
					address.put("mobile", tv_add_phone.getText().toString());
					
				    getDefaultAddress(address);
				    
				}
			}
		});
		findViews();
	}

	
	
	private TextView tv_add_constacts;

	private TextView tv_add_phone;

	private TextView tv_add_address;

	private boolean validString(String s) {
		return s != null && s.length() != 0;
	}

	private boolean checkValid() {
		boolean pass = true;
		String constact = tv_add_constacts.getText().toString();
		String phone = tv_add_phone.getText().toString();
		String address = tv_add_address.getText().toString();
		if (!validString(constact)) {
			pass = false;
			Toast.makeText(this, R.string.not_constacts, Toast.LENGTH_SHORT).show();
		} else if (!validString(phone)) {
			pass = false;
			Toast.makeText(this, R.string.not_phone, Toast.LENGTH_SHORT).show();
		} else if (!validString(address)) {
			pass = false;
			Toast.makeText(this, R.string.not_address, Toast.LENGTH_SHORT).show();
		}
		return pass;
	}

	private void findViews() {
		tv_add_constacts = (TextView) findViewById(R.id.add_constacts);
		tv_add_phone = (TextView) findViewById(R.id.add_phone);
		tv_add_address = (TextView) findViewById(R.id.add_address);
	}
	private void getDefaultAddress(final AVObject address) {
		AVQuery<AVObject> query = AVQuery.getQuery("Address");
		query.whereEqualTo("delete",false);
		query.whereEqualTo("user", AVUser.getCurrentUser());
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 == null) {
					if(arg0.size()==0)
					{
					flag=true;	
					
					}
					address.put("delete",false);
					address.saveInBackground(new SaveCallback() {
						
						@Override
						public void done(AVException arg0) {
							// TODO Auto-generated method stub
							if(arg0==null){
							AddAddressActivity.this.finish();
							}
						}
					});
				}
				
			}
		});
	}
}
