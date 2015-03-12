package com.carandcoffee;

import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import com.alipay.android.app.sdk.AliPay;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.LogUtil.log;
import com.carandcoffee.data.CarInformation;
import com.carandcoffee.data.ComboDetails;
import com.carandcoffee.data.ContactInformation;
import com.carandcoffee.pay.Keys;
import com.carandcoffee.pay.Rsa;

import android.R.integer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class OrderdetailsActivity extends BaseActivity {

	public static int PAY = 1;

	private Button title_right_button;
	private View status_layout;
	private View person_layout;
	private View car_layout;
	private View combo_layout;
	int index;
	ContactInformation contactInformation;
	CarInformation carinfo;
	HashMap<String, Object> data;
	private ComboDetails ComboDetails;
	View loadyout;
	boolean flag[];
	GetCallback<AVObject> caladress;
	GetCallback<AVObject> calcar;
	GetCallback<AVObject> calpackage;
	status currentstatus;
	String out_trade_no=null;

	Handler mHandler;
	String total_fee="";
	GetCallback<AVObject> update;
    TextView order_number;
	@SuppressWarnings({ "static-access" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_details_activity);
		flag = new boolean[] { false, false, false };

		index = getIntent().getIntExtra("index", 0);
		data = MyorderActivity.order.get(index);
        order_number=(TextView) findViewById(R.id.order_number);
		setBarTitle(R.string.order_detail);
		title_right_button = (Button) findViewById(R.id.titlebar_rightbutton);
		title_right_button.setText("联系客服");

		loadyout = findViewById(R.id.load_yout);
		status_layout = findViewById(R.id.order_details_status_layout);
		person_layout = findViewById(R.id.order_details_person_layout);
		car_layout = findViewById(R.id.order_details_car_layout);
		combo_layout = findViewById(R.id.order_details_combo_layout);

		AVObject address = (AVObject) data.get("address");
		Date date = (Date) data.get("serviceTime");
		contactInformation = new ContactInformation();
		if (date != null) {
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(date);
			contactInformation.setDate_year(calendar.get(Calendar.YEAR) + "年"
					+ (calendar.get(Calendar.MONTH) + 1) + "月"
					+ calendar.get(calendar.DAY_OF_MONTH) + "日");
			contactInformation.setDate_time(calendar.get(Calendar.HOUR_OF_DAY)
					+ ":" + calendar.get(Calendar.MINUTE)/10+calendar.get(Calendar.MINUTE)%10);
		}

		if (address != null) {
			caladress = new GetCallback<AVObject>() {

				@Override
				public void done(AVObject address, AVException arg1) {
					if (arg1 == null) {
						contactInformation
								.setName(address.getString("contact"));
						contactInformation
								.setPhone(address.getString("mobile"));
						contactInformation.setAddress(address
								.getString("detail"));
						setContactInformation(person_layout, contactInformation);
						flag[0] = true;
						if (flag[0] == true && flag[1] == true
								&& flag[2] == true) {
							loadyout.setVisibility(View.GONE);
						}

					} else {
						Toast.makeText(getApplicationContext(), "获取地址失败，重新获取",
								Toast.LENGTH_SHORT).show();
						address.fetchInBackground(caladress);
					}

				}
			};

			address.fetchInBackground(caladress);
		} else
			flag[0] = true;

		// 获取地址等信息

		AVObject car = (AVObject) data.get("car");
		carinfo = new CarInformation();
		if (car != null) {
			calcar = new GetCallback<AVObject>() {

				@Override
				public void done(AVObject car, AVException arg1) {
					if (arg1 == null) {
						carinfo.setCar_displacement(car.getString("volume"));
						carinfo.setCar_licence(car.getString("plate"));
						carinfo.setCar_type(car.getString("make") + "  "
								+ car.getString("series"));
						setCarInformation(car_layout, carinfo);
						flag[1] = true;
						if (flag[0] == true && flag[1] == true
								&& flag[2] == true) {
							loadyout.setVisibility(View.GONE);
						}
					} else {
						Toast.makeText(getApplicationContext(), "获取车型失败，重新获取",
								Toast.LENGTH_SHORT).show();
						car.fetchInBackground(calcar);
					}

				}
			};
			car.fetchInBackground(calcar);
		}
		// 设置车辆信息
		else
			flag[1] = true;

		AVObject packages = (AVObject) data.get("package");
		ComboDetails = new ComboDetails();
		if (packages != null) {
			calpackage = new GetCallback<AVObject>() {

				@Override
				public void done(AVObject packages, AVException arg1) {
					if (arg1 == null) {
						ComboDetails.setClassify(packages.getString("type"));
						String str=packages
								.getString("title");
						if(data.get("items")!=null){
							str+="+其他";
						}
						ComboDetails.setCombodetails(str);
						total_fee =(String) data.get("total_price");
						log.e("number", packages.getNumber("price").toString());
						ComboDetails.setPrice(total_fee);
						setComboDetails(combo_layout, ComboDetails);
						flag[2] = true;
						if (flag[0] == true && flag[1] == true
								&& flag[2] == true) {
							loadyout.setVisibility(View.GONE);
						}
					} else {
						Toast.makeText(getApplicationContext(),
								"获取套餐信息失败,重新获取", Toast.LENGTH_SHORT).show();
						packages.fetchInBackground(calpackage);
					}

				}
			};
			packages.fetchInBackground(calpackage);
		}
		// 查询套餐信息
		else
			{
			flag[2] = true;
			total_fee=(String) data.get("total_price");
			log.e("price,",total_fee);
			ComboDetails.setClassify("其他");
			ComboDetails.setPrice(total_fee);
			setComboDetails(combo_layout, ComboDetails);
			}

		if (flag[0] == true && flag[1] == true && flag[2] == true) {
			loadyout.setVisibility(View.GONE);
		}
		String statu = (String) data.get("status");
		tostatus(statu);
		setOrderstatus(status_layout, currentstatus);
        out_trade_no = (String) data.get("flowNo");
        order_number.setText("订单号:"+out_trade_no);
        log.e("out_trade_no",out_trade_no);

		// 设置查询到的信息进行显示
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				log.e("mhandler", msg.obj.toString());
				
				switch (msg.what) {
				case 1: {
					
					int code=getCode((String)msg.obj);
					if(code==9000)
						{
						showToast(true);
						updatestatus((String) data.get("objectId"));
						}
					else
						showToast(false);
					
				}
					break;
				}
			}

		};
	}

	public void onClick(View v) {
		switch (currentstatus) {
		case UNCONFIRMED:
			topay();
			break;
		default:
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private void topay() {
		if(total_fee!=null&&out_trade_no!=null){
		// TODO Auto-generated method stub
		String Info = getOrderInfo();
		
		// 网址需要做URL编码
		String sign = Rsa.sign(Info, Keys.PRIVATE);
		sign = URLEncoder.encode(sign);
		Info += "&sign=\"" + sign + "\"&" + getSignType();
		final String oderinfo = Info;
		log.e("dingdan", Info);
		new Thread() {
			public void run() {
				// 获取Alipay对象，构造参数为当前Activity和Handler实例对象
				AliPay alipay = new AliPay(OrderdetailsActivity.this, mHandler);
				// 调用pay方法，将订单信息传入
				String result = alipay.pay(oderinfo);
				// 处理返回结果
				Message msg = mHandler.obtainMessage();
				msg.what = PAY;
				msg.obj = result;
				mHandler.sendMessage(msg);
				log.e("结果", result);

			}
		}.start();}
		else
		{
			Toast.makeText(getApplicationContext(), "信息缺失,无法付款",Toast.LENGTH_SHORT).show();
		}
	}

	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

	private String getOrderInfo() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();
		sb.append("partner=\"");
		sb.append(Keys.DEFAULT_PARTNER);

		sb.append("\"&service=\"mobile.securitypay.pay");

		sb.append("\"&_input_charset=\"UTF-8");
        sb.append("\"&out_trade_no=\"");
		sb.append(out_trade_no);

		sb.append("\"&subject=\"");
		sb.append("嘀嘀宅车保-服务");

		sb.append("\"&payment_type=\"1");

		sb.append("\"&seller_id=\"");
		sb.append(Keys.DEFAULT_SELLER);

		sb.append("\"&body=\"");
		sb.append("嘀嘀宅车保-服务"+ComboDetails.getClassify()+ComboDetails.getCombodetails());

		sb.append("\"&total_fee=\"");
		sb.append(total_fee);

		sb.append("\"&notify_url=\"");

		// 网址需要做URL编码
		sb.append(URLEncoder.encode("http://www.zhaichebao.com/alipay/asyncnotice")+"\"");

		// 网址需要做URL编码

	//	sb.append("\"&return_url=\"");
	   //  sb.append(URLEncoder.encode("http://m.alipay.com"));
		return sb.toString();
	}
private void showToast(boolean a){
	LayoutInflater inflater = getLayoutInflater();
	View layout = inflater.inflate(R.layout.toast_layout,(ViewGroup)findViewById(R.id.toast_layout));
	Toast toast = new Toast(getApplicationContext());
	toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
	toast.setDuration(Toast.LENGTH_SHORT);
	ImageView imag=(ImageView) layout.findViewById(R.id.toast_image);
	if(a)
	imag.setImageResource(R.drawable.pay_successed);
	else
	imag.setImageResource(R.drawable.pay_wrong);	
	toast.setView(layout);
	toast.show();
	
}
	public void updatestatus(final String objID) {
		final AVQuery<AVObject> query = new AVQuery<AVObject>("Order");

		update = new GetCallback<AVObject>() {

			@Override
			public void done(AVObject arg0, AVException arg1) {
				// TODO Auto-generated method stub
				if (arg1 == null) {
					String sta = (String) arg0.get("status");
					tostatus(sta);
					setOrderstatus(status_layout, currentstatus);

				} else {

					Toast.makeText(getApplicationContext(), "刷新订单状态失败,重新刷新",
							Toast.LENGTH_SHORT).show();
					query.getInBackground(objID, update);

				}
			}
		};
		query.getInBackground(objID, update);

	}

	private int getCode(String str) {
		int t;
		int start = str.indexOf("resultStatus={") + "resultStatus={".length();
		int end = str.indexOf("}", start);
		t = Integer.valueOf(str.substring(start, end));
		return t;
	}

	private void tostatus(String str) {
		if ("unconfirmed".equals(str))
			currentstatus = status.UNCONFIRMED;
		else if ("processing".equals(str))
			currentstatus = status.PROCESSING;
		else if ("cancel".equals(str))
			currentstatus = status.CANCEL;
		else if ("done".equals(str))
			currentstatus = status.DONE;
		else if("paid".equals(str)){
			currentstatus = status.PAID;
		}
	}

	private void setOrderstatus(View v, status a) {
		TextView text = (TextView) findViewById(R.id.order_details_status);
		switch (a) {
		case UNCONFIRMED:
			text.setText("确认该订单,前去支付");
			break;
		case PAID:
			text.setText("订单已经支付");
			break;
		case PROCESSING:

			text.setText("处理中");
			break;
		case CANCEL:
			text.setText("该订单已经取消");
			break;
		case DONE:
			text.setText("该订单已经完成");
			break;
		}

	}

	private void setContactInformation(View v, ContactInformation info) {
		TextView order_details_name = (TextView) v
				.findViewById(R.id.order_details_name);
		TextView order_details_phone = (TextView) v
				.findViewById(R.id.order_details_phone);
		TextView order_details_address = (TextView) v
				.findViewById(R.id.order_details_address);
		TextView order_details_date_year = (TextView) v
				.findViewById(R.id.order_details_date_year);
		TextView order_details_date_time = (TextView) v
				.findViewById(R.id.order_details_date_time);
		order_details_name.setText(info.getName());
		order_details_phone.setText(info.getPhone());
		order_details_address.setText(info.getAddress());
		order_details_date_year.setText(info.getDate_year());
		order_details_date_time.setText(info.getDate_time());
	}

	private void setCarInformation(View v, CarInformation info) {
		TextView order_details_car_model = (TextView) v
				.findViewById(R.id.order_details_car_model_);
		TextView order_details_car_licence = (TextView) v
				.findViewById(R.id.order_details_car_licence_);
		TextView order_details_car_displacement = (TextView) v
				.findViewById(R.id.order_details_car_displacement_);
		order_details_car_model.setText(info.getCar_type());
		order_details_car_licence.setText(info.getCar_licence());
		order_details_car_displacement.setText(info.getCar_displacement());
	}

	private void setComboDetails(View v, ComboDetails info) {
		TextView order_details_combo = (TextView) v
				.findViewById(R.id.order_details_combo);
		TextView order_details_combo_details = (TextView) v
				.findViewById(R.id.order_details_combo_details);
		TextView order_details_combo_price = (TextView) v
				.findViewById(R.id.order_details_combo_price);

		order_details_combo.setText(info.getClassify());
		order_details_combo_details.setText(info.getCombodetails());
		order_details_combo_price.setText(info.getPrice());
	}

	enum status {// 订单的状态枚举量
		UNCONFIRMED, PAID, PROCESSING, CANCEL, DONE

	}

}
