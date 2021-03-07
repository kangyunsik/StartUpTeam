package com.example.startupteam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_CODE = 100;

    String[] required_permission ={
            Manifest.permission.ACCESS_FINE_LOCATION        // REQUIRED PERMISSIONs
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //위치정보 접근권한 허용작업
        checkPermission();
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck!= PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this,"권한 승인이 필요합니다",Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                Toast.makeText(this,"애플리케이션 사용을 위해 위치정보 권한이 필요합니다.",Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        PERMISSIONS_REQUEST_CODE);
                Toast.makeText(this,"애플리케이션 사용을 위해 위치정보 권한이 필요합니다.",Toast.LENGTH_LONG).show();

            }
        }


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

//        Btn_SignIn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                String id = edit_id.getText().toString();
//                String password = edit_password.getText().toString();
//                int response;
//
//                Intent intent = new Intent(getApplicationContext(), MapActivity.class);     // After Sign in, Straight to Next Activity.
//                intent.putExtra("id",id);
//                intent.putExtra("password",password);
//
//
//                response = checker.getResponse();
//
//                if(response == 1) {
//                    startActivity(intent);
//                }else{
//                    finish();
//                }
//            }
//        });


    }
    private void checkPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 다시 보지 않기 버튼을 만드려면 이 부분에 바로 요청을 하도록 하면 됨 (아래 else{..} 부분 제거)
            // ActivityCompat.requestPermissions((Activity)mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_CAMERA);

            // 처음 호출시엔 if()안의 부분은 false로 리턴 됨 -> else{..}의 요청으로 넘어감
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("알림")
                        .setMessage("위치정보 권한이 거부되었습니다. 사용을 원하시면 설정에서 해당 권한을 직접 허용하셔야 합니다.")
                        .setNeutralButton("설정", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            }
                        })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_CODE);
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