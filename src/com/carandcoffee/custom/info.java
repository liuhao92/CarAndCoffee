package com.carandcoffee.custom;

public class info {
	public String InfoType;
	public String Content;
	public long Timestamp;
	public long getTimestamp() {
		return Timestamp;
	}
	public void setTimestamp(long timestamp) {
		Timestamp = timestamp;
	}
	public String getInfoType() {
		return InfoType;
	}
	public String getContent() {
		return Content;
	}
	public void setInfoType(String infoType) {
		InfoType = infoType;
	}
	public void setContent(String content) {
		Content = content;
	}
	
}