package com.MVC.ViewAndControl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.MVC.Model.BDMapApplication;
import com.MVC.Model.Friend;
import com.example.scorerank.R;

public class ListActivity extends Activity {

	private ListView ScoreListView = null;
	private SharedPreferences sharedPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.score_list);
		BDMapApplication.getInstance().addActivity(this);
		List<Friend> FriendList = new ArrayList<Friend>();
		ScoreListView = (ListView) findViewById(R.id.ScoreList);
		sharedPreferences = getSharedPreferences("FriendsInfo", Context.MODE_PRIVATE);
		int FriendNum = sharedPreferences.getInt("TotalNum", 0);
		for(int i = 0; i < FriendNum; i++){
			String info = sharedPreferences.getString(i + "", "");
			Log.d("FriendInfo", info);
			String[] data = info.split("/");
			Log.d("0", data[0]);
			Log.d("5", data[5]);
			Friend temp = new Friend(data[0], data[5]);
			insertSort(FriendList, temp);
		}
		// 组织数据源
		List<HashMap<String, Object>> scorelist = new ArrayList<HashMap<String, Object>>();
		int idNum = 0;
		for (Friend temp : FriendList) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("Id", idNum);
			map.put("FriendId", temp.getName());
			map.put("Score", temp.getScore());
			scorelist.add(map);
		}
		// 配置适配器
		SimpleAdapter adapter = new SimpleAdapter(this, scorelist,// 数据源
				R.layout.score_item,// 显示布局
				new String[] { "Id", "FriendId", "Score" }, // 数据源的属性字段
				new int[] { R.id.Id, R.id.UserName, R.id.Score }); // 布局里的控件id
		// 添加并且显示
		ScoreListView.setAdapter(adapter);
		ScoreListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(ListActivity.this,
								FriendInfoActivity.class);
						Bundle bundle = new Bundle();

						bundle.putInt("ListPosition", arg2);
						intent.putExtras(bundle);
						startActivityForResult(intent, 0);
						//finish();
						//return;
					}
				});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list, menu);
		return true;
	}

	private void insertSort(List<Friend> list, Friend friend) {
		int current_position = list.size();
		int tail = current_position - 1;
		if (list.size() == 0)
			list.add(friend);

		else {
			while (friend.getScore() < list.get(tail).getScore()
					&& current_position > 0) {
				current_position--;
				tail--;

				if (current_position == 0)
					break;
			}
			list.add(current_position, friend);
		}
	}
}
