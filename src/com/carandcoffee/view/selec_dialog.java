package com.carandcoffee.view;

import com.carandcoffee.R;
import com.carandcoffee.ServiceActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

public class selec_dialog extends Dialog {
	Activity act;
	android.view.View.OnClickListener onclik = new android.view.View.OnClickListener() {

		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			switch (v.getId()) {

			case R.id.photo:
				intent.setAction(Intent.ACTION_PICK);
				intent.setType("image/*");
				dismiss();
				act.startActivityForResult(intent,
						ServiceActivity.PHOTO_REQUEST_GALLERY);
				break;
			case R.id.camera:
				intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
				dismiss();
				act.startActivityForResult(intent, ServiceActivity.PHOTO_CAMERA);
				break;

			}

		}
	};

	public selec_dialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		act = (Activity) context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popwindows);
		findViewById(R.id.photo).setOnClickListener(onclik);
		findViewById(R.id.camera).setOnClickListener(onclik);
	}

}
