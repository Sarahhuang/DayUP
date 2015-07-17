package com.MVC.ViewAndControl;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.MVC.Model.BDMapApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.example.scorerank.R;

public class GPSFrontActivity extends Activity {
	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI= 6.28318530712; // 2*PI
	static double DEF_PI180= 0.01745329252; // PI/180.0
	static double DEF_R =6370693.5; // radius of earth
	
	private MapView mMapView = null;
	private TextView userName, score, nextLesson;
	private Button lessonBtn, frListBtn, exitBtn, userinfoBtn;
	private ImageButton clearPlaceBtn;
	private BaiduMap bdMap;
	private Button locateBtn;
	private LocationMode currentMode;
	private BitmapDescriptor currentMarker = null;
	private Vector<Marker> vMarker = new Vector<Marker> (10);
	private LocationClient locClient;
	private boolean isFirstLoc = true;
	private int state = 0, NUM = 0;
	private Vector<BitmapDescriptor> vBitmap = new Vector<BitmapDescriptor> (10);
	private LocationClientOption option;
	
	private double CurrentLon = -1, CurrentLat = -1;
	private double DestLon = -1, DestLat = -1;
	private boolean Status = false;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gps_front_activity);
        BDMapApplication.getInstance().addActivity(this);
        init();
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	private void init(){
    	vBitmap.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_marka));
    	vBitmap.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_markb));
    	vBitmap.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_markc));
    	vBitmap.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_markd));
    	vBitmap.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_marke));
    	vBitmap.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_markf));
    	vBitmap.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_markg));
    	vBitmap.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_markh));
    	vBitmap.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_marki));
    	vBitmap.add(BitmapDescriptorFactory.fromResource(R.drawable.icon_markj));
    	
    	mMapView = (MapView) findViewById(R.id.bmapview);
    	
    	MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(15.0f);
    	bdMap = mMapView.getMap();
		bdMap.setMapStatus(msu);
		
		locateBtn = (Button) findViewById(R.id.locate_btn);
		lessonBtn = (Button) findViewById(R.id.lessonSchedule_btn);
		userinfoBtn = (Button) findViewById(R.id.userInfo_btn);
		frListBtn = (Button) findViewById(R.id.friendList_btn);
		exitBtn = (Button) findViewById(R.id.exit_btn);
		clearPlaceBtn = (ImageButton) findViewById(R.id.clearPlace_btn);
		
		lessonBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				changeToLessonSchedule();
			}
		});
		userinfoBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				changeToUserInfo();
			}
		}); 
		frListBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				changeToFriList();
			}
		}); 
		exitBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				exit();
			}
		});
		locateBtn.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v){
				switch(state % 3){
				case 0:
					locClient.start();
					locateBtn.setText("罗盘");
					currentMode = LocationMode.NORMAL;
					break;
					
				case 1:
					locateBtn.setText("关闭");
					currentMode = LocationMode.COMPASS;
					
					break;
				case 2:
					locClient.stop();
					Intent intent = new Intent(GPSFrontActivity.this,
							HeartbeatPackageService.class);
					stopService(intent);
					DestLat = -1;
					DestLon = -1;
					locateBtn.setText("定位");
					break;
				}
				bdMap.setMyLocationConfigeration(new MyLocationConfiguration(
						currentMode, true, currentMarker));
				state = state % 3 + 1;
			}
		});
		clearPlaceBtn.setOnClickListener(new ImageButton.OnClickListener(){
			public void onClick(View v){
				clearAllPlace();
			}
		});
		
		userName = (TextView) findViewById(R.id.userName);
		score = (TextView) findViewById(R.id.score);
		nextLesson = (TextView) findViewById(R.id.nextLesson);
		userName.setText(getUserName());
		score.setText(getScore());
		nextLesson.setText(getLesson());
		currentMode = LocationMode.NORMAL;
		bdMap.setMyLocationEnabled(true);
		locClient = new LocationClient(getApplicationContext());
		locClient.registerLocationListener(locListener);
		option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("gcj02");// 设置坐标类型
		option.setAddrType("all");
		option.setScanSpan(1000 * 10);//
		option.setLocationMode(com.baidu.location.LocationClientOption
				.LocationMode.Hight_Accuracy);
		locClient.setLocOption(option);
		
		// 对marker覆盖物添加点击事件
		bdMap.setOnMarkerClickListener(new OnMarkerClickListener(){
			@Override
			public boolean onMarkerClick(Marker arg0){
				for(int i = 0; i < NUM; i ++){
					if (arg0 == vMarker.get(i)){
						final LatLng latLng = arg0.getPosition();
						// 将经纬度转换成屏幕上的点
						// Point point =
						// bdMap.getProjection().toScreenLocation(latLng);
						Toast.makeText(GPSFrontActivity.this, latLng.toString(),
								Toast.LENGTH_SHORT).show();
					}
				}
				return false;
			}
		});
		bdMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng latLng) {
				displayInfoWindow(latLng);
			}
		});
		
    }
	
	private void IfStayNearDestination(double lon1, double lat1,
			double lon2, double lat2) {
		double distance = GetShortDistance(lon1, lat1 ,lon2, lat2);
		
		boolean state = false;
		if(distance <= 100 * 200){ // 200 meters
			state = true;
		}
		
		else if(distance >  100 * 200) {
			state = false;
		}
		// 状态改变，调用service
		if(Status != state){
			Status = state;
			//创建一个Service处理位置信息(Status)
			Bundle bundle = new Bundle();
			bundle.putBoolean("Code", Status);
			Intent intent = new Intent(GPSFrontActivity.this,
				HeartbeatPackageService.class);
			intent.putExtras(bundle);
			startService(intent);
		}
	}
	public double GetShortDistance(double lon1, double lat1, double lon2, double lat2)
	{
			double ew1, ns1, ew2, ns2;
			double dx, dy, dew;
			double distance;
			// 角度转换为弧度
			ew1 = lon1 * DEF_PI180;
			ns1 = lat1 * DEF_PI180;
			ew2 = lon2 * DEF_PI180;
			ns2 = lat2 * DEF_PI180;
			// 经度差
			dew = ew1 - ew2;
			// 若跨东经和西经180 度，进行调整
			if (dew > DEF_PI)
			dew = DEF_2PI - dew;
			else if (dew < -DEF_PI)
			dew = DEF_2PI + dew;
			dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
			dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
			// 勾股定理求斜边长
			distance = Math.sqrt(dx * dx + dy * dy);
			return distance;
	}
	
	private void reverseGeoCode(LatLng latLng) {
		// 创建地理编码检索实例
		GeoCoder geoCoder = GeoCoder.newInstance();
		//
		OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
			// 反地理编码查询结果回调函数
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					// 没有检测到结果
					Toast.makeText(GPSFrontActivity.this, "抱歉，未能找到结果",
							Toast.LENGTH_LONG).show();
				}
				Toast.makeText(GPSFrontActivity.this,
						"位置：" + result.getAddress(), Toast.LENGTH_LONG)
						.show();
			}

			// 地理编码查询结果回调函数
			@Override
			public void onGetGeoCodeResult(GeoCodeResult result) {
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					// 没有检测到结果
				}
			}
		};
		// 设置地理编码检索监听者
		geoCoder.setOnGetGeoCodeResultListener(listener);
		//
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
		// 释放地理编码检索实例
		// geoCoder.destroy();
	}
	BDLocationListener locListener = new BDLocationListener() {

		/*@Override
		public void onReceivePoi(BDLocation location) {

		}
		*/
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || bdMap == null) {
				return;
			}
			// 构造定位数据
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())//
					.direction(100)// 方向
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			Log.d("location",locData.longitude + "");
			Log.d("location",locData.latitude + "");
			
			CurrentLon = locData.longitude;
			CurrentLat = locData.latitude;
			
			// 计算距离，改变status
			if(DestLon != -1 && DestLat != -1) {
				IfStayNearDestination(CurrentLon, CurrentLat,
						DestLon, DestLat);
			}
			
			// 设置定位数据
			bdMap.setMyLocationData(locData);
			// 第一次定位的时候，那地图中心店显示为定位到的位置
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				// MapStatusUpdate描述地图将要发生的变化
				// MapStatusUpdateFactory生成地图将要反生的变化
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
				bdMap.animateMapStatus(msu);
				// bdMap.setMyLocationEnabled(false);
				Toast.makeText(getApplicationContext(), location.getAddrStr(),
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	/**
	 * 添加标注覆盖物
	 */
	private void clearAllPlace(){
		bdMap.clear();
		NUM = 0;
		vMarker.clear();
		Intent intent = new Intent(GPSFrontActivity.this,
				HeartbeatPackageService.class);
		stopService(intent);
		DestLat = -1;
		DestLon = -1;
	}
	private void addMarkerOverlay(LatLng point) {
		//bdMap.clear();
		// 定义marker坐标点

		// 构建markerOption，用于在地图上添加marker
		OverlayOptions options = new MarkerOptions()//
				.position(point)// 设置marker的位置
				.icon(vBitmap.get(NUM))// 设置marker的图标
				.zIndex(9)// 設置marker的所在層級
				.draggable(true);// 设置手势拖拽
		// 在地图上添加marker，并显示
		vMarker.add((Marker) bdMap.addOverlay(options));
		NUM ++;
	}
	/**
	 * 显示弹出窗口覆盖物
	 */
	private void displayInfoWindow(final LatLng latLng) {
		// 创建infowindow展示的view
		Button btn = new Button(getApplicationContext());
		btn.setBackgroundResource(R.drawable.popup);
		btn.setText("设置学习的地方");
		btn.setTextColor(0xFF000000);
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.fromView(btn);
		// infowindow点击事件
		OnInfoWindowClickListener infoWindowClickListener = new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick() {
				reverseGeoCode(latLng);
				//隐藏InfoWindow
				bdMap.hideInfoWindow();
				LatLng point = new LatLng(latLng.latitude, latLng.longitude);
				addMarkerOverlay(point);
				DestLat = latLng.latitude;
				DestLon = latLng.longitude;
			}
		};
		// 创建infowindow
		InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLng, -47,
				infoWindowClickListener);

		// 显示InfoWindow
		bdMap.showInfoWindow(infoWindow);
	}

    @Override
    protected void onResume(){
    	super.onResume();
    	mMapView.onResume();
    }
    @Override
    protected void onPause(){
    	super.onPause();
    	mMapView.onPause();
    }
    @Override
    protected void onDestroy(){
    	locClient.stop();
    	bdMap.setMyLocationEnabled(false);
    	mMapView.onDestroy();
    	mMapView = null;
    	super.onDestroy();
    	for(int i = 0; i < NUM; i ++){
    		vBitmap.get(NUM).recycle();
    	}
    	stopService(new Intent(GPSFrontActivity.this, 
				HeartbeatPackageService.class));
    }
    private String getLesson(){
    	String lesson = "课程";
    	return lesson;
    }
    private String getScore(){
    	String s = "积分";
    	String score = "";
    	return s + score;
    }
    private String getUserName(){
    	String userName = "信息";
    	return userName;
    }
    private void exit(){
    	stopService(new Intent(GPSFrontActivity.this, 
				HeartbeatPackageService.class));
    	BDMapApplication.getInstance().exit();
    	//finish();
    }
    private void changeToFriList(){
    	Intent intent = new Intent(GPSFrontActivity.this,
				ListActivity.class);
		startActivityForResult(intent, 0);
    }
    private void changeToUserInfo(){
    	Intent intent = new Intent(GPSFrontActivity.this,
				UserInfoActivity.class);
		startActivityForResult(intent, 0);
    }
    private void changeToLessonSchedule(){
    	Intent intent = new Intent(GPSFrontActivity.this,
				CourseTableActivity.class);
		startActivityForResult(intent, 0);
    }
    private void setLesson(){
    	;
    }
    private void setPoints(){
    	;
    }
    private void setUserName(){
    	;
    }
}
