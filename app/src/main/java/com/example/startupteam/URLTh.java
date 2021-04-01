package com.example.startupteam;

import android.text.Editable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class URLTh extends Thread{
    public String keyword="cafe";
    @Override
    public void run(){
        URL url = null;
        try {
            Log.d("textcheck3", keyword);
            url = new URL("https://dapi.kakao.com/v2/local/search/keyword.json?y=37.514322572335935&x=127.06283102249932&radius=20000&query="+keyword);
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
    public void setKeyword(String keyword){
        this.keyword=keyword;
    }
    public String getKeyword(){
        return keyword;
    }
}
