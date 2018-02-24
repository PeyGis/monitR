package com.capstone.icoffie.monitr.model;

/**
 * Created by iCoffie on 2/24/2018.
 */

public class UserDeviceModel {
    private String deviceName;
    private String deviceIMEI;
    private String deviceType;
    private String accountId;

    public UserDeviceModel(String deviceName, String deviceIMEI, String deviceType, String accountId) {
        this.deviceName = deviceName;
        this.deviceIMEI = deviceIMEI;
        this.deviceType = deviceType;
        this.accountId = accountId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceIMEI() {
        return deviceIMEI;
    }

    public void setDeviceIMEI(String deviceIMEI) {
        this.deviceIMEI = deviceIMEI;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
