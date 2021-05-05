package com.example.startupteam;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Route implements Parcelable {
    @SerializedName("route_nm")
    @Expose
    private String route_nm;

    @SerializedName("busInfo")
    @Expose
    private ArrayList<String> busInfo;

    @SerializedName("timeInfo")
    @Expose
    private ArrayList<String> timeInfo;


    public String getRoute_nm() {
        return route_nm;
    }

    public ArrayList<String> getBusInfo() {
        return busInfo;
    }

    public ArrayList<String> getTimeInfo() {
        return timeInfo;
    }

    public void setRoute_nm(String route_nm) {
        this.route_nm = route_nm;
    }

    public void setBusInfo(ArrayList<String> busInfo) {
        this.busInfo = busInfo;
    }

    public void setTimeInfo(ArrayList<String> timeInfo) {
        this.timeInfo = timeInfo;
    }


    protected Route() {
        //busInfo = new ArrayList<>();
        //timeInfo = new ArrayList<>();
    }

    protected Route(Parcel in) {
        route_nm = in.readString();
        busInfo = in.createStringArrayList();
        timeInfo = in.createStringArrayList();
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(route_nm);
        dest.writeStringList(busInfo);
        dest.writeStringList(timeInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
