package com.carandcoffee;

import java.util.Timer;
import java.util.TimerTask;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVMobilePhoneVerifyCallback;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.RequestMobileCodeCallback;
import com.avos.avoscloud.SignUpCallback;
import com.carandcoffee.application.CACApplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class LoginActivity extends BaseActivity {
	final static int GETYAN = 0;
	final static int LOGIN_SUCCESS = 1;
	final static int LOGIN_FAIL = 2;
	Button yanzhengma;
	Handler main;
	Timer temp;
	EditText phone_input;
	EditText sms_input;
	RelativeLayout loginlayout;
	String phone_number;
	boolean flag = false;
	CACApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);
		app = (CACApplication) getApplication();
		setBarTitle(R.string.login);

		Button title_butom = (Button) findViewById(R.id.titlebar_rightbutton);
		title_butom.setVisibility(View.GONE);

		yanzhengma = (Button) findViewById(R.id.yanzhengma_buttom);

		loginlayout = (RelativeLayout) findViewById(R.id.loginlayout);

		phone_input = (EditText) findViewById(R.id.editText1);
		sms_input = (EditText) findViewById(R.id.editText2);

		main = new Myhandler();

	}

	public void getyanzhengma(View v) {
		phone_number = phone_input.getEditableText().toString();
		yanzhengma.setTextColor(0xff8c8c8c);
		yanzhengma.setBackgroundColor(0x77fff27d);
		yanzhengma.setClickable(false);
		loginlayout.setVisibility(View.VISIBLE);
		temp = new Timer();
		temp.schedule(new TimerTask() {

			int i = 60;

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = main.obtainMessage();
				msg.what = GETYAN;
				msg.arg1 = i;
				i--;
				main.sendMessage(msg);
			}
		}, 0, 1000);
		AVUser user = new AVUser();// 注册
		user.setUsername(phone_number);
		user.setMobilePhoneNumber(phone_number);
		user.setPassword(phone_number);
		user.put("nickname", phone_number);
		String id = "";
		if (app.session != null) {
			id = app.MypeerID;
		} else {
			id = phone_number;
		}
		user.put("peerId", id);
		user.signUpInBackground(new SignUpCallback() {

			@Override
			public void done(AVException arg0) {
				if (arg0 == null) {
					flag = true;
				} else if (arg0.getCode() == AVException.USER_MOBILE_PHONENUMBER_TAKEN
						|| arg0.getCode() == AVException.USERNAME_TAKEN) {
					AVUser.requestLoginSmsCodeInBackground(phone_number,
							new RequestMobileCodeCallback() {

								@Override
								public void done(AVException arg0) {
									// TODO Auto-generated method stub
									if (arg0 != null) {
										if (arg0.getCode() == AVException.USER_MOBILEPHONE_NOT_VERIFIED) {
											AVUser.requestMobilePhoneVerifyInBackground(
													phone_number,
													new RequestMobileCodeCallback() {

														@Override
														public void done(
																AVException e) {
															if (e == null)
																flag = true;
														}
													});
										}
									}

								}
							});
				} else {

				}
			}
		});

	}

	public void Login(View v) {
		final LogInCallback<AVUser> LogInCallback = new LogInCallback<AVUser>() {
			public void done(AVUser user, AVException e) {
				if (user != null) {
					Intent it = new Intent();
					it.setClass(getApplicationContext(), MycarActivity.class);
					setResult(LOGIN_SUCCESS, it);
					finish();
				} else {
					// 登录失败
					Toast.makeText(getApplicationContext(), "登录失败",
							Toast.LENGTH_SHORT).show();
				}
			}
		};
		String smscode = sms_input.getEditableText().toString();
		if (flag) {
			AVUser.verifyMobilePhoneInBackground(smscode,
					new AVMobilePhoneVerifyCallback() {

						@Override
						public void done(AVException e) {
							// TODO Auto-generated method stub
							if (e == null) {
								AVUser.logInInBackground(phone_number,
										phone_number, LogInCallback);
							} else
								Toast.makeText(getApplicationContext(), "登录失败",
										Toast.LENGTH_SHORT).show();
						}
					});
		} else {
			AVUser.loginBySMSCodeInBackground(phone_number, smscode,
					LogInCallback);
		}
	}

	class Myhandler extends Handler {
		void settext(int arg) {
			if (arg != 0) {
				yanzhengma.setText("(" + arg + ")秒后重新获取验证码");
			} else {
				yanzhengma.setClickable(true);
				yanzhengma.setText("获取验证码");
				yanzhengma.setTextColor(0xff1c1311);
				yanzhengma.setBackgroundColor(0xfffff000);
				temp.cancel();
			}
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case GETYAN:
				settext(msg.arg1);
			}

		}
	}
}
