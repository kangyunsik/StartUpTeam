package com.example.startupteam;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class Transfer_popup extends AppCompatActivity {

    private Boolean ca;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_transfer_popup);
        TextView dest = findViewById(R.id.transfer_popup_dest);
        TextView st = findViewById(R.id.transfer_popup_station);
        TextView lst = findViewById(R.id.transfer_popup_lstation);


        dest.setText("목적지 : "+getIntent().getStringExtra("laststation"));
        if(getIntent().getStringExtra("busstation").equals(getIntent().getStringExtra("laststation"))){
            st.setText("하차 정류장 : "+  getIntent().getStringExtra("busstation"));
            lst.setText("안내를 종료합니다");
            ca = true;
        }
        else {
            st.setText("이번 승차 지점 : " + getIntent().getStringExtra("busstation"));
            lst.setText("현재 승차 지점을 기준으로 재탐색합니다.");
            ca = false;
        }
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.transfer_popup_ok:
                Intent intent = new Intent();
                if(!ca) {
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                }
                else if(ca){
                    finish();
                    break;
                }
        }
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