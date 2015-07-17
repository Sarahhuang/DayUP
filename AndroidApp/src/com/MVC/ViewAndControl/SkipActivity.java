package com.MVC.ViewAndControl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.scorerank.R;

public class SkipActivity extends Activity {
	
	private Button btSkipToRankingList = null;
	private Button btSkipToClassTable = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skip_activity);
		initViews();		
		btSkipToRankingList.setOnClickListener(listenerRL);
		btSkipToClassTable.setOnClickListener(listenerCT);
	}
	
	private void initViews() {
		btSkipToRankingList = (Button) findViewById(R.id.SkipToRankingList);
		btSkipToClassTable = (Button) findViewById(R.id.SkipToClassTable);
	}
	
	Button.OnClickListener listenerRL = new Button.OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent(SkipActivity.this,
					ListActivity.class);
			startActivityForResult(intent, 0);
			//finish();
			//return;
		}
	};
	
	Button.OnClickListener listenerCT = new Button.OnClickListener(){
		public void onClick(View v){
			Intent intent = new Intent(SkipActivity.this,
					CourseTableActivity.class);
			startActivityForResult(intent, 0);
			//finish();
			//return;
		}
	};
	
}
