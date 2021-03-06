package com.example.startupteam;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.android.map.coord.MapCoord;
import net.daum.mf.map.api.CameraUpdateFactory;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapPointBounds;
import net.daum.mf.map.api.MapPolyline;
import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class MapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {
    private final Handler handler = new Handler((message)-> {
        Object path = message.obj;
        if (message.arg1 == RESULT_OK && path != null) {
            Toast.makeText(getApplicationContext(),
                    "[" + path.toString() + "]", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),
                    " " + path.toString(), Toast.LENGTH_LONG).show();
        }
        if(path.toString().equals("버스 승차 확인되었습니다.")){
            takebus="1";
        }
        return false;
    });
    Menu menu;
    private String takebus = "0";
    private int count =0;
    boolean serviceStatus = false;
    private Intent intentservice;
    private String token;
    public Route received;
    private double startlat;
    private double startlon;
    private String SBusnum;
    private ProgressBar progressBar;
    private String SBusStation, stBusStation;
    private String id;
    private static final String LOG_TAG = "MapActivity";
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    public static final int GET_STRING = 10000;
    public static final int GET_STRING_START = 10001;
    public static final int GET_STRING_END = 10002;
    public static final int GET_STRING_BUS = 10003;
    public static final int GET_TRANSFER = 10010;
    private MapPoint map_point;
    private Document start;
    private Document end;
    private TextView start_text;
    private TextView end_text;
    private MapPOIItem customMarker, dcustomMarker;
    private boolean type = true;
    GpsTracker gpsTracker;
    MapPOIItem st, dest;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};

    private String received_id;
    private String r_station;
    private String r_lstation;
    private String r_buslat, r_buslon;
    private String r_lbuslat, r_lbuslon;

    public final static String server_ip = "34.84.111.70";
    //public final static String server_ip = "59.18.147.95";
    public final static String server_port = "8080";
    private final static String server_locate = "locate";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        start_text = (TextView)findViewById(R.id.start_text);
        end_text = (TextView)findViewById(R.id.end_text);
        progressBar = findViewById(R.id.progressbar);
        mapView = new MapView(this);
        mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        gpsTracker = new GpsTracker(MapActivity.this);
        try{
            String[] temp = new String[2];
            received_id = getIntent().getStringExtra("received_id");
            Log.d("receivedid","cant");
            r_station = getIntent().getStringExtra("busstation");
            r_lstation = getIntent().getStringExtra("laststation");
            temp = getIntent().getStringExtra("bus_latlng").split(":");
            r_buslat = temp[0];
            r_buslon = temp[1];
            temp = getIntent().getStringExtra("last_latlng").split(":");
            r_lbuslat = temp[0];
            r_lbuslon = temp[1];
            if(received_id!=null){
                id = received_id;
                type= false;
            }
            Intent transferIntent = new Intent(getApplicationContext(), Transfer_popup.class);
            transferIntent.putExtra("busstation",r_station);
            transferIntent.putExtra("laststation",r_lstation);
            startActivityForResult(transferIntent,MapActivity.GET_TRANSFER);

        }catch(Exception e){
            type = true;
            Log.d("Login","main->map");
        }
        if(type) {
            id = getIntent().getStringExtra("id");
            token = getIntent().getStringExtra("token");
        }

        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        progressBar.setVisibility(View.INVISIBLE);
    }

    public void onClickS(View v){
        Intent intent;

        switch(v.getId()){
            case R.id.start_btn:
                intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivityForResult(intent,GET_STRING_START);
                break;
            case R.id.end_btn:
                intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivityForResult(intent,GET_STRING_END);
                break;
            case R.id.my_btn:
                gpsTracker = new GpsTracker(MapActivity.this);
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(),gpsTracker.getLongitude()),true);
                //mapView.setMapCenterPoint(map_point,true);
                break;
            case R.id.FButton:
                Toast.makeText(MapActivity.this,"경로를 불러오는 중입니다.",Toast.LENGTH_LONG).show(); // 왜 안되지.
                mapView.removeAllPolylines();
                         // 추후 하단 else문에 넣기. 현재 테스트 중

                if(start_text.getText().toString().equals("출발지를 입력하세요")||
                        end_text.getText().toString().equals("도착지를 입력하세요")){
                    Toast.makeText(getApplicationContext(),
                            "출발지와 도착지를 먼저 입력하세요.",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    mapView.fitMapViewAreaToShowAllPOIItems();
                    busActivityStart();
                }
                break;
        }

    }

    // Server로 지속적으로 위치 전송하는 서비스 시작하는 메소드.
    public void serviceStart(){
        intentservice = new Intent(this,ServerCommunicator.class);
        String scURL = "http://" + server_ip + ":"+server_port+"/locateupdate";
        Messenger messenger = new Messenger(handler);
        intentservice.putExtra("MESSENGER",messenger);
        intentservice.putExtra("uripath",scURL);
        intentservice.putExtra("id",id);
        intentservice.setData(Uri.parse(scURL));
        serviceStatus= true;
        startService(intentservice);
    }

    // BUS Activity 시작.
    public void busActivityStart(){
        progressBar.setVisibility(View.VISIBLE);

        //      TEST CODE
        if(!type){
            start = new Document();
            start.setX(r_buslon);
            start.setY(r_buslat);
            end = new Document();
            end.setX(r_lbuslon);
            end.setY(r_lbuslat);
            start_text.setText(r_station);
            end_text.setText(r_lstation);
        }



        Intent intent = new Intent(this,BusActivity.class);
        intent.putExtra("start",start);
        intent.putExtra("end",end);
        Log.i("생성","진행");
        startActivityForResult(intent,GET_STRING_BUS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu_option,menu);
       this.menu = menu;
       return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu1:
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                Toast.makeText(getApplicationContext(),
                        "현재 위치를 자동으로 추적합니다.",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.menu2:
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                Toast.makeText(getApplicationContext(),
                        "위치 추적 모드를 종료합니다.",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.menu3:
                if(!serviceStatus) {
                    Toast.makeText(getApplicationContext(),
                            "선택된 경로가 존재하지 않습니다.",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    Intent intent = new Intent(getApplicationContext(), Status_popup.class);
                    intent.putExtra("route",received);
                    intent.putExtra("dest",end_text.getText().toString());
                    intent.putExtra("take",takebus);
                    startActivity(intent);
                }
                break;
            case R.id.menu4:
                if(serviceStatus) {
                    stopService(intentservice);
                    serviceStatus=false;
                    takebus="0";
                    Toast.makeText(getApplicationContext(),
                            "알림 서비스를 종료합니다.",
                            Toast.LENGTH_LONG).show();
                    mapView.removeAllPolylines();
                    mapView.removePOIItem(customMarker);
                    mapView.removePOIItem(dcustomMarker);
                }
                else
                    Toast.makeText(getApplicationContext(),
                            "선택된 경로가 존재하지 않습니다.",
                            Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        return;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapViewContainer.removeAllViews();
    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint currentLocation, float accuracyInMeters) {
        MapPoint.GeoCoordinate mapPointGeo = currentLocation.getMapPointGeoCoord();
        Log.i(LOG_TAG, String.format("MapView onCurrentLocationUpdate (%f,%f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, accuracyInMeters));
        map_point = currentLocation;
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {
    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {
    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {
    }


    private void onFinishReverseGeoCoding(String result) {
//        Toast.makeText(LocationDemoActivity.this, "Reverse Geo-coding : " + result, Toast.LENGTH_SHORT).show();
    }

    // ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                Log.i("@@@", "start");
                //위치 값을 가져올 수 있음

            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있다
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(MapActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MapActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() {

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            // 3.  위치 값을 가져올 수 있음

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.
            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MapActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);

        builder.setPositiveButton("설정", (dialog, id) -> {
            Intent callGPSSettingIntent
                    = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
        });
        builder.setNegativeButton("취소", (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("테스트","CODE : " + requestCode);

        if (requestCode == GPS_ENABLE_REQUEST_CODE) {   // switch.
            //사용자가 GPS 활성 시켰는지 검사
            if (checkLocationServicesStatus()) {
                if (checkLocationServicesStatus()) {
                    Log.i("@@@", "onActivityResult : GPS 활성화 되있음");
                    checkRunTimePermission();
                }
            }
        }else if(requestCode == GET_STRING_START){
            if(resultCode == RESULT_OK){
                if(start == null)
                    start = new Document();
                if(st!=null)
                    mapView.removePOIItem(st);
                start.setRoadAddressName(data.getStringExtra("road_address_name"));
                start.setPlaceName(data.getStringExtra("place_name"));
                start.setX(data.getStringExtra("x"));
                start.setY(data.getStringExtra("y"));
                startlat = Double.parseDouble(data.getStringExtra("y"));
                startlon = Double.parseDouble(data.getStringExtra("x"));
                Toast.makeText(getApplicationContext(),
                        data.getStringExtra("road_address_name"),
                        Toast.LENGTH_LONG).show();
                ((TextView) findViewById(R.id.start_text)).setText(start.getPlaceName());
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(data.getStringExtra("y")),Double.parseDouble(data.getStringExtra("x"))),true);
                st = new MapPOIItem();
                st.setItemName("출발지");
                st.setTag(0);
                st.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(data.getStringExtra("y")),Double.parseDouble(data.getStringExtra("x"))));
                st.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                st.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                mapView.addPOIItem(st);
                Log.d("마커","마커찍힘");
            }
            else if(resultCode == -2){
                if(start == null)
                    start = new Document();
                if(st!=null)
                    mapView.removePOIItem(st);
                gpsTracker = new GpsTracker(MapActivity.this);
                start.setRoadAddressName("불명");
                start.setPlaceName("현재 위치");
                start.setX(Double.toString(gpsTracker.getLongitude()));
                start.setY(Double.toString(gpsTracker.getLatitude()));
                startlat = gpsTracker.getLatitude();
                startlon = gpsTracker.getLongitude();
                Toast.makeText(getApplicationContext(),
                        "현재 위치로 설정합니다.",
                        Toast.LENGTH_LONG).show();
                ((TextView) findViewById(R.id.start_text)).setText(start.getPlaceName());
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(),gpsTracker.getLongitude()),true);
                st = new MapPOIItem();
                st.setItemName("출발지");
                st.setTag(0);
                st.setMapPoint(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(),gpsTracker.getLongitude()));
                st.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                st.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                mapView.addPOIItem(st);
                Log.d("마커","마커찍힘");
            }
        }else if(requestCode == GET_STRING_END){
            if(resultCode == RESULT_OK){
                if(end == null)
                    end = new Document();
                if(dest!=null)
                    mapView.removePOIItem(dest);
                end.setRoadAddressName(data.getStringExtra("road_address_name"));
                end.setPlaceName(data.getStringExtra("place_name"));
                end.setX(data.getStringExtra("x"));
                end.setY(data.getStringExtra("y"));

                Toast.makeText(getApplicationContext(),
                        data.getStringExtra("road_address_name"),
                        Toast.LENGTH_LONG).show();
                ((TextView) findViewById(R.id.end_text)).setText(end.getPlaceName());
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(end.getY()),Double.parseDouble(end.getX())),true);
                dest = new MapPOIItem();
                dest.setItemName("도착지");
                dest.setTag(0);
                dest.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(end.getY()),Double.parseDouble(end.getX())));
                dest.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                dest.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                mapView.addPOIItem(dest);
            }
            else if(resultCode == -2){
                if(end == null)
                    end = new Document();
                if(dest!=null)
                    mapView.removePOIItem(dest);
                gpsTracker = new GpsTracker(MapActivity.this);
                end.setRoadAddressName("불명");
                end.setPlaceName("현재 위치");
                end.setX(Double.toString(gpsTracker.getLongitude()));
                end.setY(Double.toString(gpsTracker.getLatitude()));

                Toast.makeText(getApplicationContext(),
                        "현재 위치로 설정합니다.",
                        Toast.LENGTH_LONG).show();
                ((TextView) findViewById(R.id.end_text)).setText(end.getPlaceName());
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(),gpsTracker.getLongitude()),true);
                dest = new MapPOIItem();
                dest.setItemName("도착지");
                dest.setTag(0);
                dest.setMapPoint(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(),gpsTracker.getLongitude()));
                dest.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
                dest.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                mapView.addPOIItem(dest);
            }
        }else if(requestCode == GET_STRING_BUS){
            if(resultCode == RESULT_OK){
                Log.i("테스트","A");
                //  nm      : 경로 번호.
                //  route   : bus번호 or 도보 여부
                //  time    : 각각의 시간 소요 list. route list와 index가 같음.

                received = data.getParcelableExtra("route");
                for (int i = 0; i < received.getBusInfo().size(); i++) {
                    if (!(received.getBusInfo().get(i).equals("0"))) {
                        SBusnum = received.getBusInfo().get(i);
                        break;
                    }
                }
                if(received.getBusInfo().size()<=3) {
                    SBusStation = received.getLastStation();
                    for (int i = 0; i < received.getBusStation().size(); i++) {
                        if (!(received.getBusStation().get(i).equals("0"))) {
                            stBusStation = received.getBusStation().get(i);
                            break;
                        }
                    }
                }
                else {
                    for (int i = 0; i < received.getBusStation().size(); i++) {
                        if (!(received.getBusStation().get(i).equals("0"))) {
                            if(count==0){
                                stBusStation = received.getBusStation().get(i);
                                Log.d("startstation",stBusStation);
                            }
                            else if(count==1){
                                SBusStation = received.getBusStation().get(i);
                            }
                            count++;
                        }
                    }
                }

                count = 0;
                if(customMarker != null) {
                    mapView.removePOIItem(customMarker);
                    mapView.removePOIItem(dcustomMarker);
                }
                Log.i("첫번째 버스",SBusnum);
                Log.i("정류장",received.getBusStation()+"");
                Log.i("테스트",received.getRoute_nm());
                Log.i("테스트",received.getBusInfo()+"");
                Log.i("테스트",received.getTimeInfo()+"");

                URLTh_send urlth_send = new URLTh_send();
                Thread Sthread = new Thread(urlth_send);
                Sthread.start();
                try {
                    Sthread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                serviceStart();


                Toast.makeText(this,"rtnm info : " + received.getRoute_nm(),Toast.LENGTH_LONG);
                // contents.
            }else{
                Log.i("테스트","result CODE : " + resultCode);

            }
        }else if (requestCode==GET_TRANSFER){
            if(resultCode==RESULT_OK){
                if(mapView==null) {
                    mapView = new MapView(this);
                    mapViewContainer = findViewById(R.id.map_view);
                    mapViewContainer.addView(mapView);
                    mapView.setMapViewEventListener(this);
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                    gpsTracker = new GpsTracker(MapActivity.this);
                    mapView.removeAllPOIItems();
                    mapView.removeAllPolylines();
                }
                startlat = Double.parseDouble(r_buslat);
                startlon = Double.parseDouble(r_buslon);
                Toast.makeText(MapActivity.this,"경로를 불러오는 중입니다.",Toast.LENGTH_LONG).show(); // 왜 안되지
                mapView.removeAllPolylines();
                busActivityStart();
            }
            else if(resultCode == -100){
                start_text.setText("출발지를 입력하세요");
                end_text.setText("도착지를 입력하세요");
                if(serviceStatus){
                    serviceStatus= false;
                    stopService(intentservice);
                }
                if(mapView==null) {
                    mapView = new MapView(this);
                    mapViewContainer = findViewById(R.id.map_view);
                    mapViewContainer.addView(mapView);
                    mapView.setMapViewEventListener(this);
                    mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
                    gpsTracker = new GpsTracker(MapActivity.this);
                    mapView.removeAllPOIItems();
                    mapView.removeAllPolylines();
                }
            }
        }
    }
//
    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }


    class URLTh_send extends Thread {

        @Override
        public void run() {
            URL url = null;
            try {
                url = new URL( "http:"+MapActivity.server_ip +":"+ MapActivity.server_port + "/"+"setRoute?id="+id+"&startstation="+stBusStation+"&busnum="+SBusnum+"&busstation="+SBusStation+"&laststation="+received.getLastStation());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlCon = null;
            try {
                urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setRequestMethod("GET");
                urlCon.setDoInput(true);
                urlCon.setDoOutput(true);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlCon.disconnect();
            }
            Log.d("Suc", "success1");
            try {
                if (urlCon.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader((urlCon.getInputStream()), "UTF-8"));
                    String br_result = br.readLine();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            markTakeBus(br_result);
                            menu.findItem(R.id.menu3).setTitle("Check Current status");
                        }
                    });
                    Log.d("brread", br_result);
                } else {
                    // Error handling code goes here
                    Log.d("Suc", Integer.toString(urlCon.getResponseCode()));
                    Log.d("Suc", "fail");
                }
            } catch (IOException e) {
                Log.d("Suc", "fail2");
                e.printStackTrace();

            }
        }
    }
    public void markTakeBus(String json) {
        try {
            Log.d("mylocationlat", Double.toString(gpsTracker.getLatitude()));
            Log.d("mylocationlon", Double.toString(gpsTracker.getLongitude()));
            Log.d("markjson value",json);
            JSONObject jsonObject = new JSONObject(json);
            JSONObject data = jsonObject.getJSONObject("data");
            String takeBusName="", takeBuslat="", takeBuslon="";
            String dtakeBusName="", dtakeBuslat="", dtakeBuslon="";
            takeBusName = data.getString("name");
            takeBuslat = data.getString("latitude");
            takeBuslon = data.getString("longitude");
            dtakeBuslat = data.getString("dlatitude");
            dtakeBuslon = data.getString("dlongitude");
            customMarker = new MapPOIItem();

            Log.d("parsingresult","pars");
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(takeBuslat),Double.parseDouble(takeBuslon)), true);
            customMarker.setItemName("탑승 정류장 : "+takeBusName);
            customMarker.setTag(1);
            customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(takeBuslat),Double.parseDouble(takeBuslon)));
            customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
            customMarker.setCustomImageResourceId(R.drawable.busmarker); // 마커 이미지.
            customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
            customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
            mapView.addPOIItem(customMarker);
            dcustomMarker = new MapPOIItem();
            dcustomMarker.setItemName("하차 지점 ");
            dcustomMarker.setTag(1);
            dcustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(dtakeBuslat),Double.parseDouble(dtakeBuslon)));
            dcustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
            dcustomMarker.setCustomImageResourceId(R.drawable.busmarker); // 마커 이미지.
            dcustomMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
            dcustomMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.
            mapView.addPOIItem(dcustomMarker);

            MapPolyline polyline = new MapPolyline();
            polyline.setTag(1000);
            polyline.setLineColor(Color.argb(128, 255, 51, 0)); // Polyline 컬러 지정.

// Polyline 좌표 지정.
            polyline.addPoint(MapPoint.mapPointWithGeoCoord(startlat,startlon));
            polyline.addPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(takeBuslat),Double.parseDouble(takeBuslon)));
// Polyline 지도에 올리기.
            mapView.addPolyline(polyline);

// 지도뷰의 중심좌표와 줌레벨을 Polyline이 모두 나오도록 조정.
            MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
            int padding = 100; // px
            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
