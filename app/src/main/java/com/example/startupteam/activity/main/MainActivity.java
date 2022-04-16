package com.example.startupteam;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private String tok;
    private static String id;

    private Handler handler = new Handler((message) -> {
        Object path = message.obj;
        if (message.arg1 == RESULT_OK && path != null) {

            if (((String) path).equals("OK")) {
                Intent intent = new Intent(getApplicationContext(), MapActivity.class);     // After Sign in, Straight to Next Activity.
                intent.putExtra("id", id);
                intent.putExtra("token",tok);
                startActivity(intent);
                Toast.makeText(this, id + "님, 환영합니다.", Toast.LENGTH_LONG).show();

            } else if (((String) path).equals("FAIL")) {
                Toast.makeText(this, "서버로부터 로그인 인증을 실패했습니다.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "서버로부터 잘못된 응답을 받았습니다.", Toast.LENGTH_LONG).show();
            }


        }
        return false;
    });


    String[] required_permission = {
            Manifest.permission.ACCESS_FINE_LOCATION        // REQUIRED PERMISSIONs
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("device", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("ttttttttt",token);
                        tok = token;
                        // Log and toast

                    }
                });



        Checker checker = new Checker();                    // PERMISSION CHECKER THREAD
        checker.start();

        EditText edit_id = findViewById(R.id.edit_id);
        EditText edit_password = findViewById(R.id.edit_pw);
        edit_id.setText("");

        //HardCoding with TEST Account id,pw = (test1,test1)
        edit_id.setText("test1");
        edit_password.setText("test1");
        //==================================================

        Button Btn_SignIn = findViewById(R.id.button_login);

        Btn_SignIn.setOnClickListener((View v) -> {
            id = edit_id.getText().toString();
            String password = edit_password.getText().toString();
            int response;

            Intent intent = new Intent(this, LoginService.class);
            String scURL = "http://" + MapActivity.server_ip + ":" + MapActivity.server_port + "/login";
            Messenger messenger = new Messenger(handler);
            intent.putExtra("MESSENGER", messenger);
            intent.putExtra("uripath", scURL);
            intent.putExtra("id", id);
            intent.putExtra("token",tok);
            intent.putExtra("pw", password);
            intent.setData(Uri.parse(scURL));
            startService(intent);


            response = checker.getResponse();
            if (response != 1) {
                Toast.makeText(this, "위치 권한이 설정되지 않아 종료됩니다.", Toast.LENGTH_LONG);
                finish();
            }
        });

        Button Btn_SignUp = findViewById(R.id.button_sign);

        Btn_SignUp.setOnClickListener((View v) -> {
            SignUp_Dialog oDialog = new SignUp_Dialog(this);
            oDialog.setCancelable(false);
            oDialog.show();
        });
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
            for (String permit : required_permission) {
                int check = checkCallingOrSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

                if (check == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(required_permission, 0);
                }
            }
            this.response = 1;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {         // If allowed
                    Toast.makeText(getApplicationContext(), "앱 권한을 허용했습니다.", Toast.LENGTH_LONG).show();
                } else {                                                      // If not allowed
                    Toast.makeText(getApplicationContext(), "앱 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

}