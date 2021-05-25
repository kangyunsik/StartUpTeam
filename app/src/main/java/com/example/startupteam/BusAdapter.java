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
        ArrayList<String> bnumcollect = new ArrayList<String>();
        ArrayList<String> bscollect = new ArrayList<String>();
        TextView[] busnumView = new TextView[5];
        TextView[] busStationView = new TextView[5];
        TextView routeView = (TextView)view.findViewById(R.id.route_nm);
        TextView wayView = (TextView)view.findViewById(R.id.way);
        TextView timeView = (TextView)view.findViewById(R.id.time);
        TextView totalView = (TextView)view.findViewById(R.id.TotalTime);
        TextView leftView = (TextView)view.findViewById(R.id.LeftTime);
        busnumView[0] = (TextView)view.findViewById(R.id.FirstBusnum);
        busStationView[0] = (TextView)view.findViewById(R.id.FirstBusStation);
        busnumView[1] = (TextView)view.findViewById(R.id.SecondBusnum);
        busStationView[1] = (TextView)view.findViewById(R.id.SecondBusStation);
        busnumView[2] = (TextView)view.findViewById(R.id.ThirdBusnum);
        busStationView[2] = (TextView)view.findViewById(R.id.ThirdBusStation);
        routes.get(position);

        routeView.setText(routes.get(position).getRoute_nm());
        int count = 0;
        for(String s : routes.get(position).getBusInfo()) {
            if(s.equals("0"))
                s = "도보";
            else {
                bnumcollect.add(s);
                bscollect.add(routes.get(position).getBusStation().get(count));
            }
            busInfo += s + "    \t\t";
            count++;
        }
        wayView.setText(busInfo);
        leftView.setText(routes.get(position).getLeftTime());
        for(String s : routes.get(position).getTimeInfo())
            timeInfo += s+"    \t\t";
        timeView.setText(timeInfo);
        int total=0;
        for(int i=0;i<routes.get(position).getTimeInfo().size();i++) {
            int plus = Integer.parseInt(routes.get(position).getTimeInfo().get(i).replace("분",""));
            total =  total + plus;
        }
        if(total>=60)
            totalView.setText("총 " +Integer.toString(total/60)+"시간 "+Integer.toString(total%60)+"분");
        else
            totalView.setText("총 "+Integer.toString(total)+"분");
        for(int i=0;i<bnumcollect.size();i++){
            busnumView[i].setText(bnumcollect.get(i));
            busStationView[i].setText(bscollect.get(i));
        }
        busnumView[bnumcollect.size()].setText("하차");
        busStationView[bnumcollect.size()].setText(routes.get(position).getLastStation());
        return view;
    }
    public void addItem(ArrayList<String> busInfo, ArrayList<String> timeInfo, ArrayList<String> busStation,String lastStation,String leftTime) {
        Route rt = new Route();

        rt.setRoute_nm((routes.size()+1)+"");
        rt.setBusInfo(busInfo);
        rt.setTimeInfo(timeInfo);
        rt.setBusStation(busStation);
        rt.setLastStation(lastStation);
        rt.setLeftTime(leftTime);
        routes.add(rt);
    }
}
