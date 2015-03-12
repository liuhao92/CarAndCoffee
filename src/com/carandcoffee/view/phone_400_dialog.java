package com.carandcoffee.view;

import com.carandcoffee.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class phone_400_dialog extends Dialog {
View left;
View tight;
Context context;
android.view.View.OnClickListener onclick=new View.OnClickListener() {
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.left_buttom:
		dismiss();
		break;
		case R.id.right_buttom:
		dismiss();
		Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+"4009201918"));  
        context.startActivity(intent);
		
		}
	}
};
	public phone_400_dialog(Context context, int theme) {
		super(context, theme);
		this.context=context;
}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.phone_400);
		findViewById(R.id.left_buttom).setOnClickListener(onclick);
		findViewById(R.id.right_buttom).setOnClickListener(onclick);
}
	
}
