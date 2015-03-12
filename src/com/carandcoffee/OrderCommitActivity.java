package com.carandcoffee;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.SaveCallback;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class OrderCommitActivity extends BaseActivity {
	public static AVObject currentaddress = null;
	public static final String ORDERNO = "com.carandcoffee.OrderCommitActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_order_commit);
		setBarTitle(R.string.input_contacts_info);
		
		dateFormat = new SimpleDateFormat("yyyy" + (R.string.year) + "MM"
				+ getString(R.string.month) + "dd" + getString(R.string.day));
		findViews();
		init();
	}

	public static AVObject order;
	public static AVObject address;
	public static AVObject car;

	private void init() {
		String objId = getIntent().getStringExtra(ORDERNO);
		getOrder(objId);
	}

	private void getOrder(String id) {
		AVQuery<AVObject> query = AVQuery.getQuery("Package");
		query.whereEqualTo("objectId", id);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 == null) {
					order = arg0.get(0);
					getDefaultAddress();
				}
			}
		});
	}

	private void getDefaultAddress() {
		AVQuery<AVObject> query = AVQuery.getQuery("Address");
		query.whereEqualTo("delete",false);
		query.whereEqualTo("defaultFlag","1");
		query.whereEqualTo("user", AVUser.getCurrentUser());
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				
				if (arg1 == null) {
					address = arg0.get(0);
					
					getDefaultcar();
				}
				initData();
			}
		});
	}
	private void getDefaultcar() {
		AVQuery<AVObject> query = AVQuery.getQuery("Car");
		query.whereEqualTo("user", AVUser.getCurrentUser());
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				if (arg1 == null&&arg0.size()!=0) {
				car=arg0.get(0);
				}
				
			}
		});
	}

	private void initData() {
		if (address == null) {
			Intent intent = new Intent(this, AddAddressActivity.class);
			startActivityForResult(intent, ADDRESS_ADD_CODE);
			
		} else {
			tv_name.setText(address.getString("contact"));
			tv_phone.setText(address.getString("mobile"));
			tv_address.setText(address.getString("detail"));
		}
	}

	private TextView tv_name, tv_phone, tv_address, tv_preset_date,
			tv_preset_time;

	private ImageView iv_next_address;

	private Button btn_commit;

	private void findViews() {
		tv_name = (TextView) findViewById(R.id.tv_name);
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		tv_address = (TextView) findViewById(R.id.tv_service_address);
		tv_preset_date = (TextView) findViewById(R.id.et_year);
		tv_preset_time = (TextView) findViewById(R.id.time);
		
		btn_commit = (Button) findViewById(R.id.bt_configCommit);
	}

	private Dialog createTimeDialog() {
		String time = tv_preset_time.getText().toString();
		int hour = 0;
		int minute = 0;
		if (time != null && time.trim().length() != 0) {
			hour = Integer.parseInt(time.substring(0,
					time.indexOf(getString(R.string.hour))));
			minute = Integer.parseInt(time.substring(
					time.indexOf(getString(R.string.hour)) + 1,
					time.indexOf(getString(R.string.minute))));
		}
		TimePickerDialog timePickerDialog = new TimePickerDialog(this,
				new OnTimeSetListener() {
					@Override
					public void onTimeSet(TimePicker view, int hourOfDay,
							int minute) {
						tv_preset_time.setText(hourOfDay
								+ getString(R.string.hour) + minute
								+ getString(R.string.minute));
					}
				}, hour, minute, true);
		timePickerDialog.setTitle(getTitle());
		timePickerDialog.setCancelable(true);
		return timePickerDialog;
	}

	private SimpleDateFormat dateFormat;

	private Dialog createDialog() {
		Date date = null;
		try {
			date = dateFormat.parse(tv_preset_date.getText().toString());
		} catch (ParseException e) {
			date = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		final int year = c.get(Calendar.YEAR);
		final int month = c.get(Calendar.MONTH);
		final int day = c.get(Calendar.DAY_OF_MONTH);
		DatePickerDialog dialogBuilder = new DatePickerDialog(this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int y, int m, int d) {
						m += 1;
						String dateMatter = String.valueOf(year)
								+ getString(R.string.year)
								+ String.valueOf(month)
								+ getString(R.string.month)
								+ String.valueOf(day) + getString(R.string.day);
						if (checkDOBValid(y, m, d)) {
							dateMatter = y + getString(R.string.year) + m
									+ getString(R.string.month) + d
									+ getString(R.string.day);
							tv_preset_date.setText(dateMatter);
						}
					}
				}, year, month, day);
		dialogBuilder.setTitle(getTitle());
		dialogBuilder.setCancelable(true);
		return dialogBuilder;
	}

	public static final int ADDRESS_REQUEST_CODE = 1;
	public static final int ADDRESS_ADD_CODE = 2;
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.et_year:
			createDialog().show();
			break;
		case R.id.time:
			createTimeDialog().show();
			break;
		case R.id.iv_next:
			Intent i = new Intent(this, AddressActivity.class);
			startActivityForResult(i, ADDRESS_REQUEST_CODE);
			break;
		case R.id.bt_configCommit:

			AVObject order_av = new AVObject("Order");
			order_av.put("status", "unconfirmed");
			order_av.put("package", order);
			order_av.put("user", AVUser.getCurrentUser());
			order_av.put("address", address);
			order_av.put("car", car);
			
			log.e("order.getNumber", ""+order.getNumber("price"));
			Date date= getServiceDate();
			order_av.put("serviceTime", date);
			if(date!=null){
			order_av.saveInBackground(new SaveCallback() {

				@Override
				public void done(AVException arg0) {
					// TODO Auto-generated method stub
					if (arg0 == null) {
						Intent it = new Intent(getApplicationContext(),
								MyorderActivity.class);
						Toast.makeText(getApplicationContext(), "下单成功",
								Toast.LENGTH_SHORT).show();
						startActivity(it);
						finish();
					} else {
						Toast.makeText(getApplicationContext(), "下单失败",
								Toast.LENGTH_SHORT).show();
					}
				}
			});}
			else
				Toast.makeText(this, "请填写服务时间",Toast.LENGTH_SHORT).show();

			break;
		default:
			break;
		}
	}

	private boolean checkDOBValid(int y, int m, int d) {
		Calendar cal = Calendar.getInstance();
		cal.set(y, m, d);
		Calendar cal2 = Calendar.getInstance();
		return cal.after(cal2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ADDRESS_REQUEST_CODE) {
			initAddress();
		}
		else if(requestCode==ADDRESS_ADD_CODE){
			getDefaultAddress();
		}

	}

	private void initAddress() {
		tv_name.setText(address.getString("contact"));
		tv_phone.setText(address.getString("mobile"));
		tv_address.setText(address.getString("detail"));
	}

	public Date getServiceDate() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(tv_preset_date.getText().toString());
		buffer.append(tv_preset_time.getText().toString());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy"
				+ getString(R.string.year) + "MM" + getString(R.string.month)
				+ "dd" + getString(R.string.day) + "HH"
				+ getString(R.string.hour) + "mm" + getString(R.string.minute));
		try {
			return dateFormat.parse(buffer.toString());
		} catch (Exception e) {
			return null;
		}
	}
}
