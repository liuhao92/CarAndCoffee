package com.carandcoffee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.carandcoffee.adapter.AddressAdapter;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class AddressActivity extends BaseActivity {
	ArrayList<Map<String, Object>> listItemsData;
	AddressAdapter adapter;
	public static AVObject currentaddress = null;
	public static int ADD_ADDRESS_CODE = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		setBarTitle(R.string.choose_service_address);
		setBarButton(R.string.address_edit, new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(AddressActivity.this,
						EditAddressActivity.class);
				i.putExtra("PROMOTIONNO", (CharSequence) v.getTag());
				startActivity(i);
			}
		});
		findViews();
		init();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getaddress(AVUser.getCurrentUser());
	}

	private void init() {
		listItemsData = new ArrayList<Map<String, Object>>();

		adapter = new AddressAdapter(this, listItemsData,
				R.layout.address_item, new String[] { "tv_name", "tv_phone",
						"tv_service_address" }, new int[] { R.id.tv_name,
						R.id.tv_phone, R.id.tv_service_address });
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				adapter.setcPosition(position);
				adapter.notifyDataSetChanged();
				OrderCommitActivity.currentaddress = currentaddress = (AVObject) listItemsData
						.get(position).get("AVobject");
			}
		});
		listView.setAdapter(adapter);
		addAdressBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(AddressActivity.this,
						AddAddressActivity.class);
				startActivity(i);
			}
		});
	}

	private ListView listView;
	private Button addAdressBtn;

	private void findViews() {
		listView = (ListView) findViewById(R.id.lv_address);
		addAdressBtn = (Button) findViewById(R.id.btn_address_add);
	}

	public void getaddress(AVUser user) {
		AVQuery<AVObject> query = AVQuery.getQuery("Address");

		query.orderByDescending("createdAt");
		query.whereEqualTo("delete",false);
		query.whereEqualTo("user", user);

		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				listItemsData.clear();
				if (arg0.size() != 0) {
					OrderCommitActivity.currentaddress = currentaddress = arg0
							.get(0);
					
					for (AVObject address : arg0) {
						Map<String, Object> item = new HashMap<String, Object>();
						item.put("tv_name", address.get("contact"));
						String str=(String) address.get("mobile");
						if(address.getString("defaultFlag").equals("1"))
							str+="     [д╛хо]";
                
						item.put("tv_phone",str);
						item.put("tv_service_address",
								address.getString("detail"));
						item.put("AVobject", address);
						listItemsData.add(item);
					}
				}
				Log.e("dizhi", arg0.toString());
				adapter.notifyDataSetChanged();

				if (listItemsData.size() == 0) {
					Intent it = new Intent(getApplicationContext(),
							AddAddressActivity.class);
					startActivity(it);
				}
			}
		});

	}

	@Override
	public void onBackPressed() {
		overridePendingTransition(R.anim.in_from__left, R.anim.out_to_right);
		OrderCommitActivity.address = currentaddress;
		setResult(OrderCommitActivity.ADDRESS_REQUEST_CODE);
		super.onBackPressed();
	}
}
