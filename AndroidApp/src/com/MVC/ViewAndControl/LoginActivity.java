package com.MVC.ViewAndControl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.MVC.Model.BDMapApplication;
import com.example.scorerank.R;
public class LoginActivity extends Activity implements OnClickListener {
    private EditText loginUsername;
    private EditText loginPassword;
    private Button loginButton;
    private Button createButton;
    
    private static final String chatKey = "SLEEKNETGEOCK4stsjeS";	
    private String Msg = null;
    private String reMsg = null;
    private String InfoFromServer = null;
    private int UserNum = 0;
	
    private ProgressDialog loginProgress;
    
    private static final int port = 15678;
	private static String ip = null;
	private Socket s = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
    
    @SuppressLint("HandlerLeak") 
    private Handler mHandler = new Handler() {
    	public void handleMessage(Message msg) {
    		if(msg.what > 0) {
    			loginProgress.cancel();
    			handleLoginResult(msg.what);
    		}
    	};
    };
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        BDMapApplication.getInstance().addActivity(this);
        ip = getIp(); 
        initViews();
        TestInitPara();
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
    private void TestInitPara(){
		loginUsername.setText("Simon");
		loginPassword.setText("2233");
	}
    
	private void initViews() {
		loginUsername = (EditText)findViewById(R.id.login_username);
		loginPassword = (EditText)findViewById(R.id.login_password);
		loginButton   = (Button)findViewById(R.id.login);
		createButton  = (Button)findViewById(R.id.create_count);
		
		loginButton.setOnClickListener(this);
		createButton.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.login:
			handleLogin();
			break;
		case R.id.create_count:
			handleCreateCount();
			break;
		default:
			break;	
		}
		
	}
	private void handleLogin() {
		String username = loginUsername.getText().toString();
		String password = loginPassword.getText().toString();
		login(username, password);
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
	private void login(final String username, final String password) {
		loginProgress = new ProgressDialog(this);
		loginProgress.setCancelable(true);
		loginProgress = ProgressDialog.show(this, null, "登陆中...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					initalsocket();
				} catch (Exception e) {
					Log.d("ErrorMessage", "socket error");
				}
				Msg = "";
				try {
					if (s.isConnected()) {
						Msg = Msg + chatKey + "Login" + "\n" + 
					"username/" + loginUsername.getText().toString() + "/" + "\n" + 
					"password/" + loginPassword.getText().toString() + "/" +"\n"; 
					dos.writeUTF(Msg);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if(s.isConnected()) {
						while( (reMsg = dis.readUTF()) != null) {
							Log.d("ServerMessage", reMsg);
							int code;
							String[] seg = reMsg.split("/");
							code = Integer.parseInt(seg[0]);
							//Log.d("ServerMessage", seg[0]);
							InfoFromServer = seg[2];
							//Log.d("ServerMessage", seg[1]);
							UserNum = Integer.parseInt(seg[1]);
							//Log.d("ServerMessage", seg[2]);
							sendMessage(code);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
		
	}
	private void handleCreateCount() {
		Intent intent = new Intent(this, CreateUserActivity.class);
		startActivity(intent);
		//finish();
	}
	
	private void handleLoginResult(int result){
		/*
		 * login_result:
		 *  登陆成功！
		 *  登陆失败，用户名或密码错误！
		 *  登陆失败，用户名不存在！
		 *  登陆失败，未知错误！
		 * */
		int resultCode = result;
		switch(resultCode) {
		case 1:
			Toast.makeText(this, "用户名不存在！", Toast.LENGTH_LONG).show();
			break;
		case 2:
			Toast.makeText(this, "用户名或密码错误！", Toast.LENGTH_LONG).show();
			break;
		case 3:
			GenerateLocalInfo(InfoFromServer, UserNum);
			onLoginSuccess();
			break;
		default:
			Toast.makeText(this, "登陆失败！未知错误！", Toast.LENGTH_LONG).show();
			break;
		}
	}
	
	private void onLoginSuccess() {
		Intent intent = new Intent(this, UserInfoActivity.class);
		SharedPreferences localUser = getSharedPreferences("FriendsInfo", 
				Context.MODE_PRIVATE);
		Log.d("username", localUser.getString("username", ""));
		Log.d("gender", localUser.getString("gender", ""));
		try {
			intent.putExtra("username", localUser.getString("username", ""));
			intent.putExtra("gender", localUser.getString("gender", ""));
			intent.putExtra("age", localUser.getString("age", ""));
			intent.putExtra("phone", localUser.getString("telephone", ""));
			intent.putExtra("email", localUser.getString("email", ""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		startActivity(intent);
		//finish();
	}
	private void sendMessage(int what) {
		Message msg = Message.obtain();
		msg.what = what;
		mHandler.sendMessage(msg);
	}
	
	private void GenerateLocalInfo(String info, int num) {
		SharedPreferences Localhost = getSharedPreferences("LocalhostInfo", 
				Context.MODE_PRIVATE);
		SharedPreferences Friends = getSharedPreferences("FriendsInfo", 
				Context.MODE_PRIVATE);
		Editor LhEditor = Localhost.edit();
		Editor FdEditor = Friends.edit();
		LhEditor.clear();
		FdEditor.clear();
		
		Log.d("UserInfoMessage", info);
		String[] temp = info.split("\\|");
		String name, email, age, gender, telephone, score;
		int index = 0;
		for(int i = 0; i < num; i++) {
			Log.d("UserInfo", temp[i]);
			String[] data = temp[i].split(",");
			name = data[0];
			email = data[1];
			age = data[2];
			gender = data[3];
			telephone = data[4];
			score = data[5];
			
			if(name.equals(loginUsername.getText().toString())){
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
						+ "男" + "/"
						+ "110" + "/"
						+ "99999" + "/");
		FdEditor.putInt("TotalNum", num);
		LhEditor.commit();
		FdEditor.commit();
	}
	
	
}