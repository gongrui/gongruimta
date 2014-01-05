package com.gongrui.mta.service;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesService {

	private Context context;

	public PreferencesService(Context context) {

		this.context = context;
	}

	/**
	 * 
	 * @param ip
	 * @param port
	 */
	public void save(String ip, Integer port,String serverUrl) {
		SharedPreferences preferences = context.getSharedPreferences(
				"bluesetting", Context.MODE_PRIVATE);
		// 不要添加后缀名,第二个是操作模式
		Editor editor = preferences.edit();
		editor.putString("ip", ip);
		editor.putInt("port", port);// 第一个是参数名称,第二个是参数值
		editor.putString("serverurl", serverUrl);
		// 目前是保存在内存中
		editor.commit();// 把内存中存在的数据写到文件中
	}

	public Map<String, String> getPreference() {
		Map<String, String> params = new HashMap<String, String>();
		SharedPreferences preferences = context.getSharedPreferences(
				"bluesetting", Context.MODE_PRIVATE);
		params.put("ip", preferences.getString("ip", "192.168.1.100"));
		params.put("port", String.valueOf(preferences.getInt("port", 5678)));
		params.put("serverurl", preferences.getString("serverurl", ""));

		// 第一个是xml中的<name>,第二个参数是默认值""
		return params;
	}

}
