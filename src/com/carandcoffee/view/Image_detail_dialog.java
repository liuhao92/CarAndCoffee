package com.carandcoffee.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.carandcoffee.R;

public class Image_detail_dialog extends AlertDialog {
	ImageView show;
public Image_detail_dialog(Context context, int theme) {
		super(context, theme);

		
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        View v=getLayoutInflater().inflate(R.layout.image_detail_view, null);
        show=(ImageView) v.findViewById(R.id.detail_image);
		setContentView(v);

	}
	

	public void setbg(Bitmap bt) {
		show.setImageBitmap(bt);
	}

}
