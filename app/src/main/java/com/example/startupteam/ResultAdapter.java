package com.example.startupteam;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ResultAdapter extends BaseAdapter {
    Context mContext = null;
    LayoutInflater mLayoutInflater = null;
    ArrayList<Document> docu;

    public ResultAdapter(Context context, ArrayList<Document> data) {
        mContext = context;
        docu = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return docu.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Document getItem(int position) {
        return docu.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        View view = mLayoutInflater.inflate(R.layout.listview_custom, null);


        TextView placeName = (TextView)view.findViewById(R.id.placename);
        TextView roadAdd = (TextView)view.findViewById(R.id.roadaddress);


        placeName.setText(docu.get(position).getPlaceName());
        roadAdd.setText(docu.get(position).getRoadAddressName());

        return view;
    }
    public void addItem(String place, String road) {
        Document item = new Document();

        item.setPlaceName(place);
        item.setAddressName(road);
        docu.add(item);
    }
}
