package com.capstone.icoffie.monitr.model;

/**
 * Created by iCoffie on 1/31/2018.
 */

public class LoginHistory {
    private  double latitude;
    private double longitude;
    private String deviceName;
    private String deviceIme;
    private String token;
    private String status;
    private String date;

    public LoginHistory(double latitude, double longitude, String deviceName, String deviceIme, String token, String status, String date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.deviceName = deviceName;
        this.deviceIme = deviceIme;
        this.token = token;
        this.status = status;
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

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceIme() {
        return deviceIme;
    }

    public void setDeviceIme(String deviceIme) {
        this.deviceIme = deviceIme;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
