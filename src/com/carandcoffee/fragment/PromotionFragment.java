package com.carandcoffee.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avos.avoscloud.AVUser;
import com.carandcoffee.OrderCommitActivity;
import com.carandcoffee.R;
import com.carandcoffee.data.Order;
import com.carandcoffee.data.TabInfo;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PromotionFragment extends Fragment {

	public static final String TABINFOKEY = "com.carandcoffee.fragment.tabinfo.key";

	private TabInfo tabInfo;

	private LinearLayout viewParent;

	private TextView remarkTextView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.tabInfo = (TabInfo) getArguments().getSerializable(TABINFOKEY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewParent = (LinearLayout) inflater.inflate(
				R.layout.promotion_fragment, container, false);
		ListView ll_promotionDetails = (ListView) viewParent
				.findViewById(R.id.promotion_details);
		ArrayList<Map<String, Object>> listItemsData = new ArrayList<Map<String, Object>>();
		for (Order order : tabInfo.getOrders()) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("displacement", order.getDisplacement());
			map.put("level", order.getLevel());
			map.put("lvTitle", order.getLvTitle());
			map.put("price", order.getPrice());
			map.put("tag", order.getOrderNo());
			listItemsData.add(map);
		}
		SimpleAdapter adapter = new PromotionAdapter(getActivity(), listItemsData, R.layout.order, new String[] {"displacement","level","lvTitle","price"}, new int[] {R.id.displacement,R.id.level,R.id.lvTitle,R.id.price});
		ll_promotionDetails.setAdapter(adapter);
		remarkTextView = (TextView) viewParent.findViewById(R.id.promotion_remark);
		if (tabInfo.getRemark().length()==0) {
			remarkTextView.setText(getString(R.string.empty));
		}else {
			remarkTextView.setText(tabInfo.getRemark());
		}
		return viewParent;
	}

	public static Fragment createFragment(TabInfo tabInfo) {
		try {
			Fragment fragment = PromotionFragment.class.newInstance();
			Bundle args = new Bundle();
			args.putSerializable(TABINFOKEY, tabInfo);
			fragment.setArguments(args);
			return fragment;
		} catch (java.lang.InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	class PromotionAdapter extends SimpleAdapter{

		public PromotionAdapter(Context context,
				List<? extends Map<String, ?>> data, int resource,
				String[] from, int[] to) {
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View cView = super.getView(position, convertView, parent);
			Button btn = (Button) cView.findViewById(R.id.order_commit);
			@SuppressWarnings("unchecked")
			final HashMap<String, Object> map = (HashMap<String, Object>) getItem(position);
			btn.setTag(map.get("tag"));
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AVUser user = AVUser.getCurrentUser();
					if (user==null) {
						Toast.makeText(getActivity(), R.string.dont_have_login, Toast.LENGTH_SHORT).show();
					}else {
						Intent i = new Intent(getActivity(),OrderCommitActivity.class);
						i.putExtra(OrderCommitActivity.ORDERNO, (String)v.getTag());
						startActivity(i);
					}
				}
			});
			return cView;
		}
		
	}
}
