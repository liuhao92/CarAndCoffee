package com.carandcoffee.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.carandcoffee.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

public class imageadapter extends SimpleAdapter {
	List<HashMap<String,Bitmap>> data;
	public imageadapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
		this.data=(List<HashMap<String, Bitmap>>) data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		HashMap<String,Bitmap> temp=data.get(position);
		View v=super.getView(position, convertView, parent);
		ImageView imag=(ImageView) v.findViewById(R.id.image_show);
		Bitmap bit=temp.get("bitmap");
		if(bit!=null)
		imag.setImageBitmap(bit);
		return v;
		
	}
	

}
