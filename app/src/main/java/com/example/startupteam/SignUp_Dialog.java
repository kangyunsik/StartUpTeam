package com.example.startupteam;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignUp_Dialog extends Dialog {
    SignUp_Dialog m_oDialog;
    TextView tv_id;
    TextView tv_pw;
    TextView tv_email;

    String id;
    String pw;
    String email;

    public SignUp_Dialog(Context context) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.custom_dialog);

        tv_id = this.findViewById(R.id.edit_customD_id);
        tv_pw = this.findViewById(R.id.edit_customD_pw);
        tv_email = this.findViewById(R.id.edit_customD_email);

        tv_id.setText("test3");
        tv_pw.setText("test3");
        tv_email.setText("test3@naver.com");

        m_oDialog = this;

        Button oBtn = (Button) this.findViewById(R.id.button_customD_cancel);
        oBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtn(v);
            }
        });

        Button inBtn = (Button) this.findViewById(R.id.button_customD_signup);
        inBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInBtn(v);
            }
        });
    }

    public void onSignInBtn(View view){
        String id = tv_id.getText().toString();
        String pw= tv_pw.getText().toString();
        String email= tv_email.getText().toString();
        if(!valid(id,pw,email)){
            return;
        }

        URLTh urlth = new URLTh(id,pw,email);
        Thread Sthread = new Thread(urlth);
        Sthread.start();
        try {
            Sthread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onClickBtn(View _oView) {
        this.dismiss();
    }

    class URLTh extends Thread {
        String id,pw,email;

        public URLTh(String id, String pw, String email){
            this.id = id;
            this.pw = pw;
            this.email = email;
        }

        @Override
        public void run() {

            Log.d("쓰레드",id);
            Log.d("쓰레드",pw);
            Log.d("쓰레드",email);
            URL url = null;
            String br_result = "";
            String hashed = SHA256.DoHash(pw);
            try {
                url = new URL("http:" + MapActivity.server_ip + ":" + MapActivity.server_port + "/" + "signin"+
                        "?id="+id+"&pw="+hashed+"&email="+email);
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

            try {
                if (urlCon.getResponseCode() == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader((urlCon.getInputStream()), "UTF-8"));
                    br_result = br.readLine();


                } else {
                    // Error handling code goes here
                    Log.d("Suc", Integer.toString(urlCon.getResponseCode()));
                }
            } catch (IOException e) {
                Log.d("Suc", "fail2");
                e.printStackTrace();

            }
            Log.d("SIGNIN",br_result);
        }


    }

    private boolean valid(String id, String pw, String email){
        if(id.length() < 4){
            Toast.makeText(getContext(),"아이디는 4글자 이상이어야 합니다.",Toast.LENGTH_LONG).show();
            return false;
        }
        if(pw.length() < 4){
            Toast.makeText(getContext(),"비밀번호는 4글자 이상이어야 합니다.",Toast.LENGTH_LONG).show();
            return false;
        }
        if(!email.contains("@")){
            Toast.makeText(getContext(),"올바른 이메일 형식이 아닙니다.",Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
