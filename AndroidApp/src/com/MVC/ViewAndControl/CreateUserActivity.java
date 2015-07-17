package com.MVC.ViewAndControl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.MVC.Model.BDMapApplication;
import com.example.scorerank.R;

public class CreateUserActivity extends Activity implements OnClickListener {

	public static final int MSG_CREATE_RESULT = 1;
	private static final String chatKey = "SLEEKNETGEOCK4stsjeS";	
	private static final int port = 15678;
	private static String ip = null;
	private Socket s = null;
	private DataInputStream dis = null;
	private DataOutputStream dos = null;
	private String Msg = null;
	private String reMsg = null;

	private EditText eUsername;
	private EditText ePwd1;
	private EditText ePwd2;
	private RadioGroup rGender;
	private EditText eAge;
	private EditText ePhone;
	private EditText eEmail;

	private Button btnSubmit;
	private Button btnReset;

	ProgressDialog progress;

	@SuppressLint("HandlerLeak") 
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.what > 0){
				progress.dismiss();
				hanleCreateAccountResult(msg.what);
			}
		}
	};
	
	private void initalsocket() throws Exception {
		try {
			this.s = new Socket(ip, port);
			this.dos = new DataOutputStream(s.getOutputStream());
			this.dis = new DataInputStream(s.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void hanleCreateAccountResult(int result) {
		/*
		 * result_code: 0 注册成功 1 用户名已存在 2 数据库操作异常
		 */

		if (result == 1) {
			Toast.makeText(this, "用户名已存在！", Toast.LENGTH_LONG).show();
			return;
		}

		if (result == 2) {
			Toast.makeText(this, "注册失败！服务端出现异常！", Toast.LENGTH_LONG).show();
			return;
		}

		if (result == 3) {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
			//finish();
			//return;
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_user_activity);
		BDMapApplication.getInstance().addActivity(this);
		ip = getIp();
		Log.d("ipaddr", ip);
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
		eUsername.setText("Simon");
		ePwd1.setText("2233");
		ePwd2.setText("2233");
		eAge.setText("28");
		ePhone.setText("13824420561");
		eEmail.setText("270018483@qq.com");
	}
	
	private void initViews() {
		eUsername = (EditText) findViewById(R.id.new_username);
		ePwd1 = (EditText) findViewById(R.id.new_password_1);
		ePwd2 = (EditText) findViewById(R.id.new_password_2);
		rGender = (RadioGroup) findViewById(R.id.new_radio_group_gender);
		eAge = (EditText) findViewById(R.id.new_age);
		ePhone = (EditText) findViewById(R.id.new_phone);
		eEmail = (EditText) findViewById(R.id.new_email);
		btnSubmit = (Button) findViewById(R.id.new_btn_submit);
		btnReset = (Button) findViewById(R.id.new_btn_reset);
		btnSubmit.setOnClickListener(this);
		btnReset.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_btn_submit:
			handleCreateAccount();
			break;
		case R.id.new_btn_reset:
			handleReset();
			break;
		}
	}

	private void handleCreateAccount() {
		boolean isUsernameValid = checkUsername();
		if (!isUsernameValid) {
			Toast.makeText(this, "用户名不正确，请重新输入", Toast.LENGTH_LONG).show();
			return;
		}

		int pwdResult = checkPassword();
		if (pwdResult == 1) {
			Toast.makeText(this, "两次输入的密码不一致，请确认！", Toast.LENGTH_LONG).show();
			return;
		}
		if (pwdResult == 2) {
			Toast.makeText(this, "密码不能为空！", Toast.LENGTH_LONG).show();
			return;
		}

		int isAgeValid = checkAge();
		if (isAgeValid == -1) {
			Toast.makeText(this, "年龄不能为空！", Toast.LENGTH_LONG).show();
			return;
		}
		if (isAgeValid == -2) {
			Toast.makeText(this, "年龄超出范围(1~100)！", Toast.LENGTH_LONG).show();
			return;
		}
		if (isAgeValid == -3) {
			Toast.makeText(this, "年龄格式输入错误，请不要输入字母、符号等其他字符串！",
					Toast.LENGTH_LONG).show();
			return;
		}

		if (TextUtils.isEmpty(ePhone.getText().toString())) {
			Toast.makeText(this, "请输入电话号码！", Toast.LENGTH_LONG).show();
			return;
		}

		if (TextUtils.isEmpty(eEmail.getText().toString())) {
			Toast.makeText(this, "请输入邮箱！", Toast.LENGTH_LONG).show();
			return;
		}

		createAccount();
	}

	private void createAccount() {
		progress = new ProgressDialog(this);
		progress.setCancelable(true);
		progress.setCanceledOnTouchOutside(false);
		progress = ProgressDialog.show(this, null, "注册中...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					initalsocket();
				} catch (Exception e) {
					Log.d("ErrorMessage", "socket error");
				}
				RadioButton selectedGender = (RadioButton) CreateUserActivity.this
						.findViewById(rGender.getCheckedRadioButtonId());
				Msg = "";
				try {
					if (s.isConnected()) {
						Msg = Msg + chatKey + "Register" + "\n" + 
					"username/" + eUsername.getText().toString() + "/" + "\n" + 
					"password/" + ePwd1.getText().toString() + "/" +"\n" + 
					"gender/" + selectedGender.getText().toString() + "/" +"\n" + 
					"age/" + eAge.getText().toString() + "/" +"\n" + 
					"phone/" + ePhone.getText().toString() + "/" +"\n" + 
					"email/" + eEmail.getText().toString() + "/" +"\n";
						dos.writeUTF(Msg);

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					if(s.isConnected()) {
						while( (reMsg = dis.readUTF()) != null) {
							System.out.println(reMsg);
							sendMessage(Integer.parseInt(reMsg));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	private boolean checkUsername() {
		String username = eUsername.getText().toString();
		if (TextUtils.isEmpty(username)) {
			return false;
		}
		return true;
	}

	private int checkPassword() {
		/*
		 * return value: 0 password valid 1 password not equal 2 inputs 2
		 * password empty
		 */
		String pwd1 = ePwd1.getText().toString();
		String pwd2 = ePwd2.getText().toString();
		if (!pwd1.equals(pwd2)) {
			return 1;
		} else if (TextUtils.isEmpty(pwd1)) {
			return 2;
		} else {
			return 0;
		}
	}

	private int checkAge() {
		/*
		 * return value 0 输入合法 -1 输入为空 -2输入为负数 -3输入为非数值字符串或包括小数
		 */
		int ageNum;
		String age = eAge.getText().toString();
		if (TextUtils.isEmpty(age)) {
			return -1;
		}
		try {
			ageNum = Integer.parseInt(age);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return -3;
		}
		if (ageNum <= 0 || ageNum > 100) {
			return -2;
		}
		return 0;
	}

	private void handleReset() {
		eUsername.setText("");
		ePwd1.setText("");
		ePwd2.setText("");
		((RadioButton) (rGender.getChildAt(0))).setChecked(true);
		eAge.setText("");
		ePhone.setText("");
		eEmail.setText("");
	}

	private void sendMessage(int what) {
		Message msg = Message.obtain();
		msg.what = what;
		mHandler.sendMessage(msg);
	}

}
