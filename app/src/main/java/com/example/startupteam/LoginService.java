package com.example.startupteam;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginService extends IntentService {
    String token;
    public LoginService() {
        super("LoginService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String uriPath = intent.getStringExtra("uripath");
        token = intent.getStringExtra("token");
        String result = "";
        String sendMsg = "";

        HttpURLConnection con = null;

        InputStream is = null;
        OutputStream os = null;

        try {
            URL url = new URL(uriPath);
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);

            os = con.getOutputStream();


            OutputStreamWriter osw;
            osw = new OutputStreamWriter(os);
            sendMsg = "id=" + intent.getStringExtra("id") + "&pw=" + intent.getStringExtra("pw") + "&token=" + token; // GET방식으로 작성해 POST로 보냄
            osw.write(sendMsg);                           // OutputStreamWriter에 담아 전송
            osw.flush();

            // jsp와 통신이 잘 되고, 서버에서 보낸 값 받음.
            if (con.getResponseCode() == con.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(con.getInputStream(), "UTF-8");
                BufferedReader br = new BufferedReader(tmp);

                StringBuffer buffer = new StringBuffer();
                result = br.readLine();
            } else {    // 통신이 실패한 이유를 찍기위한 로그
                Log.i("통신 결과", con.getResponseCode() + "에러");
            }

            Log.i("Suc", "[" + result + "]");

            result = result.split("data")[1].split(",")[0].replaceAll("\":", "");
            result = result.substring(1, result.length() - 1);

            Log.i("Suc", "[" + result + "]");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Bundle extras = intent.getExtras();
        if (extras != null) {
            Messenger messenger = (Messenger) extras.get("MESSENGER");
            Message msg = Message.obtain();
            msg.arg1 = Activity.RESULT_OK;
            msg.obj = result;
            try {
                messenger.send(msg);
            } catch (android.os.RemoteException e) {
                Log.e(getClass().getName(), "Exception sending message", e);
            }
        }
    }
}