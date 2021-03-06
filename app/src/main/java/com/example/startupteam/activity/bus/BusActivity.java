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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus);

        start_point = getIntent().getParcelableExtra("start");
        end_point = getIntent().getParcelableExtra("end");

        routes = new ArrayList<>();
        settingRoutes();

        myAdapter = new BusAdapter(this,routes);
        listView = (ListView) findViewById(R.id.listView_bus);
        listView.setAdapter(myAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Bus_popup.class);
                intent.putExtra("route",myAdapter.getItem(position));
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
            String uriPath = "http:"+MapActivity.server_ip +":"+ MapActivity.server_port + "/"+server_locate;
            String sendMsg = "";
            String result="";

            HttpURLConnection con = null;
            InputStream is = null;
            OutputStream os = null;
            BufferedReader br = null;

            try {
                URL url = new URL(uriPath);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setDoOutput(true);

                os = con.getOutputStream();

                OutputStreamWriter osw;
                osw = new OutputStreamWriter(os);
                sendMsg =   "start_x="  + start_point.getX() +
                            "&start_y=" + start_point.getY() +
                            "&end_x="   + end_point.getX()   +
                            "&end_y="   + end_point.getY();
                osw.write(sendMsg);
                osw.flush();

                // jsp??? ????????? ??? ??????, ???????????? ?????? ??? ??????.
                if (con.getResponseCode() == con.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(con.getInputStream(), "UTF-8");
                    br = new BufferedReader(tmp);

                    StringBuffer buffer = new StringBuffer();
                    result = br.readLine();
                } else {    // ????????? ????????? ????????? ???????????? ??????
                    Log.i("?????? ??????", con.getResponseCode() + "??????");
                }

                myParse(result);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*routes = new ArrayList<>();
            String result = "1??????39<total> 1????????????(???1???)<left> 4???<time> 0<busnum> 0<station> 17???<time> 643<busnum> ???????????????<station> 6???<time> 0<busnum> 0<station> 45???<time> 148<busnum> ????????????????????????<station> 24???<time> 173<busnum> ??????????????????<station> 3???<time> 0<busnum> 0<station> ????????????<last>1??????39<total> 6????????????(???10???)<left> 4???<time> 0<busnum> 0<station> 17???<time> 5524<busnum> ???????????????<station> 6???<time> 0<busnum> 0<station> 45???<time> 148<busnum> ????????????????????????<station> 24???<time> 173<busnum> ??????????????????<station> 3???<time> 0<busnum> 0<station> ????????????<last>1??????45<total> 1????????????(???1???)<left> 4???<time> 0<busnum> 0<station> 17???<time> 643<busnum> ???????????????<station> 6???<time> 0<busnum> 0<station> 51???<time> 148<busnum> ????????????????????????<station> 5???<time> 0<busnum> 0<station> 18???<time> 1137<busnum> ?????????????????????????????????<station> 4???<time> 0<busnum> 0<station> ???????????????<last>1??????45<total> 6????????????(???10???)<left> 4???<time> 0<busnum> 0<station> 17???<time> 5524<busnum> ???????????????<station> 6???<time> 0<busnum> 0<station> 51???<time> 148<busnum> ????????????????????????<station> 5???<time> 0<busnum> 0<station> 18???<time> 1137<busnum> ?????????????????????????????????<station> 4???<time> 0<busnum> 0<station> ???????????????<last>1??????46<total> 6????????????(???10???)<left> 4???<time> 0<busnum> 0<station> 17???<time> 5524<busnum> ???????????????<station> 6???<time> 0<busnum> 0<station> 51???<time> 148<busnum> ????????????????????????<station> 7???<time> 0<busnum> 0<station> 17???<time> 1017<busnum> ????????????<station> 4???<time> 0<busnum> 0<station> ???????????????<last>";
            myParse(result);*/
        }
    }

    public void myParse(String in){
        String[] strings = in.split(",");
        for(String s : strings){
            mySubParse(s);
        }
    }
//
    public void mySubParse(String in){
        in = in.replaceAll("??????","");
        in = in.replaceAll("\"","");
        in = in.replaceAll("spanclasstxttransspan", "");
        String arg = in.split("<")[0];
        if(in.contains("<total>")){
            Route rt = new Route();
            rt.setTotalTime(arg.replace("[",""));
            rt.setRoute_nm((routes.size()+1)+"");
            Log.d("totaltime",rt.getTotalTime());
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
        else if(in.contains("<last>")){
            if(routes.get(routes.size()-1).getLastStation() == null){
                routes.get(routes.size()-1).setLastStation(arg);
            }
        }
    }
}

