package com.MVC.Model;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.baidu.mapapi.SDKInitializer;


public class BDMapApplication extends Application {
	public List<Activity> activityList = new LinkedList<Activity>();
	public static BDMapApplication instance;
	@Override
	public void onCreate(){
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());
	}
	
	public BDMapApplication() {
	}
	//����ģʽ�л�ȡΨһ��ExitApplicationʵ��
	public static BDMapApplication getInstance() {
		if(null == instance)
			instance = new BDMapApplication();
		return instance;
	}
		 
	//���Activity��������
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}
		 
	//��������Activity��finish
	public void exit() {
		for(Activity activity:activityList)
			activity.finish();
		System.exit(0);
	}
}
