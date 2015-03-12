package com.carandcoffee.custom;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.avos.avoscloud.AVMessage;

public class MyAVmessage {
	public static String TEXT_INFOMATION = "TEXT";
	public static String IMAGE_INFOMATION = "IMAGE";
	public static String VOICE_INFOMATION = "VOICE";
	private String jsonString="";
	private info info=new info();

	public MyAVmessage(AVMessage msg) {
		// TODO Auto-generated constructor stub

		JSONTokener paser = new JSONTokener(msg.getMessage());

		try {
			JSONObject ob = (JSONObject) paser.nextValue();
			info.setInfoType(ob.getString("InfoType"));
			info.setContent(ob.getString("Content"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public MyAVmessage(info info) {
		// TODO Auto-generated constructor stub
		this.info = info;
		JSONObject json = new JSONObject();
		try {
			json.put("InfoType", info.InfoType);
			json.put("Content", info.Content);
			json.put("Timestamp",info.Timestamp);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		jsonString = "" + json;

	}

	public String getJsonString() {
		return jsonString;
	}

	public info getInfo() {
		return info;
	}

}
