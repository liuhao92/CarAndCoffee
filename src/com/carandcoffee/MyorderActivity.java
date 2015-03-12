package com.carandcoffee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.LogUtil.log;

public class MyorderActivity extends BaseActivity {
	private Button title_right_button;
	private ListView order_list_view;
	ListAdapter order_adapter;
	List<HashMap<String, String>> order_list;
	public static ArrayList<HashMap<String, Object>> order = new ArrayList<HashMap<String, Object>>();
	View load_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
		setContentView(R.layout.order_activity);
		load_layout = findViewById(R.id.load_yout);
		title_right_button = (Button) findViewById(R.id.titlebar_rightbutton);
		order_list_view = (ListView) findViewById(R.id.order_list);
		title_right_button.setText("联系客服");
		setBarTitle(R.string.my_order);

		order_list = new ArrayList<HashMap<String, String>>();

		order_adapter = new SimpleAdapter(this, order_list,
				R.layout.order_list_item, new String[] { "date" },
				new int[] { R.id.order_time_text });
		order_list_view.setAdapter(order_adapter);
		order_list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent it = new Intent();
				it.putExtra("index", position);
				it.setClass(getApplicationContext(), OrderdetailsActivity.class);
				startActivity(it);

			}
		});
		getorder();
		
	}

	public void getorder() {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("uid", AVUser.getCurrentUser().getObjectId());
		AVCloud.callFunctionInBackground("getOrderList", parameters,
				new FunctionCallback<Object>() {

					@SuppressWarnings({ "unchecked" })
					@Override
					public void done(Object arg0, AVException arg1) {
						// TODO Auto-generated method stub
						if (arg1 == null) {
							order_list.clear();
							order = (ArrayList<HashMap<String, Object>>) arg0;
							for (int i = 0; i < order.size(); i++) {
								HashMap<String, String> temp = new HashMap<String, String>();
								HashMap<String, Object> a = order.get(i);
								String date = (String) a.get("createdAt");
								temp.put("date", gettimestring(date).year);
								order_list.add(temp);
							}
							if(order.size()!=0){
							load_layout.setVisibility(View.GONE);
							SimpleAdapter a = (SimpleAdapter) order_adapter;
							a.notifyDataSetChanged();}
							else
								{
								load_layout.findViewById(R.id.loadProgressBar).setVisibility(View.GONE);
								TextView text = (TextView) load_layout
										.findViewById(R.id.load_text);
								text.setText("您没有订单");
								}

						} else {
							TextView text = (TextView) load_layout
									.findViewById(R.id.load_text);
							text.setText("加载失败，正在重新加载");
							getorder();
						}
					}

				});

	}

	public static time gettimestring(String str) {

		String str1 = str.substring(0, str.indexOf("T"));
		String str2 = str.substring(str.indexOf("T") + 1, str.length() - 1);
		String datearray[] = str1.split("-");
		time a = new time();
		a.year = datearray[0] + "年" + datearray[1] + "月" + datearray[2] + "日";
		String datearray1[] = str2.split(":");
		a.hour = datearray1[0] + ":" + datearray1[1];
		return a;
	}

}

class time {
	time() {
	}

	public String year;
	public String hour;
}
