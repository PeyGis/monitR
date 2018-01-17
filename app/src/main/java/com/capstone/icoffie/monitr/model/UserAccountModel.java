package com.capstone.icoffie.monitr.model;

/**
 * Created by iCoffie on 12/26/2017.
 */

public class UserAccountModel {

    private String accountName;
    private String accountTagline;
    private String accountId;
    private String userAccountId;

    public UserAccountModel(String accountName, String accountTagline, String accountId, String userAccountId) {
        this.accountName = accountName;
        this.accountTagline = accountTagline;
        this.accountId = accountId;
        this.userAccountId = userAccountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountTagline() {
        return accountTagline;
    }

    public void setAccountTagline(String accountTagline) {
        this.accountTagline = accountTagline;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getUserAccountId() {
        return userAccountId;
    }

    public void setUserAccountId(String userAccountId) {
        this.userAccountId = userAccountId;
    }
}
