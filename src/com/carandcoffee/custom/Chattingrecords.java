package com.carandcoffee.custom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.alibaba.fastjson.JSONArray;

public class Chattingrecords {
	String username;
	String coString="";
	File cofile;
	static String FLAG = "_Chattingrecords.txt";

	public Chattingrecords(String username, String path) {
		this.username = username;
		cofile = new File(path, username + FLAG);
		if (cofile.exists()) {
			coString = read(cofile);
    }

	}

	public String whos() {
		return username;
	}

	private String read(File file) {
		StringBuffer buffer = new StringBuffer();
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");// ÎÄ¼þ±àÂëUnicode,UTF-8,ASCII,GB2312,Big5
			Reader in = new BufferedReader(isr);
			int ch;
			while ((ch = in.read()) > -1) {
				buffer.append((char) ch);
			}
			in.close();

		} catch (IOException e) {

		}
		return buffer.toString();

	}
	private void write(String str){
		try {
			FileOutputStream out=new FileOutputStream(cofile,true);
			out.write(str.getBytes());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public  void add(String str){
		coString=coString+str;
		write(str);
		
	}
	public String getchatstring() {
		return coString;
	}
	public ArrayList<JSONObject> getjsonarray(){
		ArrayList<JSONObject> temp=new ArrayList<JSONObject>();
		if(coString.equals(""))
			return temp;
		JSONTokener toker=new JSONTokener(coString);
		while(toker.more()){
			try {
				temp.add((JSONObject) toker.nextValue());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return temp;
	}
	
	public void clean(){
		if(cofile.exists()){
			cofile.delete();
		}
	}

}
