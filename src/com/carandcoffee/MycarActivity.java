package com.carandcoffee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SessionManager;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.SaveCallback;
import com.carandcoffee.ServiceActivity.info_type;
import com.carandcoffee.ServiceActivity.type;
import com.carandcoffee.application.CACApplication;
import com.carandcoffee.view.CircleImageView;
import com.carandcoffee.view.phone_400_dialog;
import com.carandcoffee.view.selec_dialog;

import android.R.bool;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.drm.DrmStore.RightsStatus;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class MycarActivity extends BaseActivity {
	@SuppressLint("NewApi")
	final static int LOGIN = 0;
	final static int MY_ACCOUNT = 1;
	final static int PHOTO_REQUEST_GALLERY = 2;
	private static final int PHOTO_REQUEST_CUT = 3;// 结果
	View login_layout;
	View login_success_layout;
	private Bitmap bitmap;
	private Bitmap temp;
	CircleImageView icon;
	Handler main;
	Handler children;
	Handler post;
	boolean result = false;
	CACApplication app;
	List<HashMap<String, Object>> listsdate;
	ListAdapter listadpter;
	View tijian_layout;
	TextView checktime;
	boolean select_head_icon = false;
	selec_dialog pol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.mycar_activity);
		app = (CACApplication) getApplication();

		pol = new selec_dialog(this, R.style.dialog);
		pol.setCanceledOnTouchOutside(true);

		setBarTitle(R.string.my_car_string);

		Button right = (Button) findViewById(R.id.titlebar_rightbutton);
		right.setVisibility(View.VISIBLE);
		right.setText(getString(R.string.customer_service));

		right.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(MycarActivity.this,
						ServiceActivity.class);
				startActivity(it);
				finish();
			}
		});
		ListView checklistview = (ListView) findViewById(R.id.checklist);

		tijian_layout = findViewById(R.id.tijian_layout);
		login_layout = findViewById(R.id.login_layout);

		login_success_layout = findViewById(R.id.login_succed_layout);
		checktime = (TextView) findViewById(R.id.check_time);
		icon = (CircleImageView) findViewById(R.id.head_icon);
		String[] checkname = getResources().getStringArray(
				R.array.checklist_name);
		listsdate = new ArrayList<HashMap<String, Object>>();

		listadpter = new SimpleAdapter(this, listsdate,
				R.layout.checklist_item,
				new String[] { "check_name", "check" }, new int[] {
						R.id.check_text, R.id.checkBox });
		checklistview.setItemsCanFocus(false);
		checklistview.setAdapter(listadpter);
		post = new Handler();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case LOGIN:
			if (resultCode == LoginActivity.LOGIN_SUCCESS) {
				login_layout.setVisibility(View.GONE);
				login_success_layout.setVisibility(View.VISIBLE);
				TextView text = (TextView) login_success_layout
						.findViewById(R.id.yonghuming);

				text.setText(AVUser.getCurrentUser().getString("nickname"));
				downloadicon(AVUser.getCurrentUser().getUsername(), icon, null);
				if (app.session != null) {
					app.session.close();
					post.postDelayed(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							app.MypeerID = AVUser.getCurrentUser().getString(
									"peerId");
							app.session = SessionManager
									.getInstance(app.MypeerID);
							app.session.open(new ArrayList<String>());
							app.saveInstallation(app.MypeerID);
						}
					}, 500);

				}
				getchecklist(AVUser.getCurrentUser());
			}
			break;
		case MY_ACCOUNT:
			if (resultCode == RESULT_OK) {
				login_layout.setVisibility(View.VISIBLE);
				login_success_layout.setVisibility(View.GONE);
			}
			break;
		case PHOTO_REQUEST_GALLERY:
			if (data != null) {
				// 得到图片的全路径
				Uri uri = data.getData();
				crop(uri);
			}
			break;
		case ServiceActivity.PHOTO_REQUEST_GALLERY:
			if (data != null) {
				// 得到图片的全路径
				Uri uri = data.getData();
				String[] proj = { MediaStore.Images.Media.DATA };

				@SuppressWarnings("deprecation")
				Cursor actualimagecursor = managedQuery(uri, proj, null, null,
						null);
				int actual_image_column_index = actualimagecursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				actualimagecursor.moveToFirst();

				String img_path = actualimagecursor
						.getString(actual_image_column_index);
				temp = Bytes2Bimap(DensityUtil.getBytes(img_path));
				AVFile headicon = new AVFile("headicon", Bitmap2Bytes(temp));
				uploadicon(AVUser.getCurrentUser().getUsername(), headicon);
			}
			select_head_icon = false;
			break;

		case ServiceActivity.PHOTO_CAMERA:
		case PHOTO_REQUEST_CUT:
			if (data != null) {
				// temp = data.getParcelableExtra("data");
				Bundle bu = data.getExtras();
				if (bu != null) {
					temp = (Bitmap) bu.get("data");
					AVFile headicon = new AVFile("headicon", Bitmap2Bytes(temp));
					uploadicon(AVUser.getCurrentUser().getUsername(), headicon);
				}

			}
			select_head_icon = false;
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		AVUser current = AVUser.getCurrentUser();

		if (current != null) {
			if (!select_head_icon)
				getchecklist(current);
			login_layout.setVisibility(View.GONE);
			login_success_layout.setVisibility(View.VISIBLE);
			TextView text = (TextView) login_success_layout
					.findViewById(R.id.yonghuming);
			String nickname = current.getString("nickname");
			text.setText(nickname);
			bitmap = geticon(current.getUsername());
			if (bitmap != null)
				icon.setImageBitmap(bitmap);
			else {
				downloadicon(current.getUsername(), icon, null);
			}

		} else
			tijian_layout.setVisibility(View.GONE);
	}

	public void Itemclick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.button1:
			intent.setClass(this, LoginActivity.class);
			startActivityForResult(intent, LOGIN);
			break;
		case R.id.login_succed_layout:
			intent.setClass(this, MyaccountActivity.class);
			startActivityForResult(intent, MY_ACCOUNT);
			break;
		case R.id.head_icon:
			intent.setAction(Intent.ACTION_PICK);
			intent.setType("image/*");
			// startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
			pol.show();
			select_head_icon = true;
			break;
		}
	}

	public void uploadicon(final String username, final AVFile icon) {
		final AVUser user = AVUser.getCurrentUser();
		user.put("avatar", icon);
		user.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException arg0) {
				// TODO Auto-generated method stub
				if (arg0 == null) {
					Toast.makeText(getApplicationContext(), "保存更改成功",
							Toast.LENGTH_SHORT).show();
					saveicon(user.getUsername(), temp);
					MycarActivity.this.icon.setImageBitmap(temp);
				} else
					Toast.makeText(getApplicationContext(), "保存更改失败",
							Toast.LENGTH_SHORT).show();

			}
		});
	}

	public void getchecklist(AVUser user) {
		if (user != null) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("userid", user.getObjectId());
			AVCloud.callFunctionInBackground("getHealthCheckList", parameters,
					new FunctionCallback<Object>() {

						@Override
						public void done(Object arg0, AVException arg1) {
							// TODO Auto-generated method stub
							@SuppressWarnings("unchecked")
							ArrayList<Object> d = (ArrayList<Object>) arg0;
							if (arg1 == null && d.size() != 0) {

								listsdate.clear();
								tijian_layout.setVisibility(View.VISIBLE);
								log.e("tijiandan", arg0.toString());

								@SuppressWarnings("unchecked")
								HashMap<String, Object> data = (HashMap<String, Object>) d
										.get(0);

								@SuppressWarnings("unchecked")
								ArrayList<Object> array = (ArrayList<Object>) data
										.get("items");
								String time = (String) data.get("createdAt");
								time tim = MyorderActivity.gettimestring(time);
								checktime.setText(tim.year);
								for (Object i : array) {
									@SuppressWarnings("unchecked")
									ArrayList<Object> temp = (ArrayList<Object>) i;
									HashMap<String, Object> temp_data = new HashMap<String, Object>();
									temp_data.put("check_name",
											(array.indexOf(i) + 1) + "."
													+ temp.get(0).toString());
									String str = temp.get(1).toString();
									if (str.equals("true"))
										temp_data.put("check", true);
									else if (str.equals("false"))
										temp_data.put("check", false);
									listsdate.add(temp_data);
									log.e("tijiandan", temp.get(1).toString());
								}
								SimpleAdapter a = (SimpleAdapter) listadpter;
								a.notifyDataSetChanged();
							} else {
								tijian_layout.setVisibility(View.GONE);
							}
						}
					});
		}
	}

	/**
	 * 剪切图片
	 * 
	 * @function:
	 * @author:Jerry
	 * @date:2013-12-30
	 * @param uri
	 */
	private void crop(Uri uri) {
		// 裁剪图片意图
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// 裁剪框的比例，1：1
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// 裁剪后输出图片的尺寸大小
		intent.putExtra("outputX", 250);
		intent.putExtra("outputY", 250);
		// 图片格式
		intent.putExtra("outputFormat", "JPEG");
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:不返回uri，false：返回uri
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

}
