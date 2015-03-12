package com.carandcoffee.data;

public class ComboDetails {
	private String classify="";
	private String combodetails="";
	private String price="";
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = "¹²"+price+"Ôª";
	}
	public String getClassify() {
		return classify;
	}
	public void setClassify(String classify) {
		this.classify = classify;
	}
	public String getCombodetails() {
		return combodetails;
	}
	public void setCombodetails(String combodetails) {
		this.combodetails = combodetails;
	}
}
