package com.example.startupteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    String[] required_permission ={
            Manifest.permission.ACCESS_FINE_LOCATION        // REQUIRED PERMISSIONs
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Checker checker = new Checker();                    // PERMISSION CHECKER THREAD
        checker.start();

        EditText edit_id = findViewById(R.id.edit_id);
        EditText edit_password = findViewById(R.id.edit_pw);
        edit_id.setText("");

        Button Btn_SignIn = findViewById(R.id.button_login);
        Button Btn_SignUp = findViewById(R.id.button_left);
        Button Btn_PwFind = findViewById(R.id.button_right);

        RadioButton RdBtn_consist = findViewById(R.id.radioButton);

        Btn_SignIn.setText("로그인");
        Btn_SignUp.setText("회원가입");
        Btn_PwFind.setText("비밀번호 찾기");
        RdBtn_consist.setText("로그인 상태 유지");

        Btn_SignIn.setOnClickListener((View v) -> {
            String id = edit_id.getText().toString();
            String password = edit_password.getText().toString();
            int response;

            Intent intent = new Intent(getApplicationContext(), MapActivity.class);     // After Sign in, Straight to Next Activity.
            intent.putExtra("id",id);
            intent.putExtra("password",password);

            response = checker.getResponse();

            if(response == 1) {
                startActivity(intent);
            }else{
                finish();
            }
        });


        Button test_btn = findViewById(R.id.test_button);
        test_btn.setOnClickListener((View v)->{
            Intent intent = new Intent();
            intent.setData(Uri.parse("kakaomap://route?sp=37.537229,127.005515&ep=37.4979502,127.0276368&by=PUBLICTRANSIT"));
            startActivity(intent);

        });

        Button test_btn2 = findViewById(R.id.test_button2);
        test_btn2.setOnClickListener((View v)->{
            URLTh thread = new URLTh();
            thread.start();

            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    private class URLTh extends Thread{
        @Override
        public void run(){
            URL url = null;
            try {
                url = new URL("https://dapi.kakao.com/v2/local/search/keyword.json?y=37.514322572335935&x=127.06283102249932&radius=20000&query=cafe");
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
    }

    private class Checker extends Thread {
        private int response;

        public Checker() {
            // initializing
        }

        protected int getResponse() {
            return this.response;
        }

        @Override
        public void run() {
            for(String permit : required_permission) {
                int check = checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

                if (check == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(required_permission,0);
                }
            }
            this.response = 1;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==0)
        {
            for(int grantResult : grantResults)
            {
                if(grantResult==PackageManager.PERMISSION_GRANTED){         // If allowed
                    Toast.makeText(getApplicationContext(),"앱 권한을 허용했습니다.",Toast.LENGTH_LONG).show();
                }
                else {                                                      // If not allowed
                    Toast.makeText(getApplicationContext(),"앱 권한이 필요합니다.",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

}