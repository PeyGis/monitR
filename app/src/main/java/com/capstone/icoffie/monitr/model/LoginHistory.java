package com.capstone.icoffie.monitr.model;

/**
 * Created by iCoffie on 1/31/2018.
 * A class to represent an instance of user's login history
 */

public class LoginHistory {
    private  double latitude;
    private double longitude;
    private String deviceName;
    private String deviceIme;
    private String token;
    private String status;
    private String date;

    /**
     * Constructor
     * @param latitude latitude
     * @param longitude longitude
     * @param deviceName device name
     * @param deviceIme device imei
     * @param token user token from service provider
     * @param status login status
     * @param date login date
     */
    public LoginHistory(double latitude, double longitude, String deviceName, String deviceIme, String token, String status, String date) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.deviceName = deviceName;
        this.deviceIme = deviceIme;
        this.token = token;
        this.status = status;
        this.date = date;
    }

    /**
     * a function to return latitude from login history object
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    /**
     * a function to return longitude from login history object
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    /**
     * a function to return device name from login history object
     * @return device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    /**
     * a function to return device imei from login history object
     * @return device imei
     */
    public String getDeviceIme() {
        return deviceIme;
    }

    public void setDeviceIme(String deviceIme) {
        this.deviceIme = deviceIme;
    }
    /**
     * a function to return token from login history object
     * @return token
     */
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    /**
     * a function to return status from login history object
     * @return status
     */
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    /**
     * a function to return date from login history object
     * @return date
     */
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
