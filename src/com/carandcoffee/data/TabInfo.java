package com.carandcoffee.data;

import java.io.Serializable;
import java.util.List;

public class TabInfo implements Serializable {

	private static final long serialVersionUID = -5294916544010352873L;

	private String title = "";

	private String remark = "";

	private String tag = "";

	private List<Order> orders;

	public List<Order> getOrders() {
		return orders;
	}

	public void setOrders(List<Order> orders) {
		this.orders = orders;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
