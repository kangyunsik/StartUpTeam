package com.example.startupteam;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import net.daum.android.map.coord.MapCoord;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MapActivity extends AppCompatActivity implements MapView.CurrentLocationEventListener, MapView.MapViewEventListener {

    private static final String LOG_TAG = "MapActivity";
    private MapView mapView;
    private ViewGroup mapViewContainer;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    public static final int GET_STRING = 10000;
    public static final int GET_STRING_START = 10001;
    public static final int GET_STRING_END = 10002;
    private MapPoint map_point;
    private Document start;
    private Document end;
    GpsTracker gpsTracker;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //지도를 띄우자
        // java code
        mapView = new MapView(this);
        mapViewContainer = findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
        if (!checkLocationServicesStatus()) {
            showDialogForLocationServiceSetting();
        } else {
            checkRunTimePermission();
        }
        gpsTracker = new GpsTracker(MapActivity.this);
    }
    public void onClickS(View v){
        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
        switch(v.getId()){
            case R.id.start_btn:
                startActivityForResult(intent,GET_STRING_START);
                break;
            case R.id.end_btn:
                startActivityForResult(intent,GET_STRING_END);
                break;
            case R.id.my_btn:
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(),gpsTracker.getLongitude()),true);
                //mapView.setMapCenterPoint(map_point,true);
                break;
        }

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
                Log.d("@@@", "start");
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

        if (requestCode == GPS_ENABLE_REQUEST_CODE) {   // switch.
            //사용자가 GPS 활성 시켰는지 검사
            if (checkLocationServicesStatus()) {
                if (checkLocationServicesStatus()) {
                    Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                    checkRunTimePermission();
                }
            }
        }else if(requestCode == GET_STRING_START){
            if(resultCode == RESULT_OK){
                if(start == null)
                    start = new Document();
                start.setRoadAddressName(data.getStringExtra("road_address_name"));
                start.setPlaceName(data.getStringExtra("place_name"));
                start.setX(data.getStringExtra("x"));
                start.setY(data.getStringExtra("y"));
                Toast.makeText(getApplicationContext(),
                        data.getStringExtra("road_address_name"),
                        Toast.LENGTH_LONG).show();
                ((TextView) findViewById(R.id.start_text)).setText(start.getPlaceName());
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(data.getStringExtra("y")),Double.parseDouble(data.getStringExtra("x"))),true);
            }
        }else if(requestCode == GET_STRING_END){
            if(resultCode == RESULT_OK){
                if(end == null)
                    end = new Document();
                end.setRoadAddressName(data.getStringExtra("road_address_name"));
                end.setPlaceName(data.getStringExtra("place_name"));
                end.setX(data.getStringExtra("x"));
                end.setY(data.getStringExtra("y"));

                Toast.makeText(getApplicationContext(),
                        data.getStringExtra("road_address_name"),
                        Toast.LENGTH_LONG).show();
                ((TextView) findViewById(R.id.end_text)).setText(end.getPlaceName());
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(end.getY()),Double.parseDouble(end.getX())),true);
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

    private class URLTh extends Thread{
        @Override
        public void run(){
            URL url = null;
            try {
                url = new URL("https://dapi.kakao.com/v2/local/search/keyword.json?y=37.514322572335935&x=127.06283102249932&radius=20000&query=cafe");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlCon = null;
            try {
                urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setRequestProperty("Authorization","KakaoAK 08524df1fb5ba759f2cfd86e83401e49");
                urlCon.setRequestMethod("GET");
                urlCon.setDoInput(true);
                urlCon.setDoOutput(true);
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                urlCon.disconnect();
            }
            Log.d("Suc","success1");
            try {
                if (urlCon.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader((urlCon.getInputStream()), "UTF-8"));
                    String str = br.readLine();
                    Log.d("Suc",str);

                    Log.d("Suc","success");
                } else {
                    // Error handling code goes here
                    Log.d("Suc",Integer.toString(urlCon.getResponseCode()));
                    Log.d("Suc","fail");
                }
            } catch (IOException e) {
                Log.d("Suc","fail2");
                e.printStackTrace();

            }
        }
    }

}

