package com.example.startupteam;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BusAdapter extends BaseAdapter {
    Context mContext = null;
    ArrayList<Route> routes;
    LayoutInflater mLayoutInflater = null;

    public BusAdapter(Context context, ArrayList<Route> data) {
        mContext = context;
        routes = data;
        mLayoutInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return routes.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Route getItem(int position) {
        return routes.get(position);
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        Log.i("버스 어댑터","getView 작동");
        View view = mLayoutInflater.inflate(R.layout.listview_bus, null);
        String busInfo = "",timeInfo = "";

        TextView routeView = (TextView)view.findViewById(R.id.route_nm);
        TextView wayView = (TextView)view.findViewById(R.id.way);
        TextView timeView = (TextView)view.findViewById(R.id.time);
        TextView first = (TextView)view.findViewById(R.id.FirstStation);

        routes.get(position);

        routeView.setText(routes.get(position).getRoute_nm());

        for(String s : routes.get(position).getBusInfo()) {
            if(s.equals("0"))
                s = "도보";
            busInfo += s + "    \t\t";
        }
        wayView.setText(busInfo);

        for(String s : routes.get(position).getTimeInfo())
            timeInfo += s+"    \t\t";
        timeView.setText(timeInfo);
        first.setText("승차 정류장은 "+routes.get(position).getBusStation().get(0)+"입니다.");
        return view;
    }
    public void addItem(ArrayList<String> busInfo, ArrayList<String> timeInfo, ArrayList<String> busStation) {
        Route rt = new Route();

        rt.setRoute_nm((routes.size()+1)+"");
        rt.setBusInfo(busInfo);
        rt.setTimeInfo(timeInfo);
        rt.setBusStation(busStation);
        routes.add(rt);
    }
}
