package com.example.startupteam;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

public class Status_popup extends AppCompatActivity {

    private Route received;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_status_popup);

        TextView dest = findViewById(R.id.status_popup_dest);
        TextView bnum = findViewById(R.id.status_popup_busnum);
        TextView buss = findViewById(R.id.status_popup_buss);

        received = getIntent().getParcelableExtra("route");
        ArrayList<String> bnumcollect = new ArrayList<String>();
        ArrayList<String> bscollect = new ArrayList<String>();
        for(int i=0;i<received.getBusInfo().size();i++) {
            if (received.getBusInfo().get(i).equals("0")){

            }
            else {
                bnumcollect.add(received.getBusInfo().get(i));
                bscollect.add(received.getBusStation().get(i));
                break;
            }
        }
        dest.setText("목적지 : "+getIntent().getStringExtra("dest"));
        bnum.setText("탑승 버스 : " + bnumcollect.get(0));
        buss.setText("탑승 정류장 : " +  bscollect.get(0));
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.status_popup_ok:
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

    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction()== MotionEvent.ACTION_OUTSIDE)
            return false;
        return true;
    }

    public void onBackPressed(){
        return;
    }
}
