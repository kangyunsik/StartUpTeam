package com.example.startupteam;

public class Item {
    public String place_name;
    public String place_url;
    public String address_name;
    public String road_address_name;
    public String phone;
    public double x;
    public double y;
    public double distance;
    public String category_group_name;
    public String category_group_code;
    public String id;
    public String placeUrl;

    public String getPlace_name(){
            return place_name;
    }
    public void setPlace_name(String place_name){
        place_name=this.place_name;
    }
    public void setPlace_url(String place_url){
        place_url=this.place_url;
    }
    public String getPlace_url(){
        return place_url;
    }
    public void setAddress_name(String address_name){
        address_name=this.address_name;
    }
    public String getAddress_name(){
        return address_name;
    }
    public void setRoad_address_name(String road_address_name){
        road_address_name=this.road_address_name;
    }
    public String getRoad_address_name(){
        return road_address_name;
    }
    public void setPhone(String phone){
        phone=this.phone;
    }
    public String getPhone(){
        return phone;
    }
    public void setX(double x){
        x=this.x;
    }
    public double getX(){
        return x;
    }
    public void setY(double y){
        y=this.y;
    }
    public double getY(){
        return y;
    }
    public void setDistance(double distance){
        distance=this.distance;
    }
    public double getDistance(){
        return distance;
    }
    public void setCategory_group_code(String category_group_code){
        category_group_code=this.category_group_code;
    }
    public String getCategory_group_code(){
        return category_group_code;
    }
    public void setCategory_group_name(String category_group_name){
        category_group_name=this.category_group_name;
    }
    public String getCategory_group_name(){
        return category_group_name;
    }

    public void setId(String id){
        id=this.id;
    }
    public String getId(){
        return id;
    }
    public void setPlaceUrl(String placeUrl){
        placeUrl=this.placeUrl;
    }
    public String getPlaceUrl(){
        return placeUrl;
    }

}