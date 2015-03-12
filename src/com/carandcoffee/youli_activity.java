package com.carandcoffee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;
import com.carandcoffee.adapter.imageadapter;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class youli_activity extends BaseActivity {
	List<HashMap<String,Bitmap>> data;
	ListAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youli_activity);
		super.setBarTitle("ÓÐÀñ»î¶¯");
		findViewById(R.id.titlebar_rightbutton).setVisibility(View.GONE);
		ListView list=(ListView) findViewById(R.id.iamge_list);
		
		 data=new ArrayList<HashMap<String,Bitmap>>();
		
		
		 adapter=new imageadapter(this,data,R.layout.youli_item ,new String[]{},new int[]{});
		 list.setAdapter(adapter);
		 getimage();
	}
private void getimage(){
		AVQuery<AVObject> image=new AVQuery<AVObject>("Promotion");
		image.orderByAscending("createdAt");
		image.findInBackground(new FindCallback<AVObject>() {
			
			@Override
			public void done(List<AVObject> arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if(arg1==null)
				for(AVObject obj:arg0){
					AVFile ima=obj.getAVFile("activity_pic");
					ima.getDataInBackground(new GetDataCallback() {
						
						@Override
						public void done(byte[] arg0, AVException arg1) {
							// TODO Auto-generated method stub
							if(arg0!=null){
							HashMap<String,Bitmap> map=new HashMap<String, Bitmap>();
							map.put("bitmap",Bytes2Bimap(arg0));
							data.add(map);
							SimpleAdapter a=(SimpleAdapter)adapter;
							a.notifyDataSetChanged();}
							
						}
					});
					
					
				}
			}
		});
	}

}
