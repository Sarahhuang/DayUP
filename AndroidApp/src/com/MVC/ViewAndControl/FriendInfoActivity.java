package com.MVC.ViewAndControl;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.MVC.Model.BDMapApplication;
import com.example.scorerank.R;

public class FriendInfoActivity extends Activity {
	private TextView FriendIdView = null;
	private TextView FriendScoreView = null;
	private TextView FriendEmailView = null;
	private TextView FriendGenderView = null;
	private TextView FriendAgeView = null;
	private TextView FriendPhoneView = null;
	
	private SharedPreferences sharedPreferences;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.friend_info);
		BDMapApplication.getInstance().addActivity(this);
		Bundle bundle = getIntent().getExtras();
		int position = bundle.getInt("ListPosition");
				
		// obtain Friend List
		sharedPreferences = getSharedPreferences("FriendsInfo", Activity.MODE_PRIVATE);
		String Info = sharedPreferences.getString(position + "", "");
		
		/*
		 list[0] - name, list[1] - email, list[2] - age
		 list[3] - gender, list[4] - phone, list[5] - score
		 */
		Log.d("FriendInfo", Info);
		String[] list = Info.split("/");
		
		FriendIdView = (TextView) findViewById(R.id.FriendId);
		FriendIdView.setText(list[0]);
		
		FriendEmailView = (TextView) findViewById(R.id.FriendEmail);
		FriendEmailView.setText(list[1]);
		
		FriendAgeView = (TextView) findViewById(R.id.FriendAge);
		FriendAgeView.setText(list[2]);
		
		FriendGenderView = (TextView) findViewById(R.id.FriendGender);
		FriendGenderView.setText(list[3]);
		
		FriendPhoneView = (TextView) findViewById(R.id.FriendPhone);
		FriendPhoneView.setText(list[4]);
		
		FriendScoreView = (TextView) findViewById(R.id.FriendScore);
		FriendScoreView.setText(list[5]);
	}
}
