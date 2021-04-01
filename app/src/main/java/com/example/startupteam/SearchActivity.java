package com.example.startupteam;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchActivity extends AppCompatActivity {
    EditText sch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        sch = (EditText)findViewById(R.id.editTextSearch);

    }
    public void onClickFButton(View v){
        Toast.makeText(getApplicationContext(), sch.getText(),
                Toast.LENGTH_SHORT).show();
        URLTh skeyword = new URLTh();
        skeyword.setKeyword(sch.getText().toString());
        skeyword.start();
    }





}