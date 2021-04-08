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
    public ArrayList<Document> places;
    ResultAdapter myAdapter;
    ListView listView;
    URLTh r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        //리스트뷰
        listView = (ListView) findViewById(R.id.listView);
        places = new ArrayList<Document>();
        getString = getIntent().getStringExtra("keyword");
        r = new URLTh();
        keyword = getString;
        r.start();
        try {
            r.join();
            Log.d("test3", "join");
        } catch (InterruptedException e) {
            Log.d("test2", "errorrrrrrrrrrrrrrr");
            e.printStackTrace();
        }
        Log.d("test2", Integer.toString(places.size()));


        Log.d("test2", "aa");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                /*Toast.makeText(getApplicationContext(),
                        myAdapter.getItem(position).getPlaceName(),
                        Toast.LENGTH_LONG).show();*/
                Intent intent = new Intent();
                intent.putExtra("road_address_name", myAdapter.getItem(position).getAddressName());
                intent.putExtra("place_name", myAdapter.getItem(position).getPlaceName());
                intent.putExtra("x", myAdapter.getItem(position).getX());
                intent.putExtra("y", myAdapter.getItem(position).getY());
                setResult(RESULT_OK, intent);
                finish();
                //Log.d("test2", myAdapter.getItem(position).getPlaceName());
            }
        });


    }


    public void JsonParse(String target) {
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(target);
            JSONArray memberArray = (JSONArray) jsonObj.get("documents");

            Log.d("test: ", "=====Members=====");
            for (int i = 0; i < 5; i++) {
                final JSONObject tempObj = (JSONObject) memberArray.get(i);
                final Document set = new Document();
                set.setAddressName(tempObj.get("road_address_name").toString());
                set.setPlaceName(tempObj.get("place_name").toString());
                set.setX(tempObj.get("x").toString());
                set.setY(tempObj.get("y").toString());
                places.add(set);
                Log.d("sss", "" + (i + 1) + "번째 멤버의 이름 : " + places.get(i).getPlaceName());
                Log.d("sss", "" + (i + 1) + "번째 멤버의 주소: " + tempObj.get("road_address_name"));
                Log.d("sss", "size : " + Integer.toString(places.size()));
            }
            myAdapter = new ResultAdapter(this, places);
            listView.setAdapter(myAdapter);

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
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

