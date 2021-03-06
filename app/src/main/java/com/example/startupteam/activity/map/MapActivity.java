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
        if(path.toString().equals("?????? ?????? ?????????????????????.")){
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
                Toast.makeText(MapActivity.this,"????????? ???????????? ????????????.",Toast.LENGTH_LONG).show(); // ??? ?????????.
                mapView.removeAllPolylines();
                         // ?????? ?????? else?????? ??????. ?????? ????????? ???

                if(start_text.getText().toString().equals("???????????? ???????????????")||
                        end_text.getText().toString().equals("???????????? ???????????????")){
                    Toast.makeText(getApplicationContext(),
                            "???????????? ???????????? ?????? ???????????????.",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    mapView.fitMapViewAreaToShowAllPOIItems();
                    busActivityStart();
                }
                break;
        }

    }

    // Server??? ??????????????? ?????? ???????????? ????????? ???????????? ?????????.
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

    // BUS Activity ??????.
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
        Log.i("??????","??????");
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
                        "?????? ????????? ???????????? ???????????????.",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.menu2:
                mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
                Toast.makeText(getApplicationContext(),
                        "?????? ?????? ????????? ???????????????.",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.menu3:
                if(!serviceStatus) {
                    Toast.makeText(getApplicationContext(),
                            "????????? ????????? ???????????? ????????????.",
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
                            "?????? ???????????? ???????????????.",
                            Toast.LENGTH_LONG).show();
                    mapView.removeAllPolylines();
                    mapView.removePOIItem(customMarker);
                    mapView.removePOIItem(dcustomMarker);
                }
                else
                    Toast.makeText(getApplicationContext(),
                            "????????? ????????? ???????????? ????????????.",
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

    // ActivityCompat.requestPermissions??? ????????? ????????? ????????? ????????? ???????????? ?????????
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????
            boolean check_result = true;

            // ?????? ???????????? ??????????????? ???????????????.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                Log.i("@@@", "start");
                //?????? ?????? ????????? ??? ??????

            } else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ??????
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                    Toast.makeText(MapActivity.this, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MapActivity.this, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() {

        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MapActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)
            // 3.  ?????? ?????? ????????? ??? ??????

        } else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.
            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapActivity.this, REQUIRED_PERMISSIONS[0])) {
                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Toast.makeText(MapActivity.this, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.", Toast.LENGTH_LONG).show();
                // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult?????? ???????????????.
                ActivityCompat.requestPermissions(MapActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ?????????????????????????");
        builder.setCancelable(true);

        builder.setPositiveButton("??????", (dialog, id) -> {
            Intent callGPSSettingIntent
                    = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
        });
        builder.setNegativeButton("??????", (dialog, id) -> dialog.cancel());

        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("?????????","CODE : " + requestCode);

        if (requestCode == GPS_ENABLE_REQUEST_CODE) {   // switch.
            //???????????? GPS ?????? ???????????? ??????
            if (checkLocationServicesStatus()) {
                if (checkLocationServicesStatus()) {
                    Log.i("@@@", "onActivityResult : GPS ????????? ?????????");
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
                st.setItemName("?????????");
                st.setTag(0);
                st.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(data.getStringExtra("y")),Double.parseDouble(data.getStringExtra("x"))));
                st.setMarkerType(MapPOIItem.MarkerType.BluePin); // ???????????? ???????????? BluePin ?????? ??????.
                st.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // ????????? ???????????????, ???????????? ???????????? RedPin ?????? ??????.
                mapView.addPOIItem(st);
                Log.d("??????","????????????");
            }
            else if(resultCode == -2){
                if(start == null)
                    start = new Document();
                if(st!=null)
                    mapView.removePOIItem(st);
                gpsTracker = new GpsTracker(MapActivity.this);
                start.setRoadAddressName("??????");
                start.setPlaceName("?????? ??????");
                start.setX(Double.toString(gpsTracker.getLongitude()));
                start.setY(Double.toString(gpsTracker.getLatitude()));
                startlat = gpsTracker.getLatitude();
                startlon = gpsTracker.getLongitude();
                Toast.makeText(getApplicationContext(),
                        "?????? ????????? ???????????????.",
                        Toast.LENGTH_LONG).show();
                ((TextView) findViewById(R.id.start_text)).setText(start.getPlaceName());
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(),gpsTracker.getLongitude()),true);
                st = new MapPOIItem();
                st.setItemName("?????????");
                st.setTag(0);
                st.setMapPoint(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(),gpsTracker.getLongitude()));
                st.setMarkerType(MapPOIItem.MarkerType.BluePin); // ???????????? ???????????? BluePin ?????? ??????.
                st.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // ????????? ???????????????, ???????????? ???????????? RedPin ?????? ??????.
                mapView.addPOIItem(st);
                Log.d("??????","????????????");
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
                dest.setItemName("?????????");
                dest.setTag(0);
                dest.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(end.getY()),Double.parseDouble(end.getX())));
                dest.setMarkerType(MapPOIItem.MarkerType.BluePin); // ???????????? ???????????? BluePin ?????? ??????.
                dest.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // ????????? ???????????????, ???????????? ???????????? RedPin ?????? ??????.
                mapView.addPOIItem(dest);
            }
            else if(resultCode == -2){
                if(end == null)
                    end = new Document();
                if(dest!=null)
                    mapView.removePOIItem(dest);
                gpsTracker = new GpsTracker(MapActivity.this);
                end.setRoadAddressName("??????");
                end.setPlaceName("?????? ??????");
                end.setX(Double.toString(gpsTracker.getLongitude()));
                end.setY(Double.toString(gpsTracker.getLatitude()));

                Toast.makeText(getApplicationContext(),
                        "?????? ????????? ???????????????.",
                        Toast.LENGTH_LONG).show();
                ((TextView) findViewById(R.id.end_text)).setText(end.getPlaceName());
                mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(),gpsTracker.getLongitude()),true);
                dest = new MapPOIItem();
                dest.setItemName("?????????");
                dest.setTag(0);
                dest.setMapPoint(MapPoint.mapPointWithGeoCoord(gpsTracker.getLatitude(),gpsTracker.getLongitude()));
                dest.setMarkerType(MapPOIItem.MarkerType.BluePin); // ???????????? ???????????? BluePin ?????? ??????.
                dest.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // ????????? ???????????????, ???????????? ???????????? RedPin ?????? ??????.
                mapView.addPOIItem(dest);
            }
        }else if(requestCode == GET_STRING_BUS){
            if(resultCode == RESULT_OK){
                Log.i("?????????","A");
                //  nm      : ?????? ??????.
                //  route   : bus?????? or ?????? ??????
                //  time    : ????????? ?????? ?????? list. route list??? index??? ??????.

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
                Log.i("????????? ??????",SBusnum);
                Log.i("?????????",received.getBusStation()+"");
                Log.i("?????????",received.getRoute_nm());
                Log.i("?????????",received.getBusInfo()+"");
                Log.i("?????????",received.getTimeInfo()+"");

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
                Log.i("?????????","result CODE : " + resultCode);

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
                Toast.makeText(MapActivity.this,"????????? ???????????? ????????????.",Toast.LENGTH_LONG).show(); // ??? ?????????
                mapView.removeAllPolylines();
                busActivityStart();
            }
            else if(resultCode == -100){
                start_text.setText("???????????? ???????????????");
                end_text.setText("???????????? ???????????????");
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
            customMarker.setItemName("?????? ????????? : "+takeBusName);
            customMarker.setTag(1);
            customMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(takeBuslat),Double.parseDouble(takeBuslon)));
            customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // ??????????????? ????????? ????????? ??????.
            customMarker.setCustomImageResourceId(R.drawable.busmarker); // ?????? ?????????.
            customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi ??? ??????????????? ???????????? ???????????? ????????? ?????? ?????? ?????????????????? ????????? ????????? ??????.
            customMarker.setCustomImageAnchor(0.5f, 1.0f); // ?????? ???????????? ????????? ?????? ??????(???????????????) ?????? - ?????? ????????? ?????? ?????? ?????? x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) ???.
            mapView.addPOIItem(customMarker);
            dcustomMarker = new MapPOIItem();
            dcustomMarker.setItemName("?????? ?????? ");
            dcustomMarker.setTag(1);
            dcustomMarker.setMapPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(dtakeBuslat),Double.parseDouble(dtakeBuslon)));
            dcustomMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // ??????????????? ????????? ????????? ??????.
            dcustomMarker.setCustomImageResourceId(R.drawable.busmarker); // ?????? ?????????.
            dcustomMarker.setCustomImageAutoscale(false); // hdpi, xhdpi ??? ??????????????? ???????????? ???????????? ????????? ?????? ?????? ?????????????????? ????????? ????????? ??????.
            dcustomMarker.setCustomImageAnchor(0.5f, 1.0f); // ?????? ???????????? ????????? ?????? ??????(???????????????) ?????? - ?????? ????????? ?????? ?????? ?????? x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) ???.
            mapView.addPOIItem(dcustomMarker);

            MapPolyline polyline = new MapPolyline();
            polyline.setTag(1000);
            polyline.setLineColor(Color.argb(128, 255, 51, 0)); // Polyline ?????? ??????.

// Polyline ?????? ??????.
            polyline.addPoint(MapPoint.mapPointWithGeoCoord(startlat,startlon));
            polyline.addPoint(MapPoint.mapPointWithGeoCoord(Double.parseDouble(takeBuslat),Double.parseDouble(takeBuslon)));
// Polyline ????????? ?????????.
            mapView.addPolyline(polyline);

// ???????????? ??????????????? ???????????? Polyline??? ?????? ???????????? ??????.
            MapPointBounds mapPointBounds = new MapPointBounds(polyline.getMapPoints());
            int padding = 100; // px
            mapView.moveCamera(CameraUpdateFactory.newMapPointBounds(mapPointBounds, padding));


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
