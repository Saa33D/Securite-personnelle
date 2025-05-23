package com.example.securitpersonnelle;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "alerts")
public class Alert {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public double latitude;
    public double longitude;
    public String date;
    public String photoUri;


    public Alert(double latitude, double longitude, String date, String photoUri) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.photoUri = photoUri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }
}
