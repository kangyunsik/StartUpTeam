package com.example.startupteam;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {
    public String getString;
    public String keyword;
    public String str;
    private ArrayList<Document> places = new ArrayList<Document>();
    ResultAdapter myAdapter;
    URLTh r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //리스트뷰
        getString=getIntent().getStringExtra("keyword");
        r = new URLTh();
        keyword = getString;
        r.start();
        try {
            r.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("test2","aa");
        ListView listView = (ListView)findViewById(R.id.listView);
        myAdapter = new ResultAdapter(this,places);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id){
                Toast.makeText(getApplicationContext(),
                        myAdapter.getItem(position).getPlaceName(),
                        Toast.LENGTH_LONG).show();
            }
        });

        Log.d("test2","aa");

        /*for(int i=0;i<5;i++){
            Log.d("test2",places.get(i).getPlaceName());
        }*/

    }


    public void JsonParse(String target){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(target);
            JSONArray memberArray = (JSONArray) jsonObj.get("documents");

            Log.d("test: ","=====Members=====");
            for(int i=0 ; i<5 ; i++){
                JSONObject tempObj = (JSONObject) memberArray.get(i);
                Document set = new Document();
                set.setAddressName(tempObj.get("road_address_name").toString());
                set.setPlaceName(tempObj.get("place_name").toString());
                places.add(set);
                Log.d("sss",""+(i+1)+"번째 멤버의 이름 : "+places.get(i).getPlaceName());
                Log.d("sss",""+(i+1)+"번째 멤버의 주소: "+tempObj.get("road_address_name"));
            }

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    class URLTh extends Thread {

        @Override
        public void run() {
            URL url = null;
            try {
                Log.d("textcheck3", keyword);
                url = new URL("https://dapi.kakao.com/v2/local/search/keyword.json?radius=20000&size=5&query=" + keyword);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlCon = null;
            try {
                urlCon = (HttpURLConnection) url.openConnection();
                urlCon.setRequestProperty("Authorization", "KakaoAK 08524df1fb5ba759f2cfd86e83401e49");
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
                    str = br.readLine();
                    Log.d("Suc", str);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            JsonParse(str);
                        }
                    });
                    Log.d("Suc", "success");
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
}

