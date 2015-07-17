package com.MVC.ViewAndControl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import com.MVC.Model.BDMapApplication;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class HeartbeatPackageService extends Service {
	private static final String chatKey = "SLEEKNETGEOCK4stsjeS";
	private static final int port = 15678;
	private static String ip = null;
	private Socket s = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String Msg = null;
	private String reMsg = null;
	private String UserName = null;
	private String InfoFromServer = null;
    private int UserNum = 0;
	private long sendTime = 0L;
	private boolean Status = false;
	private static final long HEART_BEAT_RATE = 60 * 1000; // 1 min
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		//Log.d("Tag", "Service onCreate!");
		sendTime = System.currentTimeMillis();
		SharedPreferences user = getSharedPreferences("LocalhostInfo", 
				Context.MODE_PRIVATE);
		UserName = user.getString("username", "");
		ip = getIp();
		// send Heart Package
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					initalsocket();
				}catch(Exception e){
					Log.d("ErrorMessage", "socket error");
				}
				Msg = "";
				while(s.isConnected()) {
					try{
						if(System.currentTimeMillis() - sendTime
								>= HEART_BEAT_RATE) {
							
							if(Status == true)
								// Request for adding 5 marks
								Msg = Msg + chatKey + "Update" + "\n"
											+ "username" + "/" + UserName + "/" + "\n"
											+ "updatescore" + "/" + 1 + "/" + "\n";
							
							else 
								Msg = Msg + chatKey + "Update" + "\n"
										+ "username" + "/" + UserName + "/" + "\n"
										+ "updatescore" + "/" + 0 + "/" + "\n";
							
							dos.writeUTF(Msg);
							sendTime = System.currentTimeMillis();
						}
					} catch(Exception e){
						Log.d("ErrorMessage",
							"Heart Package send error!");
					}
				}
			}
		}).start();	
		
		// listen msg from server
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					Log.d("waiting thread", "wait error!");
				}
				while(s.isConnected()) {
					try {
						while((reMsg = dis.readUTF()) != null) {
							//Log.d("ServerMessage", reMsg);
							int code;
							String[] seg = reMsg.split("/");
							code = Integer.parseInt(seg[0]);
							InfoFromServer = seg[2];
							UserNum = Integer.parseInt(seg[1]);
							sendMessage(code);
						}
					}catch(Exception e){
						Log.d("ErrorMessage",
								"Heart Package receive error!");
					}
				}
			}
		}).start();	
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		super.onStartCommand(intent, flags, startId);
		//Log.d("Tag", "Service start!");
		Bundle bundle = (Bundle)intent.getExtras();
		Status = bundle.getBoolean("Code");
		return startId;
	}
	
	@Override
	public void onDestroy(){
		Log.d("Tag", "onDestory!");
		super.onDestroy();
	}
	
	private void initalsocket() throws Exception {
		try {
			this.s = new Socket(ip, port);
			this.dos = new DataOutputStream(s.getOutputStream());
			this.dis = new DataInputStream(s.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage(int what) {
		Message msg = Message.obtain();
		msg.what = what;
		mHandler.sendMessage(msg);
	}
	
	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    			case 1:
    				UpdateLocalInfo(InfoFromServer, UserNum);
    				break;
    			case 2:
    				break;
    		}
    	};
    };
    
    private void UpdateLocalInfo(String info, int num) {
		SharedPreferences Localhost = getSharedPreferences("LocalhostInfo", 
				Context.MODE_PRIVATE);
		SharedPreferences Friends = getSharedPreferences("FriendsInfo", 
				Context.MODE_PRIVATE);
		Editor LhEditor = Localhost.edit();
		Editor FdEditor = Friends.edit();
		LhEditor.clear();
		FdEditor.clear();
		
		//Log.d("UserInfoMessage2", info);
		String[] temp = info.split("\\|");
		String name, email, age, gender, telephone, score;
		int index = 0;
		for(int i = 0; i < num; i++) {
			//Log.d("UserInfo", temp[i]);
			String[] data = temp[i].split(",");
			name = data[0];
			email = data[1];
			age = data[2];
			gender = data[3];
			telephone = data[4];
			score = data[5];
			
			if(name.equals(UserName)){
				LhEditor.putString("username", name);
				LhEditor.putString("email", email);
				LhEditor.putString("age", age);
				LhEditor.putString("gender", gender);
				LhEditor.putString("telephone", telephone);
				LhEditor.putString("score", score);	
			}
			
			else 
				FdEditor.putString(index++ + "", name + "/"
						+ email + "/"
						+ age + "/"
						+ gender + "/"
						+ telephone + "/"
						+ score + "/");
						
		}
		
		// System helper info
		FdEditor.putString(index++ + "", "System Helper" + "/"
						+ "ABC@help.com" + "/"
						+ "None" + "/"
						+ "ÄÐ" + "/"
						+ "110" + "/"
						+ "99999" + "/");
		FdEditor.putInt("TotalNum", num);
		LhEditor.commit();
		FdEditor.commit();
	}
    private String getIp(){
    	WifiManager wifiManager = (WifiManager) 
        		getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {  
        	wifiManager.setWifiEnabled(true);    
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();     
        int ipAddress = wifiInfo.getIpAddress(); 
        String p = intToIp(ipAddress); 
        p = getServerIp(p);
        return p;
    }
    
    private String intToIp(int i) { 
		return (i & 0xFF ) + "." +   
				((i >> 8 ) & 0xFF) + "." +      
				((i >> 16 ) & 0xFF) + "." +         
				 ( i >> 24 & 0xFF) ;   
	}
    
    private String getServerIp(String str) {
    	String[] addr = str.split("\\.");
		addr[3] = "1";
		String ServerIp = addr[0] + "." + addr[1] + "."
				+ addr[2] + "." + addr[3];
		return ServerIp;
    }
}
