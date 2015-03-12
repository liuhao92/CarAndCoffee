package com.carandcoffee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.AVCloud;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.FunctionCallback;
import com.avos.avoscloud.LogUtil.log;
import com.carandcoffee.data.Order;
import com.carandcoffee.data.TabInfo;
import com.carandcoffee.fragment.PromotionFragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabContentFactory;

public class PromotionActivity extends BaseActivity {

	private TabHost tabHost;
	private TabInfo[] tabInfos;
	String[] a;
    Handler mHandle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_promotion);
         mHandle=new Handler();
		setBarTitle(R.string.promotion_title);
		setBarButton(R.string.customer_service, new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(getApplicationContext(),
						ServiceActivity.class);
				startActivity(it);
				finish();

			}
		});
		gettype();
      
	}

	private void tabData(final String[] tab_tag,
			final HashMap<String, String> map) {
		AVQuery<AVObject> query = AVQuery.getQuery("Package");
		query.orderByAscending("price");
		query.whereEqualTo("deleted",false);
		query.findInBackground(new FindCallback<AVObject>() {
			@Override
			public void done(List<AVObject> addresstList, AVException e) {
				if (e == null) {
					if (tab_tag.length == 0)
						tabInfos = new TabInfo[] {};
					else {
						tabInfos = new TabInfo[tab_tag.length];
						for (int i = 0; i < tab_tag.length; i++) {
							TabInfo info = new TabInfo();
							info.setTitle(tab_tag[i]);
							info.setTag(tab_tag[i]);

							info.setRemark(getremark(map.get(tab_tag[i])));
							List<Order> list = new ArrayList<Order>();
							info.setOrders(list);
							tabInfos[i] = info;
						}
					}
					for (AVObject address : addresstList) {
						String type = address.getString("type");
						for (int t = 0; t < tab_tag.length; t++) {
							if (tab_tag[t].equals(type)) {
								Order order = new Order();
								order.setDisplacement(address
										.getString("title"));
								order.setPrice(String.valueOf(address
										.getNumber("price")));
								order.setLvTitle(address.getString("subtitle"));
								order.setOrderNo(address.getObjectId());
								tabInfos[t].getOrders().add(order);
							}
						}
					}
					setTabHost();
				} else {
					tabInfos = new TabInfo[] {};
					setTabHost();
				}
			}
		});
	}

	private void updateTitleView() {
		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++) {
			TextView view = (TextView) tabHost.getTabWidget().getChildAt(i)
					.findViewById(R.id.tab_title);
			if (tabHost.getCurrentTab() == i) {
				view.setTextColor(Color.parseColor("#1c1311"));
			} else {
				view.setTextColor(Color.parseColor("#7d7f87"));
			}
		}
	}

	private OnTabChangeListener onTabChangeListener = new OnTabChangeListener() {
		@Override
		public void onTabChanged(String tabId) {
			FragmentManager fm = getFragmentManager();
			android.app.FragmentTransaction ft = fm.beginTransaction();
			for (TabInfo info : tabInfos) {
				Fragment fragment = fm.findFragmentByTag(info.getTag());
				if (tabId.equals(info.getTag())) {
					show(ft, info, fragment);
					invalidateOptionsMenu();
				} else {
					hide(ft, info, fragment);
				}
			}
			ft.commit();
			updateTitleView();
		}

		private void show(FragmentTransaction ft, TabInfo tab, Fragment fragment) {
			if (fragment == null) {
				ft.add(android.R.id.tabcontent,
						PromotionFragment.createFragment(tab), tab.getTag());
			} else {
				ft.attach(fragment);
			}
		}

		private void hide(FragmentTransaction ft, TabInfo tab, Fragment fragment) {
			if (fragment != null) {
				ft.detach(fragment);
			}
		}
	};

	private void addTabs() {
		TabHost.TabContentFactory contentFactory = new TabContentFactory() {
			@Override
			public View createTabContent(String tag) {
				return new View(getApplicationContext());
			}
		};
		for (TabInfo info : tabInfos) {
			tabHost.addTab(tabHost.newTabSpec(info.getTag())
					.setIndicator(makeIndicator(info.getTitle()))
					.setContent(contentFactory));
		}
	}

	private View makeIndicator(String title) {
		LayoutInflater inflater = LayoutInflater.from(this);
		View indicator = inflater.inflate(R.layout.tab_indicator, null);
		TextView textView = (TextView) indicator.findViewById(R.id.tab_title);
		textView.setText(title);
		return indicator;
	}

	private void setTabHost() {
		tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();
		addTabs();
		tabHost.setOnTabChangedListener(onTabChangeListener);
		tabHost.setCurrentTab(0);
		if (tabInfos.length != 0) {
			onTabChangeListener.onTabChanged(tabInfos[0].getTag());
		}
	}

	private void gettype() {
		Map<String, Object> parameters = new HashMap<String, Object>();

		AVCloud.callFunctionInBackground("getPackType", parameters,
				new FunctionCallback<Object>() {

					@Override
					public void done(Object arg0, AVException arg1) {
						// TODO Auto-generated method stub
						if (arg1 == null) {
							@SuppressWarnings("unchecked")
							ArrayList<String> type = (ArrayList<String>) arg0;
							a = new String[type.size()];
							for (int s = 0; s < type.size(); s++) {
								a[s] = type.get(s);
							}
							AVQuery<AVObject> remake = new AVQuery<AVObject>(
									"Remark");
							remake.findInBackground(new FindCallback<AVObject>() {

								@Override
								public void done(List<AVObject> arg0,
										AVException arg1) {
									// TODO Auto-generated method stub
									HashMap<String, String> remark = new HashMap<String, String>();
									for (AVObject i : arg0) {
										remark.put(i.getString("type"),
												i.getString("content"));
									}
									tabData(a, remark);
								}
							});

							log.e("a", "" + a.length);
							for (String i : type) {
								log.e("Ì×²Í", i);
							}
						} else
							{Toast.makeText(getApplicationContext(), "»ñÈ¡Ì×²ÍÊ§°Ü",
									Toast.LENGTH_SHORT).show();
							mHandle.postDelayed(new Runnable() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									gettype();
								}
							}, 500);
							}

					}
				});
	}

	private String getremark(String str) {
		log.e("strstart", str);
		String s = str;
		String result = s.replace("^", "\n");
		log.e("strsend", result);
		return result;

	}
}
