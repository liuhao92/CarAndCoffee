package com.carandcoffee;

import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.SaveCallback;

public class EditAddressActivity extends BaseActivity {

	EditText edit_contact;
	EditText edit_mobile;
	EditText edit_address_detail;

	AVObject obj;

	public static AVObject address;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address_edit);
		setBarTitle(R.string.edit_service_address);
		setBarButton(R.string.save, new OnClickListener() {
			@Override
			public void onClick(View v) {

				AddressActivity.currentaddress.put("contact", edit_contact
						.getText().toString());
				AddressActivity.currentaddress.put("mobile", edit_mobile
						.getText().toString());
				AddressActivity.currentaddress.put("detail",
						edit_address_detail.getText().toString());
				AddressActivity.currentaddress.saveInBackground();
				finish();

			}
		});
		findViews();
		init();
	}

	private void init() {

		if (AddressActivity.currentaddress != null) {
			edit_contact.setText(AddressActivity.currentaddress
					.getString("contact"));
			edit_mobile.setText(AddressActivity.currentaddress
					.getString("mobile"));
			edit_address_detail.setText(AddressActivity.currentaddress
					.getString("detail"));
		}

	}

	private void findViews() {

		edit_contact = (EditText) findViewById(R.id.edit_contact);
		edit_mobile = (EditText) findViewById(R.id.edit_mobile);
		edit_address_detail = (EditText) findViewById(R.id.edit_address_detail);

	}

	public void delete(View v) {
		if (AddressActivity.currentaddress != null) {
			String str=(String) AddressActivity.currentaddress.get("defaultFlag");
			if(!str.equals("1")){
			AddressActivity.currentaddress.put("delete",true);
			AddressActivity.currentaddress.saveInBackground(new SaveCallback() {

				@Override
				public void done(AVException arg0) {
					// TODO Auto-generated method stub
					if (arg0 == null) {
						Toast.makeText(getApplicationContext(), "删除成功",
								Toast.LENGTH_SHORT).show();

						AddressActivity.currentaddress = null;
						finish();
					} else
						Toast.makeText(getApplicationContext(), "删除失败",
								Toast.LENGTH_SHORT).show();

				}
			});}
			else
				Toast.makeText(this,"默认地址无法删除，请设置其他地址为默认再删除",Toast.LENGTH_LONG).show();
			// .deleteInBackground(new DeleteCallback() {
			//
			// @Override
			// public void done(AVException arg0) {
			// // TODO Auto-generated method stub
			// if (arg0 == null) {
			// Toast.makeText(getApplicationContext(), "删除成功",
			// Toast.LENGTH_SHORT).show();
			//
			// AddressActivity.currentaddress = null;
			// finish();
			// } else
			// Toast.makeText(getApplicationContext(), "删除失败",
			// Toast.LENGTH_SHORT).show();
			// }
			// });

		}
	}

	public void setdefault(View v) {
		if (AddressActivity.currentaddress != null) {
			getDefaultAddress();
			AddressActivity.currentaddress.put("defaultFlag", "1");
			AddressActivity.currentaddress.saveInBackground(new SaveCallback() {

				@Override
				public void done(AVException arg0) {
					// TODO Auto-generated method stub
					if (arg0 == null) {
						Toast.makeText(getApplicationContext(), "设置成功",
								Toast.LENGTH_SHORT).show();
					}
				}
			});

		}

	}

	private void getDefaultAddress() {
		AVQuery<AVObject> query = AVQuery.getQuery("Address");
		query.whereEqualTo("defaultFlag", "1");
		query.whereEqualTo("user", AVUser.getCurrentUser());
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {

				log.e("arg", arg0.size() + "");
				if (arg1 == null && arg0.size() != 0) {

					obj = arg0.get(0);
					obj.put("defaultFlag", "0");
					obj.saveInBackground();
				}

			}
		});
	}

}
