package com.example.startupteam;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Result_popup extends AppCompatActivity {
    Button button_ok, button_cancel;
    String roadaddress;
    String placename;
    String pointx;
    String pointy;
    String phonenumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_result_popup);
        button_ok = (Button)findViewById(R.id.button_ok);
        button_cancel=(Button)findViewById(R.id.button_cancel);
        TextView place = (TextView)findViewById(R.id.r_placename);
        TextView road = (TextView)findViewById(R.id.r_roadaddress);
        TextView phone = (TextView)findViewById(R.id.r_phone);
        TextView category = (TextView)findViewById(R.id.r_category);
        TextView url = (TextView)findViewById(R.id.r_url);
        TextView distance = (TextView)findViewById(R.id.r_distance);
        TextView x = (TextView)findViewById(R.id.r_x);
        TextView y = (TextView)findViewById(R.id.r_y);
        placename = getIntent().getStringExtra("place_name");
        roadaddress = getIntent().getStringExtra("road_address_name");
        pointx = getIntent().getStringExtra("x");
        pointy = getIntent().getStringExtra("y");
        phonenumber = getIntent().getStringExtra("phone");
        place.setText(getIntent().getStringExtra("place_name"));
        if(roadaddress.equals(""))
            roadaddress= "알 수 없음";
        
        if(phonenumber.equals(""))
            phonenumber="알 수 없음";
        road.setText("도로명주소 : "+roadaddress);
        phone.setText("전화번호 : "+phonenumber);
        category.setText(getIntent().getStringExtra("category_name"));
      /*  url.setText("URL : "+getIntent().getStringExtra("place_url"));
        distance.setText("거리 : "+getIntent().getStringExtra("distance"));*/
        distance.setText("선택하신 장소가 올바르면 확인을 눌러주세요");
        //x.setText("x : "+getIntent().getStringExtra("x"));
       // y.setText("y : "+getIntent().getStringExtra("y"));
    }
    public void onOk(View v){
        Intent intent = new Intent();
        intent.putExtra("road_address_name", roadaddress);
        intent.putExtra("place_name", placename);
        intent.putExtra("x", pointx);
        intent.putExtra("y", pointy);
        setResult(RESULT_OK, intent);
        finish();
    }
    public void onCancel(View v){
        finish();
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