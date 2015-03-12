package com.carandcoffee;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.SaveCallback;
import com.avos.avoscloud.SessionManager;
import com.buihha.audiorecorder.Mp3Recorder;
import com.carandcoffee.BaseActivity.DensityUtil;
import com.carandcoffee.application.CACApplication;
import com.carandcoffee.custom.Chattingrecords;
import com.carandcoffee.custom.CustomAdapter;
import com.carandcoffee.custom.ExtAudioRecorder;
import com.carandcoffee.custom.Messagelistener;
import com.carandcoffee.custom.MyAVmessage;
import com.carandcoffee.custom.Myanimation;
import com.carandcoffee.custom.Myreceiver;
import com.carandcoffee.custom.info;
import com.carandcoffee.view.phone_400_dialog;
import com.carandcoffee.view.selec_dialog;

@SuppressLint({ "CutPasteId", "NewApi", "HandlerLeak" })
public class ServiceActivity extends BaseActivity implements Messagelistener {

	final public static int PHOTO_REQUEST_GALLERY = 7;
	final public static int PHOTO_CAMERA = 8;
	private AVUser user;
	private EditText text;
	private List<String> peerIds = MainActivity.peerIds;
	private ListView chatlist;
	private List<HashMap<String, Object>> data;
	private Bitmap Myicon;
	private SimpleAdapter adapter;
	private ImageView voice;
	private MediaRecorder mRecorder;
	private MediaPlayer player = new MediaPlayer();
	private AVFile temp;
	private Handler main;
	private String path;
	private info_type now;
	private CACApplication app;
	private Myanimation myanimation;
	private Timer time;
	private Animation anim;
	private List<View> list = new ArrayList<View>();
	private int i = 0;
	private View animationlayout;
	private Button sendbutton;
	private Drawable exit;
	private Drawable send;
	private boolean ex = true;
	private Handler recoder;
	private String soundpath;
	private boolean isinlayout;
	private int seconds;
	private TextView timeshow_text;
	private TextView cancel_voice_text;
	private Chattingrecords text_manager;
	private ArrayList<JSONObject> chat_old;
	private int chat_old_index = 1;
	private int thefirstitem = 0;
	private int j;
	private String username;
	ExtAudioRecorder extRecorder;
	Mp3Recorder mp3recoder;
	TextView timestamp;;
	long currenttimestamp;
	phone_400_dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		app = (CACApplication) getApplication();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.service_activity);
		setBarTitle("正在转接客服");
		init();
		settimesteamp(System.currentTimeMillis());
		pop = new selec_dialog(this, R.style.dialog);
		pop.setCanceledOnTouchOutside(true);
		new recoderthread().start();

		data = new ArrayList<HashMap<String, Object>>();

		if (user == null) {
			BitmapDrawable dra = (BitmapDrawable) getResources().getDrawable(
					R.drawable.head_icon);
			Myicon = dra.getBitmap();
		} else {
			Myicon = geticon(username);
			if (Myicon == null) {
				downloadicon(username, null, null);
				Myicon = targeticon;
			}
		}
		targeticon = geticon("service_icon");
		if (targeticon == null)
			targeticon = ((BitmapDrawable) getResources().getDrawable(
					R.drawable.head_icon)).getBitmap();
		adapter = new CustomAdapter(this, data, R.layout.chat_listview_item,
				new String[] {}, new int[] {}, getLayoutInflater(), player,
				this);
		chatlist.setAdapter(adapter);
		Myreceiver.listerner = this;

		if (AVUser.getCurrentUser() != null && app.session == null) {
			app.MypeerID = AVUser.getCurrentUser().getString("peerId");
			SessionManager.getInstance(app.MypeerID).open(peerIds);
			app.saveInstallation(app.MypeerID);
			;
		} else if (AVUser.getCurrentUser() != null && app.session != null
				&& !app.session.isOpen()) {
			app.session.open(peerIds);
			app.saveInstallation(app.session.getSelfPeerId());
		}

		text.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (s.toString().length() == 0) {
					sendbutton.setBackgroundResource(R.drawable.exit);
					ex = true;
				} else {
					sendbutton.setBackground(send);
					ex = false;
				}

			}
		});

		chatlist.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

				thefirstitem = firstVisibleItem;
				if (firstVisibleItem < data.size()) {
					log.e("timestamp",
							"" + data.get(firstVisibleItem).get("timestamp"));
					compeara((Long) data.get(firstVisibleItem).get("timestamp"));
				}

			}

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}
		});
		chatlist.setOnTouchListener(new OnTouchListener() {
			boolean begin = false;

			float y;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				TextView text = (TextView) findViewById(R.id.note_text);
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (thefirstitem == 0) {
						y = event.getRawY();
						begin = true;
					} else
						begin = false;
					break;

				case MotionEvent.ACTION_UP:
					if (begin) {
						if (event.getRawY() - y > 400) {
							if (j != -1) {
								getold_map();
								Log.e("jzhi", "" + j);

							}
							Log.e("yzhi", "" + (event.getRawY() - y));

							text.setVisibility(View.GONE);
						}
					}
					break;
				case MotionEvent.ACTION_MOVE:
					if (begin) {
						text.setVisibility(View.GONE);
						if (event.getRawY() - y > 400) {
							if (j == -1)
								text.setText("没有消息了!");
							else
								text.setText("松开手指加载历史消息!");
							text.setVisibility(View.VISIBLE);
						}
					} else
						text.setVisibility(View.GONE);
					break;

				}

				return false;

			}
		});

		voice.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					seconds = 0;
					timeshow_text.setText("00:00");
					cancel_voice_text.setText(R.string.cancel_voice);
					soundpath = null;
					Message msg = recoder.obtainMessage();
					msg.what = 1;
					recoder.sendMessage(msg);
					myanimation.thefirststep();
					chatlist.setVisibility(View.GONE);
					timestamp.setVisibility(View.GONE);
					for (View p : list) {
						p.clearAnimation();
					}
					animationlayout.setVisibility(View.VISIBLE);
					break;
				case MotionEvent.ACTION_UP:

					isinlayout = isINlayout(event.getRawX(), event.getRawY(),
							animationlayout);
					Message temp = recoder.obtainMessage();
					temp.what = 2;
					recoder.sendMessage(temp);
				case MotionEvent.ACTION_MOVE:
					if (isINlayout(event.getRawX(), event.getRawY(),
							animationlayout)) {

						cancel_voice_text.setText("松开手指，取消发送");
					} else
						cancel_voice_text.setText(R.string.cancel_voice);

					break;

				}

				return true;
			}
		});

		main = new Handler() {

			int timeshow = 0;

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				if (msg.what == 3) {

					timeshow++;
					if (timeshow == 10) {
						seconds++;
						int fen = seconds / 60;
						int miao = seconds % 60;
						timeshow_text.setText("" + fen / 10 + "" + fen % 10
								+ ":" + miao / 10 + "" + miao % 10);
						timeshow = 0;
					}

				} else if (msg.what == 4) {
					log.e("在控件里", "" + isinlayout);
					if (!isinlayout && soundpath != null) {

						if (time != null)
							time.cancel();
						try {

							 player.reset();
							 player.setDataSource(soundpath);
							 player.prepare();
						     if (player.getDuration() >800) {
							 player.reset();
							if (peerIds.size() != 0) {
								HashMap<String, Object> map = addinfo(
										soundpath, type.RIGHT, info_type.VOICE);

								update();
								uploadvoice(soundpath, map);
							} else {
								Toast.makeText(getApplicationContext(),
										"没有客服在线", Toast.LENGTH_SHORT).show();
								dialog.show();
							}
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
					chatlist.setVisibility(View.VISIBLE);
					timestamp.setVisibility(View.VISIBLE);
					animationlayout.setVisibility(View.GONE);
					myanimation.thesecondtstep();

				} else {
					path = (String) (msg.obj);
					if (msg.arg1 != 1) {

						targeticon = Bytes2Bimap(DensityUtil.getBytes(path));
					} else {

						if (now == info_type.VOICE) {
							addinfo(path, type.LEFT, info_type.VOICE);
							update();
						} else if (now == info_type.IMAGE) {
							addinfo(path, type.LEFT, info_type.IMAGE);
							update();
						}
					}
				}

			}

		};

		if (peerIds==null||peerIds.size() == 0){
			getCustomerServicePeerID();
			if(peerIds==null)
			peerIds=new ArrayList<String>();
		}
			
		else {
			app.session.watchPeers(peerIds);
			setBarTitle(custom_service_nickname);
			log.e("custom_service_nickname", custom_service_nickname);
		}
		if(app.session!=null)
		log.e("selfpeerID", app.session.getSelfPeerId());
		getold_map();
		handllelixian();
		dialog = new phone_400_dialog(this, R.style.dialog);

	}

	public HashMap<String, Object> addinfo(String content, type _temp,
			info_type infotype) {
		JSONObject json = new JSONObject();
		HashMap<String, Object> temp = new HashMap<String, Object>();
		temp.put("type", _temp);
		temp.put("info", content);
		if (_temp == type.LEFT)
			temp.put("icon", targeticon);
		else
			temp.put("icon", Myicon);
		temp.put("info_type", infotype);
		if (infotype == info_type.VOICE && _temp == type.RIGHT) {
			temp.put("time_long", seconds);
		}
		if (Myreceiver.isflag)
			temp.put("status", "now");
		else
			temp.put("status", "fail");
		data.add(temp);

		try {
			long time = System.currentTimeMillis();
			json.put("type", _temp);
			json.put("info", content);
			json.put("info_type", infotype);
			json.put("time_long", seconds);
			json.put("timestamp", time);
			temp.put("timestamp", time);
			log.e("json数据穿000", json.toString());
			text_manager.add(json.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}

	public void update() {
		adapter.notifyDataSetChanged();
		chatlist.setSelection(chatlist.getBottom());
	}

	@Override
	public void handlemessage(info info) {
		// TODO Auto-generated method stub
		addinfo(info.getContent(), type.LEFT, info_type.TEXT);
		update();
		Log.e("abc", "处理了信息");
	}

	@Override
	public void handlvoicmessage(info info) {
		// TODO Auto-generated method stub

		Log.e("得到了正确的url", info.Content);

		now = info_type.VOICE;
		new downloadThread(info.Content).start();
	}

	@Override
	public void handlimagemessage(info info) {
		// TODO Auto-generated method stub

		now = info_type.IMAGE;
		new downloadThread(info.Content).start();
		Log.e("得到了图的正确的url", info.Content);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case PHOTO_REQUEST_GALLERY:
			if (data != null) {
				// 得到图片的全路径
				if (peerIds.size() != 0) {
					Uri uri = data.getData();
					String[] proj = { MediaStore.Images.Media.DATA };

					@SuppressWarnings("deprecation")
					Cursor actualimagecursor = managedQuery(uri, proj, null,
							null, null);
					int actual_image_column_index = actualimagecursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

					actualimagecursor.moveToFirst();

					String img_path = actualimagecursor
							.getString(actual_image_column_index);

					HashMap<String, Object> a = addinfo(img_path, type.RIGHT,
							info_type.IMAGE);
					uploadimage(img_path, a);
					update();
				} else {
					Toast.makeText(getApplicationContext(), "没有客服在线",
							Toast.LENGTH_SHORT).show();
					dialog.show();
				}

			}
			break;
		case PHOTO_CAMERA:
			if (data != null) {
				if (peerIds.size() != 0) {
					Bundle bu = data.getExtras();
					if (bu != null) {
						Bitmap bitmap = (Bitmap) bu.get("data");
						String pat = savefile(bitmap);
						HashMap<String, Object> a = addinfo(pat, type.RIGHT,
								info_type.IMAGE);

						uploadimage(pat, a);
						update();
					}

				} else {
					Toast.makeText(getApplicationContext(), "没有客服在线",
							Toast.LENGTH_SHORT).show();
					dialog.show();
				}

			}
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Myreceiver.listerner = null;
		if (app.session != null && peerIds != null && peerIds.size() != 0)
			app.session.unwatchPeers(peerIds);

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		List<RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
		if (runningTaskInfos.get(0).numRunning == 1) {
			Intent it = new Intent(this, MainActivity.class);
			startActivity(it);
			finish();
		} else
			super.onBackPressed();
	}

	public void uploadimage(String path, final HashMap<String, Object> map) {

		Bitmap bit = DensityUtil.getBitmapByBytes(DensityUtil.getBytes(path),
				null);
		temp = new AVFile(username + "_" + System.currentTimeMillis(),
				Bitmap2Bytes(bit));

		temp.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException arg0) {
				if (arg0 == null) {
					// TODO Auto-generated method stub
					log.e("abc", temp.getUrl());
					info info = new info();
					info.InfoType = MyAVmessage.IMAGE_INFOMATION;
					info.Content = temp.getUrl();
					info.Timestamp = System.currentTimeMillis();
					MyAVmessage msg = new MyAVmessage(info);
					AVMessage sen = new AVMessage(msg.getJsonString(), peerIds,
							false);

					Myreceiver.map.put(info.Timestamp, map);
					if (app.session != null)
						app.session.sendMessage(sen);
					else {
						map.put("status", "fail");
						update();
					}
				} else {
					map.put("status", "fail");
					update();
				}

			}
		});
	}

	public void uploadvoice(String path, final HashMap<String, Object> map) {
		try {
			temp = AVFile.withAbsoluteLocalPath(
					username + "_" + System.currentTimeMillis() + ".mp3", path);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		temp.saveInBackground(new SaveCallback() {

			@Override
			public void done(AVException arg0) {
				// TODO Auto-generated method stub
				if (arg0 == null) {
					log.e("abc", temp.getUrl());
					info info = new info();
					info.InfoType = MyAVmessage.VOICE_INFOMATION;
					info.Content = temp.getUrl();
					info.Timestamp = System.currentTimeMillis();
					MyAVmessage msg = new MyAVmessage(info);
					AVMessage sen = new AVMessage(msg.getJsonString(), peerIds,
							false);

					Myreceiver.map.put(info.Timestamp, map);
					if (app.session != null)
						app.session.sendMessage(sen);
					else {
						map.put("status", "fail");
						update();
					}
				} else {
					map.put("status", "fail");
					update();
				}
			}
		});

	}

	private Bitmap getBitmapFromUri(Uri uri)// 根据uri获取图片
	{
		try {
			// 读取uri所在的图片
			Bitmap bitmap = MediaStore.Images.Media.getBitmap(
					this.getContentResolver(), uri);
			return bitmap;
		} catch (Exception e) {
			Log.e("[Android]", e.getMessage());
			Log.e("[Android]", "目录为：" + uri);
			e.printStackTrace();
			return null;
		}
	}

	public void itemclick(View v) {
		switch (v.getId()) {
		case R.id.chang_to_text_layout:
			findViewById(R.id.send_more_message_layout)
					.setVisibility(View.GONE);
			findViewById(R.id.send_message_layout).setVisibility(View.VISIBLE);
			LayoutParams a = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			a.addRule(RelativeLayout.ABOVE, R.id.send_message_layout);
			chatlist.setLayoutParams(a);
			EditText editText = (EditText) findViewById(R.id.send_info);

			editText.setFocusableInTouchMode(true);

			editText.requestFocus();

			InputMethodManager inputManager =

			(InputMethodManager) editText.getContext().getSystemService(
					Context.INPUT_METHOD_SERVICE);

			inputManager.showSoftInput(editText, 0);
			flaginput = true;
			break;
		case R.id.send_text_message:
			if (ex != true) {

				String info = text.getText().toString();
				info mytext = new info();
				mytext.InfoType = MyAVmessage.TEXT_INFOMATION;
				mytext.Content = info;
				mytext.Timestamp = System.currentTimeMillis();
				MyAVmessage msg = new MyAVmessage(mytext);
				AVMessage message = new AVMessage(msg.getJsonString(), peerIds,
						false);
				message.setTimestamp(System.currentTimeMillis());
				if (peerIds.size() != 0) {
					HashMap<String, Object> b = addinfo(info, type.RIGHT,
							info_type.TEXT);
					Myreceiver.map.put(mytext.Timestamp, b);
					if (app.session != null)
						app.session.sendMessage(message);
					else {
						b.put("status", "fail");
						update();
					}

					log.e("av ID", "" + mytext.Timestamp);
					text.setText("");
					update();
				} else {
					Toast.makeText(getApplicationContext(), "没有客服在线",
							Toast.LENGTH_SHORT).show();
					dialog.show();
				}

			} else {
				InputMethodManager mage = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				mage.hideSoftInputFromWindow(v.getWindowToken(), 0);
				flaginput = false;

				main.postDelayed(new Runnable() {

					@Override
					public void run() {
						LayoutParams a2 = new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT);
						a2.addRule(RelativeLayout.ABOVE,
								R.id.send_more_message_layout);
						chatlist.setLayoutParams(a2);
						findViewById(R.id.send_more_message_layout)
								.setVisibility(View.VISIBLE);
						findViewById(R.id.send_message_layout).setVisibility(
								View.GONE);
					}
				}, 100);

			}
			break;
		case R.id.photo_camera:
			pop.show();
			flagpop = true;
			break;
		case R.id.promotion_to:
			Intent it = new Intent(this, PromotionActivity.class);
			startActivity(it);
			finish();
			break;
		case R.id.youli_huodong:
			Intent it1 = new Intent(this, youli_activity.class);
			startActivity(it1);
		}

	}

	public boolean isINlayout(float x, float y, View v) {
		int vloc[] = new int[2];

		v.getLocationOnScreen(vloc);

		float px = vloc[0];
		float py = vloc[1];
		float ex = vloc[0] + v.getWidth();
		float ey = vloc[1] + v.getHeight();
		if (x > px && x < ex && y > py && y < ey)
			return true;
		else
			return false;

	}

	public void init() {
		user = AVUser.getCurrentUser();
		if (user != null)
			username = user.getUsername();
		else
			username = "casual_user";
		text_manager = new Chattingrecords(username, getCacheDir()
				.getAbsolutePath());

		chat_old = text_manager.getjsonarray();
		j = chat_old.size() - 1;

		exit = getResources().getDrawable(R.drawable.exit);
		send = getResources().getDrawable(R.drawable.send_message);

		findViewById(R.id.titlebar_rightbutton).setVisibility(View.GONE);
		text = (EditText) findViewById(R.id.send_info);
		sendbutton = (Button) findViewById(R.id.send_text_message);
		sendbutton.setBackground(exit);
		cancel_voice_text = (TextView) findViewById(R.id.cancel_voice_text);
		timeshow_text = (TextView) findViewById(R.id.luying_time_show);
		chatlist = (ListView) findViewById(R.id.chat_listview);
		voice = (ImageView) findViewById(R.id.voice_send_bt);
		ImageView left_buttom = (ImageView) findViewById(R.id.chang_to_text_layout);
		ImageView right_buttom = (ImageView) findViewById(R.id.photo_camera);
		ImageView bowen_view = (ImageView) findViewById(R.id.dianboquan);
		ImageView bg = (ImageView) findViewById(R.id.bg);
		timestamp = (TextView) findViewById(R.id.time_stamp);
		ImageView yuandian1 = (ImageView) findViewById(R.id.yuandian1);
		animationlayout = findViewById(R.id.animation_layout);
		myanimation = new Myanimation(left_buttom, right_buttom, voice,
				bowen_view, bg);

	}

	public void getold_map() {

		type _temp;
		info_type infotype;
		for (; j >= 0 && j > chat_old.size() - 5 * chat_old_index - 1; j--) {
			HashMap<String, Object> temp = new HashMap<String, Object>();
			JSONObject a = chat_old.get(j);

			try {
				if (a.get("type").equals("LEFT"))
					_temp = type.LEFT;
				else
					_temp = type.RIGHT;
				if (a.get("info_type").equals("TEXT"))
					infotype = info_type.TEXT;
				else if (a.get("info_type").equals("VOICE"))
					infotype = info_type.VOICE;
				else
					infotype = info_type.IMAGE;
				temp.put("type", _temp);
				temp.put("info", a.get("info"));
				if (_temp == type.LEFT)
					temp.put("icon", targeticon);
				else
					temp.put("icon", Myicon);
				temp.put("info_type", infotype);
				if (infotype == info_type.VOICE) {
					temp.put("time_long", a.get("time_long"));
				}
				temp.put("timestamp", a.get("timestamp"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			data.add(0, temp);
		}
		if (j != -1) {
			adapter.notifyDataSetChanged();
			chatlist.setSelection(5);
			chat_old_index++;

		}
	}

	public void handllelixian() {
		ArrayList<info> lixian = (ArrayList<info>) Myreceiver.lixian;
		if (lixian.size() != 0) {
			for (info info : lixian) {
				if (info.getInfoType().equals(MyAVmessage.TEXT_INFOMATION))
					handlemessage(info);
				else if (info.getContent().equals(MyAVmessage.IMAGE_INFOMATION))
					handlimagemessage(info);
				else
					handlvoicmessage(info);
			}
			lixian.clear();
		}
	}

	public void getCustomerServicePeerID() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		if (AVUser.getCurrentUser() != null||app.session!=null) {
			parameters.put("peerId", app.session.getSelfPeerId());
			parameters.put("temp", "n");
		} else
			parameters.put("temp", "y");

		AVCloud.callFunctionInBackground("getOnlineCustomerService",
				parameters, new FunctionCallback<Object>() {
					public void done(Object object, AVException e) {
						if (e == null) {
							@SuppressWarnings("unchecked")
							HashMap<String, String> map = (HashMap<String, String>) object;
							if (AVUser.getCurrentUser() != null||app.session!=null) {
								peerIds.clear();
								String pid = map.get("cspid");
								log.e("peerID", map.toString());
								// Toast.makeText(getApplicationContext(),map.toString(),Toast.LENGTH_SHORT).show();
								if (pid != null) {
									peerIds.add(pid);
									app.session.watchPeers(peerIds);

									downloadicon(map.get("cspid"), null,
											titleView);
								} else {
									TextView text = (TextView) findViewById(R.id.title_textview);
									text.setText("无客服在线");
									dialog.show();
								}

							} else {
								
								app.MypeerID = map.get("tempPeerId");
								app.session = SessionManager
										.getInstance(app.MypeerID);
								
								peerIds.clear();
								String pid = map.get("operatorPeerId");
								// Toast.makeText(getApplicationContext(),map.toString(),Toast.LENGTH_SHORT).show();
								if (pid != null) {
									peerIds.add(pid);
									app.session.open(peerIds);
									app.saveInstallation(app.MypeerID);
                                    app.writeshar(app.MypeerID);
									downloadicon(map.get("operatorPeerId"),
											null, titleView);
								} else {
									TextView text = (TextView) findViewById(R.id.title_textview);
									text.setText("无客服在线");

									dialog.show();
								}

							}
							log.e("晕代码代销", map.toString());
						} else {
							setBarTitle("转接失败,正在重新转接");
							main.postDelayed(new Runnable() {

								@Override
								public void run() {
									// TODO Auto-generated method stub
									getCustomerServicePeerID();
								}
							}, 500);

						}
					}
				});
	}

	public void settimesteamp(long time) {
		currenttimestamp = time;
		Date date = new Date(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		timestamp.setText(calendar.get(Calendar.YEAR) + "/"
				+ (calendar.get(Calendar.MONTH) + 1) + "/"
				+ calendar.get(Calendar.DAY_OF_MONTH));
	}

	public void compeara(long newtime) {
		if (Math.abs(currenttimestamp - newtime) >= 86400000) {
			currenttimestamp = newtime;
			Date date = new Date(newtime);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			timestamp.setText(calendar.get(Calendar.YEAR) + "/"
					+ (calendar.get(Calendar.MONTH) + 1) + "/"
					+ calendar.get(Calendar.DAY_OF_MONTH));
		}
	}

	public class downloadThread extends Thread {
		URL url;
		String ur;

		downloadThread(String ur) {
			this.ur = ur;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Message msg = main.obtainMessage();
			msg.obj = writeFromInput(
					getCacheDir().getAbsolutePath(),
					"" + System.currentTimeMillis() + "."
							+ DensityUtil.parseSuffix(ur), getInputStream(ur))
					.getAbsolutePath();
			msg.arg1 = 1;
			main.sendMessage(msg);

		}

		public InputStream getInputStream(String urlStr) {
			InputStream is = null;
			try {
				url = new URL(urlStr);
				HttpURLConnection urlConn = (HttpURLConnection) url
						.openConnection();
				urlConn.setRequestProperty("Connection", "close");
				urlConn.connect();
				is = urlConn.getInputStream();

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return is;
		}

		public File writeFromInput(String path, String fileName,
				InputStream input) {
			File file = null;
			OutputStream output = null;
			try {

				file = new File(path, fileName);
				output = new FileOutputStream(file);
				byte buffer[] = new byte[4 * 1024];
				// while ((input.read(buffer)) != -1) {
				// output.write(buffer);
				// }

				while (true) {
					int temp = input.read(buffer, 0, buffer.length);
					if (temp == -1) {
						break;
					}
					output.write(buffer, 0, temp);
				}

				output.flush();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					output.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return file;
		}
	}

	public class recoderthread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			Looper.prepare();
			recoder = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					switch (msg.what) {
					case 1:
						File soundFile = new File(getCacheDir(),
								System.currentTimeMillis() + ".mp3");

						// 设置录音的声音来源
						/*
						 * mRecorder = new MediaRecorder();
						 * mRecorder.setAudioSource
						 * (MediaRecorder.AudioSource.MIC); //
						 * 设置录制的声音的输出格式（必须在设置声音编码格式之前设置） mRecorder
						 * .setOutputFormat
						 * (MediaRecorder.OutputFormat.AAC_ADTS); // 设置声音编码的格式
						 * mRecorder
						 * .setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
						 * 
						 * mRecorder.setOutputFile(soundFile.getAbsolutePath());
						 * try { mRecorder.prepare(); } catch
						 * (IllegalStateException e) { // TODO Auto-generated
						 * catch block e.printStackTrace(); } catch (IOException
						 * e) { // TODO Auto-generated catch block
						 * e.printStackTrace(); } // 开始录音 mRecorder.start();
						 */
						mp3recoder = new Mp3Recorder();
						try {
							mp3recoder.startRecording(soundFile
									.getAbsolutePath());
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}

						time = new Timer();
						time.schedule(new TimerTask() {

							@Override
							public void run() {

								// TODO Auto-generated method stub
								/*
								 * float y = (float)
								 * mRecorder.getMaxAmplitude(); if (y != 0) {
								 * double db = 20 * Math.log10(y / 600f);
								 * Log.e("qiangdu", "" + db); anim =
								 * AnimationUtils.loadAnimation(
								 * getApplicationContext(), R.anim.daodi);
								 */

								Message msg = Message.obtain();
								// msg.obj = (int) db;
								msg.obj = 10;
								msg.what = 3;
								main.sendMessage(msg);
								// Log.e("mp3强度",""+mp3recoder.getqiangdu());
							}

						}, 500, 100);
						soundpath = soundFile.getAbsolutePath();

						break;

					case 2:
						try {
							Thread.currentThread().sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						// mRecorder.stop();
						try {
							mp3recoder.stopRecording();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Message a = main.obtainMessage();
						a.what = 4;
						main.sendMessage(a);
						break;
					}
				}

			};
			Looper.loop();
		}
	}

	public enum type {
		LEFT, RIGHT
	}

	public enum info_type {
		TEXT, VOICE, IMAGE
	}

}
