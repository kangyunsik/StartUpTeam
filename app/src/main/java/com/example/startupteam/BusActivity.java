package com.example.startupteam;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BusActivity extends AppCompatActivity {
    private Document start_point;
    private Document end_point;
    private ArrayList<Route> routes;
    private BusAdapter myAdapter;
    private ListView listView;
    public static final int GET_BUS_RTNM = 9999;
    public static final String server_locate = "getRoute";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("생성","0");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        Log.i("생성","1");

        routes = new ArrayList<>();

        settingRoutes();

        myAdapter = new BusAdapter(this,routes);
        listView = (ListView) findViewById(R.id.listView_bus);
        listView.setAdapter(myAdapter);

        Log.i("생성","2");

        start_point = getIntent().getParcelableExtra("start_point");
        end_point = getIntent().getParcelableExtra("end_point");

        //keyword = getString;




        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                //Intent intent = new Intent(getApplicationContext(), Bus_popup.class);
                //intent.putExtra("road_address_name", myAdapter.getItem(position).getRoadAddressName());
                //startActivityForResult(intent,1);
                Intent intent = new Intent(getApplicationContext(), Bus_popup.class);
                intent.putExtra("routeNm", myAdapter.getItem(position).getRoute_nm());
                intent.putExtra("busInfo", myAdapter.getItem(position).getBusInfo());
                intent.putExtra("timeInfo", myAdapter.getItem(position).getTimeInfo());
                startActivityForResult(intent,GET_BUS_RTNM);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GET_BUS_RTNM){
            if(resultCode == RESULT_OK){
                setResult(RESULT_OK,data);
                finish();
            }
        }
    }

    public void settingRoutes(){
        //                                    testSetting();  // 하드코딩. TEST용도.
        //  ==========      == SERVER랑 통신해서 ROUTE 갖고오는 코드 구현 필요  ================

        URLth_bus urlth_bus = new URLth_bus();
        Thread thread = new Thread(urlth_bus);
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    class URLth_bus implements Runnable{
        @Override
        public void run() {
            String uriPath = MapActivity.server_ip +":"+ MapActivity.server_port + "/"+server_locate;

            HttpURLConnection con = null;
            InputStream is = null;
            BufferedReader br = null;

            /*try {
                //finalURL = uriPath + "?x=" + gpsTracker.getLatitude() + "&y=" + gpsTracker.getLongitude();
                URL url = new URL(uriPath);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setDoOutput(true);

                is = con.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                String result;
                while ((result = br.readLine()) != null) {
                    Log.i("Suc", "!" + result + "!");
                }
                myParse(result);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

            routes = new ArrayList<>();
            String result = "1시간39<total> 1정류장전(약1분)<left> 4분<time> 0<busnum> 0<station> 17분<time> 643<busnum> 낙성대입구<station> 6분<time> 0<busnum> 0<station> 45분<time> 148<busnum> 방배동삼호아파트<station> 24분<time> 173<busnum> 숭례초등학교<station> 3분<time> 0<busnum> 0<station> 광운대역<last>1시간39<total> 6정류장전(약10분)<left> 4분<time> 0<busnum> 0<station> 17분<time> 5524<busnum> 낙성대입구<station> 6분<time> 0<busnum> 0<station> 45분<time> 148<busnum> 방배동삼호아파트<station> 24분<time> 173<busnum> 숭례초등학교<station> 3분<time> 0<busnum> 0<station> 광운대역<last>1시간45<total> 1정류장전(약1분)<left> 4분<time> 0<busnum> 0<station> 17분<time> 643<busnum> 낙성대입구<station> 6분<time> 0<busnum> 0<station> 51분<time> 148<busnum> 방배동삼호아파트<station> 5분<time> 0<busnum> 0<station> 18분<time> 1137<busnum> 송중동경남아너스빌정문<station> 4분<time> 0<busnum> 0<station> 월계삼거리<last>1시간45<total> 6정류장전(약10분)<left> 4분<time> 0<busnum> 0<station> 17분<time> 5524<busnum> 낙성대입구<station> 6분<time> 0<busnum> 0<station> 51분<time> 148<busnum> 방배동삼호아파트<station> 5분<time> 0<busnum> 0<station> 18분<time> 1137<busnum> 송중동경남아너스빌정문<station> 4분<time> 0<busnum> 0<station> 월계삼거리<last>1시간46<total> 6정류장전(약10분)<left> 4분<time> 0<busnum> 0<station> 17분<time> 5524<busnum> 낙성대입구<station> 6분<time> 0<busnum> 0<station> 51분<time> 148<busnum> 방배동삼호아파트<station> 7분<time> 0<busnum> 0<station> 17분<time> 1017<busnum> 창문여고<station> 4분<time> 0<busnum> 0<station> 월계삼거리<last>";
            myParse(result);
        }
    }

    public void myParse(String in){
        in.replace("[","");
        in.replace("]","");

        String[] strings = in.split(",");
        for(String s : strings){
            mySubParse(s);
        }
    }

    public void mySubParse(String in){
        in.replace("\"","");
        in.replace("\"","");
        String arg = in.split("<")[0];
        if(in.contains("<total>")){
            Route rt = new Route();
            rt.setTotalTime(arg);
            routes.add(rt);
        }else if(in.contains("<left>")){
            routes.get(routes.size()-1).setLeftTime(arg);
        }else if(in.contains("<time>")){
            if(routes.get(routes.size()-1).getTimeInfo() == null){
                routes.get(routes.size()-1).setTimeInfo(new ArrayList<String>());
            }
            routes.get(routes.size()-1).getTimeInfo().add(arg);
        }else if(in.contains("<busnum>")){
            if(routes.get(routes.size()-1).getBusInfo() == null){
                routes.get(routes.size()-1).setBusInfo(new ArrayList<String>());
            }
            routes.get(routes.size()-1).getBusInfo().add(arg);
        }else if(in.contains("<station>")){
            if(routes.get(routes.size()-1).getBusStation() == null){
                routes.get(routes.size()-1).setBusStation(new ArrayList<String>());
            }
            routes.get(routes.size()-1).getBusStation().add(arg);
        }
    }

    public void testSetting(){
        Route rt1 = new Route();
        Route rt2 = new Route();
        ArrayList<String> rt1_busInfo = new ArrayList<>(),rt1_timeInfo= new ArrayList<>(),rt2_busInfo= new ArrayList<>(),rt2_timeInfo= new ArrayList<>();


        rt1_busInfo.add("66번");
        rt1_busInfo.add("도보");
        rt1_busInfo.add("10번");
        rt1_timeInfo.add("12분");
        rt1_timeInfo.add("3분");
        rt1_timeInfo.add("15분");

        rt2_busInfo.add("66-4번");
        rt2_busInfo.add("도보");
        rt2_busInfo.add("25번");
        rt2_timeInfo.add("10분");
        rt2_timeInfo.add("5분");
        rt2_timeInfo.add("21분");


        rt1.setRoute_nm("1");
        rt1.setBusInfo(rt1_busInfo);
        rt1.setTimeInfo(rt1_timeInfo);
        rt2.setRoute_nm("2");
        rt2.setBusInfo(rt2_busInfo);
        rt2.setTimeInfo(rt2_timeInfo);


        routes.add(rt1);
        routes.add(rt2);
    }
}
