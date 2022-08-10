package com.example.choosephotoapplication.entiy;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class FileImgBean implements Parcelable  {
    private String data;
    private long id;
    private String title;
    private String mimeType;
    private String date;
    private String  size;
    private Uri uri;

    public FileImgBean() {

    }

    public FileImgBean(Parcel in) {
        data = in.readString();
        id = in.readLong();
        title = in.readString();
        mimeType = in.readString();
        date = in.readString();
        size = in.readString();
        uri = in.readParcelable(Uri.class.getClassLoader());
    }

    public static final Creator<FileImgBean> CREATOR = new Creator<FileImgBean>() {
        @Override
        public FileImgBean createFromParcel(Parcel in) {
            return new FileImgBean(in);
        }

        @Override
        public FileImgBean[] newArray(int size) {
            return new FileImgBean[size];
        }
    };




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(data);
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(mimeType);
        dest.writeString(date);
        dest.writeString(size);
        dest.writeParcelable(uri, 0);
    }
    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }




}
