package com.example.startupteam;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class CategoryResult implements Parcelable {

    @SerializedName("meta")
    @Expose
    private Meta meta;
    @SerializedName("documents")
    @Expose
    private List<Document> documents = null;

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.meta, flags);
        dest.writeList(this.documents);
    }

    public CategoryResult() {
    }

    protected CategoryResult(Parcel in) {
        this.meta = in.readParcelable(Meta.class.getClassLoader());
        this.documents = new ArrayList<Document>();
        in.readList(this.documents, Document.class.getClassLoader());
    }

    public static final Creator<com.example.startupteam.CategoryResult> CREATOR = new Creator<com.example.startupteam.CategoryResult>() {
        @Override
        public com.example.startupteam.CategoryResult createFromParcel(Parcel source) {
            return new com.example.startupteam.CategoryResult(source);
        }

        @Override
        public com.example.startupteam.CategoryResult[] newArray(int size) {
            return new com.example.startupteam.CategoryResult[size];
        }
    };
}
