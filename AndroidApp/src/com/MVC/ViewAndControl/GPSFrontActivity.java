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
					locateBtn.setText("����");
					currentMode = LocationMode.NORMAL;
					break;
					
				case 1:
					locateBtn.setText("�ر�");
					currentMode = LocationMode.COMPASS;
					
					break;
				case 2:
					locClient.stop();
					Intent intent = new Intent(GPSFrontActivity.this,
							HeartbeatPackageService.class);
					stopService(intent);
					DestLat = -1;
					DestLon = -1;
					locateBtn.setText("��λ");
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
		option.setOpenGps(true);// ��gps
		option.setCoorType("gcj02");// ������������
		option.setAddrType("all");
		option.setScanSpan(1000 * 10);//
		option.setLocationMode(com.baidu.location.LocationClientOption
				.LocationMode.Hight_Accuracy);
		locClient.setLocOption(option);
		
		// ��marker��������ӵ���¼�
		bdMap.setOnMarkerClickListener(new OnMarkerClickListener(){
			@Override
			public boolean onMarkerClick(Marker arg0){
				for(int i = 0; i < NUM; i ++){
					if (arg0 == vMarker.get(i)){
						final LatLng latLng = arg0.getPosition();
						// ����γ��ת������Ļ�ϵĵ�
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
		// ״̬�ı䣬����service
		if(Status != state){
			Status = state;
			//����һ��Service����λ����Ϣ(Status)
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
			// �Ƕ�ת��Ϊ����
			ew1 = lon1 * DEF_PI180;
			ns1 = lat1 * DEF_PI180;
			ew2 = lon2 * DEF_PI180;
			ns2 = lat2 * DEF_PI180;
			// ���Ȳ�
			dew = ew1 - ew2;
			// ���綫��������180 �ȣ����е���
			if (dew > DEF_PI)
			dew = DEF_2PI - dew;
			else if (dew < -DEF_PI)
			dew = DEF_2PI + dew;
			dx = DEF_R * Math.cos(ns1) * dew; // �������򳤶�(��γ��Ȧ�ϵ�ͶӰ����)
			dy = DEF_R * (ns1 - ns2); // �ϱ����򳤶�(�ھ���Ȧ�ϵ�ͶӰ����)
			// ���ɶ�����б�߳�
			distance = Math.sqrt(dx * dx + dy * dy);
			return distance;
	}
	
	private void reverseGeoCode(LatLng latLng) {
		// ��������������ʵ��
		GeoCoder geoCoder = GeoCoder.newInstance();
		//
		OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
			// ����������ѯ����ص�����
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					// û�м�⵽���
					Toast.makeText(GPSFrontActivity.this, "��Ǹ��δ���ҵ����",
							Toast.LENGTH_LONG).show();
				}
				Toast.makeText(GPSFrontActivity.this,
						"λ�ã�" + result.getAddress(), Toast.LENGTH_LONG)
						.show();
			}

			// ��������ѯ����ص�����
			@Override
			public void onGetGeoCodeResult(GeoCodeResult result) {
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR) {
					// û�м�⵽���
				}
			}
		};
		// ���õ���������������
		geoCoder.setOnGetGeoCodeResultListener(listener);
		//
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(latLng));
		// �ͷŵ���������ʵ��
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
			// ���춨λ����
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())//
					.direction(100)// ����
					.latitude(location.getLatitude())//
					.longitude(location.getLongitude())//
					.build();
			Log.d("location",locData.longitude + "");
			Log.d("location",locData.latitude + "");
			
			CurrentLon = locData.longitude;
			CurrentLat = locData.latitude;
			
			// ������룬�ı�status
			if(DestLon != -1 && DestLat != -1) {
				IfStayNearDestination(CurrentLon, CurrentLat,
						DestLon, DestLat);
			}
			
			// ���ö�λ����
			bdMap.setMyLocationData(locData);
			// ��һ�ζ�λ��ʱ���ǵ�ͼ���ĵ���ʾΪ��λ����λ��
			if (isFirstLoc) {
				isFirstLoc = false;
				LatLng ll = new LatLng(location.getLatitude(),
						location.getLongitude());
				// MapStatusUpdate������ͼ��Ҫ�����ı仯
				// MapStatusUpdateFactory���ɵ�ͼ��Ҫ�����ı仯
				MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(ll);
				bdMap.animateMapStatus(msu);
				// bdMap.setMyLocationEnabled(false);
				Toast.makeText(getApplicationContext(), location.getAddrStr(),
						Toast.LENGTH_SHORT).show();
			}
		}
	};
	/**
	 * ��ӱ�ע������
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
		// ����marker�����

		// ����markerOption�������ڵ�ͼ�����marker
		OverlayOptions options = new MarkerOptions()//
				.position(point)// ����marker��λ��
				.icon(vBitmap.get(NUM))// ����marker��ͼ��
				.zIndex(9)// �O��marker�����ڌӼ�
				.draggable(true);// ����������ק
		// �ڵ�ͼ�����marker������ʾ
		vMarker.add((Marker) bdMap.addOverlay(options));
		NUM ++;
	}
	/**
	 * ��ʾ�������ڸ�����
	 */
	private void displayInfoWindow(final LatLng latLng) {
		// ����infowindowչʾ��view
		Button btn = new Button(getApplicationContext());
		btn.setBackgroundResource(R.drawable.popup);
		btn.setText("����ѧϰ�ĵط�");
		btn.setTextColor(0xFF000000);
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory
				.fromView(btn);
		// infowindow����¼�
		OnInfoWindowClickListener infoWindowClickListener = new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick() {
				reverseGeoCode(latLng);
				//����InfoWindow
				bdMap.hideInfoWindow();
				LatLng point = new LatLng(latLng.latitude, latLng.longitude);
				addMarkerOverlay(point);
				DestLat = latLng.latitude;
				DestLon = latLng.longitude;
			}
		};
		// ����infowindow
		InfoWindow infoWindow = new InfoWindow(bitmapDescriptor, latLng, -47,
				infoWindowClickListener);

		// ��ʾInfoWindow
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
    	String lesson = "�γ�";
    	return lesson;
    }
    private String getScore(){
    	String s = "����";
    	String score = "";
    	return s + score;
    }
    private String getUserName(){
    	String userName = "��Ϣ";
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
