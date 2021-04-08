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

import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ResultActivity extends AppCompatActivity {
    public String getString;
    public String keyword;
    public String str;
    URLTh r;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        getString=getIntent().getStringExtra("keyword");
        r = new URLTh();
        keyword = getString;
        r.start();
    }
    public void JsonParse(String target){
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(target);
            JSONArray memberArray = (JSONArray) jsonObj.get("documents");

            Log.d("test: ","=====Members=====");
            for(int i=0 ; i<5 ; i++){
                JSONObject tempObj = (JSONObject) memberArray.get(i);
                Log.d("sss",""+(i+1)+"번째 멤버의 이름 : "+tempObj.get("place_name"));
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
                    JsonParse(str);
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

