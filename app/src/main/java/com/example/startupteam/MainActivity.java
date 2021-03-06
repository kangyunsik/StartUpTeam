package com.example.startupteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button Btn_SignIn = (Button) findViewById(R.id.button_login);
        Button Btn_SignUp = (Button) findViewById(R.id.button_left);
        Button Btn_PwFind = (Button) findViewById(R.id.button_right);

        TextView editText_id = (TextView) findViewById(R.id.edit_id);
        TextView editText_pw = (TextView) findViewById(R.id.edit_pw);

        RadioButton RdBtn_consist = (RadioButton) findViewById(R.id.radioButton);

        Btn_SignIn.setText("로그인");
        Btn_SignUp.setText("회원가입");
        Btn_PwFind.setText("비밀번호 찾기");
        RdBtn_consist.setText("로그인 상태 유지");

        Intent intent = getIntent();

        Btn_SignIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                EditText edit_id = (EditText) findViewById(R.id.edit_id);
                EditText edit_password = (EditText) findViewById(R.id.edit_pw);

                String id = edit_id.getText().toString();
                String password = edit_password.getText().toString();

                Intent intent = new Intent(getApplicationContext(), MapActivity.class);     // After Sign in, Straight to Next Activity.

                //intent.putExtra("id",id);
                //intent.putExtra("password",password);
                startActivity(intent);
            }
        });


    }


}