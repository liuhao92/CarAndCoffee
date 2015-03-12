package com.carandcoffee.custom;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.LogUtil.log;
import com.carandcoffee.BaseActivity.DensityUtil;
import com.carandcoffee.BaseActivity;
import com.carandcoffee.R;
import com.carandcoffee.ServiceActivity.info_type;
import com.carandcoffee.ServiceActivity.type;
import com.carandcoffee.SpaceImageDetailActivity;
import com.carandcoffee.imageview.GestureImageView;
import com.carandcoffee.view.CircleImageView;
import com.carandcoffee.view.Image_detail_dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class CustomAdapter extends SimpleAdapter {
	LayoutInflater minflater;
	List<HashMap<String, Object>> data;
	MediaPlayer player;
	HashMap<String, Object> temp;
	Activity act;

	@SuppressWarnings("unchecked")
	public CustomAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to, LayoutInflater minflater,
			MediaPlayer meadia, Activity act) {
		super(context, data, resource, from, to);
		this.data = (List<HashMap<String, Object>>) data;
		this.minflater = minflater;
		this.player = meadia;
		this.act = act;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View v = super.getView(position, convertView, parent);
		temp = (HashMap<String, Object>) data.get(position);
		type temp_1 = (type) temp.get("type");
		RelativeLayout infolayout = null;
		RelativeLayout no_infolayout = null;
		CircleImageView icon;
		CircleImageView no_icon;
		RelativeLayout status = null;
		TextView text;
		if (temp_1 == type.LEFT) {
			infolayout = (RelativeLayout) v.findViewById(R.id.left_info_layout);
			no_infolayout = (RelativeLayout) v
					.findViewById(R.id.right_info_layout);
			text = (TextView) infolayout.findViewById(R.id.left_info);
			icon = (CircleImageView) v.findViewById(R.id.left_icon);
			no_icon = (CircleImageView) v.findViewById(R.id.right_icon);

		} else {
			infolayout = (RelativeLayout) v
					.findViewById(R.id.right_info_layout);
			no_infolayout = (RelativeLayout) v
					.findViewById(R.id.left_info_layout);
			text = (TextView) infolayout.findViewById(R.id.right_info);
			icon = (CircleImageView) v.findViewById(R.id.right_icon);
			no_icon = (CircleImageView) v.findViewById(R.id.left_icon);
			status = (RelativeLayout) v
					.findViewById(R.id.send_status_right_layout);
			String status_s = (String) temp.get("status");
			status.setVisibility(View.GONE);
			if(status_s!=null){
			if (status_s.equals("now")) {
				status.setVisibility(View.VISIBLE);
				status.findViewById(R.id.loadProgressBar).setVisibility(
						View.VISIBLE);
				status.findViewById(R.id.loadimage).setVisibility(View.GONE);

			} else if (status_s.equals("successed")) {
				
			} else if(status_s.equals("fail")) {
				status.setVisibility(View.VISIBLE);
				status.findViewById(R.id.loadProgressBar).setVisibility(
						View.GONE);
				status.findViewById(R.id.loadimage).setVisibility(View.VISIBLE);
			}
			}
		}
		no_infolayout.setVisibility(View.GONE);
		infolayout.setVisibility(View.VISIBLE);
		no_icon.setVisibility(View.GONE);
		icon.setVisibility(View.VISIBLE);
		icon.setImageBitmap((Bitmap) temp.get("icon"));
		RelativeLayout voice_layout = (RelativeLayout) infolayout
				.findViewById(R.id.voice_info_layout);
		final ImageView image_info = (ImageView) infolayout
				.findViewById(R.id.image_info);

		if ((info_type) temp.get("info_type") == info_type.TEXT) {
			text.setVisibility(View.VISIBLE);
			text.setText((String) temp.get("info"));
			voice_layout.setVisibility(View.GONE);
			image_info.setVisibility(View.GONE);
		}

		else if ((info_type) temp.get("info_type") == info_type.VOICE) {
			text.setVisibility(View.GONE);
			infolayout.setOnClickListener(new playListerner((String) temp
					.get("info")));
			voice_layout.setVisibility(View.VISIBLE);
			image_info.setVisibility(View.GONE);

			Integer time_ = (Integer) temp.get("time_long");
			if (time_ == null) {
				time_ = gettime((String) temp.get("info"));
				temp.put("time_long", time_);
			}
			LayoutParams a = new LayoutParams(voice_layout.getLayoutParams());
			a.addRule(RelativeLayout.CENTER_VERTICAL);
			a.width = DensityUtil.dip2px(voice_layout.getContext(),
					50 + time_ / 2 * 20);
			voice_layout.setLayoutParams(a);
			TextView time = (TextView) voice_layout
					.findViewById(R.id.voice_long);
			time.setText(time_ + "''");
		} else if ((info_type) temp.get("info_type") == info_type.IMAGE) {
			text.setVisibility(View.GONE);
			voice_layout.setVisibility(View.GONE);
			image_info.setVisibility(View.VISIBLE);
			image_info.setScaleType(ScaleType.CENTER_CROP);

			Bitmap a = (Bitmap) temp.get("image_suolue");
			if (a == null) {
				a = (DensityUtil.getBitmapByBytes(
						DensityUtil.getBytes((String) temp.get("info")), act));
				temp.put("image_suolue", a);
			}

			image_info.setImageBitmap(a);
			image_info.setOnClickListener(new imageListerner((String) temp
					.get("info"), image_info));
		}
		return v;
	}

	public void play(String path) {

		try {
			player.reset();
			player.setDataSource(path);
			player.prepare();
			player.start();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public int gettime(String path) {

		try {
			player.reset();
			player.setDataSource(path);
			player.prepare();

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return player.getDuration() / 1000;

	}

	class playListerner implements OnClickListener {
		String path;

		public playListerner(String path) {
			// TODO Auto-generated constructor stub
			this.path = path;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			play(path);

		}

	}

	class imageListerner implements OnClickListener {
		String path;
		ImageView image_info;

		public imageListerner(String path, ImageView image_info) {
			// TODO Auto-generated constructor stub
			this.path = path;
			this.image_info = image_info;
		}

		@Override
		public void onClick(View v) {

			final View va = act.getLayoutInflater().inflate(
					R.layout.image_detail_view, null);
			GestureImageView image = (GestureImageView) va
					.findViewById(R.id.detail_image);
			image.setImageBitmap(BaseActivity.Bytes2Bimap(DensityUtil
					.getBytes(path)));

			final Dialog a = new Dialog(act, R.style.Transparent) {

				@Override
				protected void onCreate(Bundle savedInstanceState) {
					// TODO Auto-generated method stub
					super.onCreate(savedInstanceState);
					setContentView(va);
				}
			};
			image.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					a.dismiss();
				}
			});
			a.show();

		}

	}

}
