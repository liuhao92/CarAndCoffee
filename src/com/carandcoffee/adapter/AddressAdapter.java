package com.carandcoffee.adapter;

import java.util.List;
import java.util.Map;

import com.carandcoffee.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AddressAdapter extends SimpleAdapter {

	private int cPosition;

	public void setcPosition(int cPosition) {
		this.cPosition = cPosition;
	}

	public AddressAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		if (cPosition == position) {
			TextView tv_constacts_title = (TextView) view
					.findViewById(R.id.tv_contacts_tile);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			TextView tv_service_address_title = (TextView) view
					.findViewById(R.id.tv_service_address_title);
			TextView tv_service_address = (TextView) view
					.findViewById(R.id.tv_service_address);
			ImageView iv_circle_v = (ImageView) view
					.findViewById(R.id.iv_circle_v);
			view.setBackgroundColor(Color.parseColor("#1c1311"));
			tv_constacts_title.setTextColor(Color.parseColor("#FFF000"));
			tv_name.setTextColor(Color.parseColor("#FFF000"));
			tv_phone.setTextColor(Color.parseColor("#FFF000"));
			tv_service_address_title.setTextColor(Color.parseColor("#FFF000"));
			tv_service_address.setTextColor(Color.parseColor("#FFF000"));
			iv_circle_v.setVisibility(View.VISIBLE);
		} else {
			TextView tv_constacts_title = (TextView) view
					.findViewById(R.id.tv_contacts_tile);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			TextView tv_service_address_title = (TextView) view
					.findViewById(R.id.tv_service_address_title);
			TextView tv_service_address = (TextView) view
					.findViewById(R.id.tv_service_address);
			ImageView iv_circle_v = (ImageView) view
					.findViewById(R.id.iv_circle_v);
			view.setBackgroundColor(Color.parseColor("#ffffff"));
			tv_constacts_title.setTextColor(Color.parseColor("#1c1311"));
			tv_name.setTextColor(Color.parseColor("#1c1311"));
			tv_phone.setTextColor(Color.parseColor("#1c1311"));
			tv_service_address_title.setTextColor(Color.parseColor("#1c1311"));
			tv_service_address.setTextColor(Color.parseColor("#1c1311"));
			iv_circle_v.setVisibility(View.GONE);
		}
		return view;
	}

}
