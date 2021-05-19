package com.example.startupteam;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Bus_popup extends AppCompatActivity {
    private Route received;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_bus_popup);

        TextView rtnm = findViewById(R.id.bus_popup_routenm);
        TextView rt = findViewById(R.id.bus_popup_route);
        TextView time = findViewById(R.id.bus_popup_time);

        received = getIntent().getParcelableExtra("route");

        rtnm.setText("경로 번호: " + received.getRoute_nm());
        rt.setText("수단 : " + received.getBusInfo());
        time.setText("총 소요 시간 : " +  received.getTimeInfo());
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.bus_popup_ok:
                //Intent intent = new Intent();
                setResult(RESULT_OK,getIntent());
                finish();
                break;
            case R.id.bus_popup_cancel:
                finish();
                break;
        }
    }

    private String ArrayListToString(ArrayList<String> trans){
        StringBuilder sb = new StringBuilder();
        for(String s : trans)
            sb.append(s+" ");
        if(trans != null)
            sb.substring(0,sb.length()-1);
        return sb.toString();
    }

    private String getTimeSum(ArrayList<String> times){
        int hour = 0,min = 0;


        String temp1 = "0",temp2 = "0";
        for(String s : times){
            if(s.contains("시간")){
                temp1 = s.split("시간")[0];
                s = s.split("시간")[1];
            }
            temp2 = s.split("분")[0];
            min += Integer.parseInt(temp1)*60 + Integer.parseInt(temp2);
        }

        hour = min/60;
        min = min%60;

        return hour+"시간 "+min+"분";
    }

    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    public void onBackPressed(){
        return;
    }
}
