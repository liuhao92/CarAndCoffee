package com.carandcoffee.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.parser.JSONToken;
import com.avos.avoscloud.AVMessage;
import com.avos.avoscloud.AVMessageReceiver;
import com.avos.avoscloud.LogUtil.log;
import com.avos.avoscloud.Session;
import com.carandcoffee.MainActivity;
import com.carandcoffee.R;
import com.carandcoffee.ServiceActivity;

public class Myreceiver extends AVMessageReceiver {
	public static Messagelistener listerner = null;
	public static List<info> lixian = new ArrayList<info>();
	public static Map<Long, HashMap<String, Object>> map = new HashMap<Long, HashMap<String, Object>>();
	public static boolean isflag = true;

	@Override
	public void onError(Context arg0, Session arg1, Throwable arg2) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onMessage(Context arg0, Session arg1, AVMessage arg2) {
		// TODO Auto-generated method stub

		NotificationManager mNotificationManager = (NotificationManager) arg0
				.getSystemService(Context.NOTIFICATION_SERVICE);
		MyAVmessage msg = new MyAVmessage(arg2);
		info info = msg.getInfo();
		if (listerner != null) {

			log.e("abc", info.getInfoType());
			if (info.getInfoType().equals(MyAVmessage.TEXT_INFOMATION))
				listerner.handlemessage(info);
			else if (info.getInfoType().equals(MyAVmessage.IMAGE_INFOMATION))
				listerner.handlimagemessage(info);
			else if (info.getInfoType().equals(MyAVmessage.VOICE_INFOMATION))
				listerner.handlvoicmessage(info);

			log.e("abc", arg2.getMessage());
		} else {
			Notification notification = new Notification(
					R.drawable.ic_launcher, "您有新消息", System.currentTimeMillis());
			notification.defaults |= Notification.DEFAULT_VIBRATE; 
			Intent a[] = new Intent[1];
			a[0] = new Intent(arg0, ServiceActivity.class);
			PendingIntent intent = PendingIntent.getActivities(arg0, 0, a, 0);
			String content = "";
			if (info.getInfoType().equals(MyAVmessage.TEXT_INFOMATION))
				content = arg2.getFromPeerId() + "(客服):" + info.getContent();

			else if (info.getInfoType().equals(MyAVmessage.IMAGE_INFOMATION))
				content = "[图片]";
			else
				content = "[语音]";
			notification.setLatestEventInfo(arg0, "车与咖啡", content, intent);
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			mNotificationManager.notify(R.string.app_name, notification);
			lixian.add(info);
		}
		log.e("abc", arg2.getMessage() + listerner);
	}

	@Override
	public void onMessageFailure(Context arg0, Session arg1, AVMessage arg2) {
		// TODO Auto-generated method stub
		log.e("abc", "onMessageFailure");
		long time = 0;
		try {
			JSONObject obj = new JSONObject(arg2.getMessage());
			time = obj.getLong("Timestamp");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("time", "" + arg2.getTimestamp());
		if (time != 0) {
			log.e("abc",
					"" + arg2.getTimestamp() + arg2.getFromPeerId()
							+ arg2.getMessage());
			HashMap<String, Object> data = map.get(time);
			data.put("status", "fail");
			ServiceActivity activity = (ServiceActivity) listerner;
			activity.update();
			map.remove(time);
		}
	}

	@Override
	public void onMessageSent(Context arg0, Session arg1, AVMessage arg2) {
		// TODO Auto-generated method stub
		log.e("abc", "onMessageSent");
		long time = 0;
		try {
			JSONObject obj = new JSONObject(arg2.getMessage());
			time = obj.getLong("Timestamp");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.e("time", "" + arg2.getTimestamp());
		if (time != 0) {
			log.e("abc",
					"" + arg2.getTimestamp() + arg2.getFromPeerId()
							+ arg2.getMessage());
			HashMap<String, Object> data = map.get(time);
			data.put("status", "successed");
			ServiceActivity activity = (ServiceActivity) listerner;
			activity.update();
			map.remove(time);
		}

	}

	@Override
	public void onSessionOpen(Context arg0, Session arg1) {
		// TODO Auto-generated method stub
		log.e("av", "onSessionOpen");
	}

	@Override
	public void onSessionPaused(Context arg0, Session arg1) {
		// TODO Auto-generated method stub
		Log.e("av", "onSessionPaused");
		isflag = false;
	}

	@Override
	public void onSessionResumed(Context arg0, Session arg1) {
		// TODO Auto-generated method stub
		log.e("av", "onSessionResumed");
		isflag = true;
	}

	@Override
	public void onStatusOffline(Context arg0, Session arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusOnline(Context arg0, Session arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

}
