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
        place.setText("장소명 : "+getIntent().getStringExtra("place_name"));
        road.setText("도로명주소 : "+getIntent().getStringExtra("road_address_name"));
        phone.setText("전화번호 : "+getIntent().getStringExtra("phone"));
        category.setText(getIntent().getStringExtra("category_name"));
        url.setText("URL : "+getIntent().getStringExtra("place_url"));
        distance.setText("거리 : "+getIntent().getStringExtra("distance"));
        x.setText("x : "+getIntent().getStringExtra("x"));
        y.setText("y : "+getIntent().getStringExtra("y"));
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