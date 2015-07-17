package com.MVC.ViewAndControl;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.MVC.Model.BDMapApplication;
import com.example.scorerank.R;

public class UserInfoActivity extends Activity {
	private TextView tvUsername;
	private TextView tvGender;
	private TextView tvAge;
	private TextView tvPhone;
	private TextView tvEmail;
	private Button btDone;
	
	private SharedPreferences sharedPreferences;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info_activity);
		initViews();
		BDMapApplication.getInstance().addActivity(this);
		btDone.setOnClickListener(listener);
		Intent intent = getIntent();
		displayUserInfo(intent);
	}
	
	Button.OnClickListener listener = new Button.OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent(UserInfoActivity.this,
					GPSFrontActivity.class);
			startActivityForResult(intent, 0);
			finish();
			//return;
		}
	};

	private void initViews() {
		tvUsername = (TextView) findViewById(R.id.usr_info_username);
		tvGender   = (TextView) findViewById(R.id.usr_info_gender);
		tvAge      = (TextView) findViewById(R.id.usr_info_age);
		tvPhone    = (TextView) findViewById(R.id.usr_info_phone);
		tvEmail    = (TextView) findViewById(R.id.usr_info_email);
		btDone     = (Button) findViewById(R.id.usr_info_done);
	}
	
	private void displayUserInfo(Intent intent) {
		sharedPreferences = getSharedPreferences("LocalhostInfo",
				Activity.MODE_PRIVATE);
		String username = sharedPreferences.getString("username", "");
		String gender   = sharedPreferences.getString("gender", "");
		String age      = sharedPreferences.getString("age", "");
		String phone    = sharedPreferences.getString("telephone", "");
		String email    = sharedPreferences.getString("email", "");
		
		tvUsername.setText(username);
		tvGender.setText(gender);
		tvAge.setText(age);
		tvPhone.setText(phone);
		tvEmail.setText(email);
	}
}

