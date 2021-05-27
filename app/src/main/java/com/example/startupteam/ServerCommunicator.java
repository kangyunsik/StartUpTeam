package com.example.startupteam;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ServerCommunicator extends IntentService {

    public ServerCommunicator(){
        super("ServerCommunicator");
        Log.i("Suc","const");
    }

    @Override
    protected void onHandleIntent(Intent intent){
        //Uri data = intent.getData();
        String id = intent.getStringExtra("id");
        String uriPath = intent.getStringExtra("uripath");


        String finalURL;

        GpsTracker gpsTracker = new GpsTracker(ServerCommunicator.this);
        Log.e("Suc","aa");
        HttpURLConnection con = null;

        InputStream is = null;
        BufferedReader br = null;

        Bundle extras = intent.getExtras();
        if(extras!=null){
            Messenger messenger = (Messenger) extras.get("MESSENGER");
            Message msg = Message.obtain();
            msg.arg1 = Activity.RESULT_OK;
            msg.obj = "하차 알림을 시작합니다.";
            try{
                messenger.send(msg);
            }catch(android.os.RemoteException e){
                Log.e(getClass().getName(),"Exception sending message", e);
            }
        }

        String result ="ok";
        do {
            try {
                Thread.sleep(3000);
                gpsTracker.getLocation();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                finalURL = uriPath + "?x=" + gpsTracker.getLatitude() + "&y=" + gpsTracker.getLongitude() +"&id="+id;
                URL url = new URL(finalURL);
                con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setDoInput(true);
                con.setDoOutput(true);

                is = con.getInputStream();
                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

                result = br.readLine();

                Log.i("Suc","["+result+"]");

                Log.i("Suc","TT");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }while(result.equals("ok"));

    }
}
