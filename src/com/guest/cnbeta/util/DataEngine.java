package com.guest.cnbeta.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class DataEngine extends Activity {

	public static SharedPreferences data;
	
	public DataEngine(Context context) {
		data = context.getSharedPreferences("Data", 0);
	}
	
	public void setIsLoadImg(Boolean isLoadImg) {
		data.edit().putBoolean("loadImg", isLoadImg).commit();
	}
	
	public Boolean getIsLoadImg() {
		return data.getBoolean("loadImg", true);
	}
	
	public void setIsFirst(boolean isFirst) {
		data.edit().putBoolean("isFirst", isFirst).commit();
	}

	public boolean getIsFirst() {
		return data.getBoolean("isFirst", true);
	}
	
	public void setFontSize(int fontSize) {
		data.edit().putInt("fontSize", fontSize).commit();
	}

	public int getFontSize() {
		return data.getInt("fontSize", 3);
	}
	
	public void setIsDisplayAd(boolean isDisplay) {
		data.edit().putBoolean("isDisplay", isDisplay).commit();
	}

	public boolean getIsDisplayAd() {
		return data.getBoolean("isDisplay", true);
	}
	
	public void setNightMode(boolean nightMode) {
		data.edit().putBoolean("nightMode", nightMode).commit();
	}

	public boolean getNightMode() {
		return data.getBoolean("nightMode", false);
	}
	
//	public void setClientSN(String clientSN) {
//		setData("clientSN", clientSN);
//	}
//	
//	public String getClientSN() {
//		return getData("clientSN", "100001");
//	}
	
	//DataEngine Method
	
	public void setData(String key, String value){
		
		data.edit().putString(key, value);
	}
	
	public String getData(String key, String defaultValue){
		
		return data.getString(key, defaultValue);
	}
	
}





