package com.example.a100026051.customapp.pojo;

import java.util.ArrayList;

/**
 * Created by 100026051 on 11/3/16.
 */

public class User {

    private String userid;
    private String username;
    private String email;
    private String gender;
    private int numOfHelp;
    private int numOfHelped;
    private String currentAddress;
    private String phoneNum;
    private double ratingHelp;
    private double ratingHelped;
    private String description;
    private String status;
    private ArrayList<Need> needlist;
    private ArrayList<String> helpingNeedList;      // firebase need node id list
    private ArrayList<String> beingHelpedNeedList;

    private String fireUserNodeId;  // time string of setting username
    private int numOfHelping;
    private int numOfBeingHelped;

    public ArrayList<String> getBeingHelpedNeedList() {
        return beingHelpedNeedList;
    }

    public void setBeingHelpedNeedList(ArrayList<String> beingHelpedNeedList) {
        this.beingHelpedNeedList = beingHelpedNeedList;
    }

    public ArrayList<String> getHelpingNeedList() {
        return helpingNeedList;
    }

    public void setHelpingNeedList(ArrayList<String> helpingNeedList) {
        this.helpingNeedList = helpingNeedList;
    }

    public ArrayList<Need> getNeedlist() {
        return needlist;
    }

    public int getNumOfBeingHelped() {
        return numOfBeingHelped;
    }

    public void setNumOfBeingHelped(int numOfBeingHelped) {
        this.numOfBeingHelped = numOfBeingHelped;
    }

    public int getNumOfHelping() {
        return numOfHelping;
    }

    public void setNumOfHelping(int numOfHelping) {
        this.numOfHelping = numOfHelping;
    }

    public String getFireUserNodeId() {
        return fireUserNodeId;
    }

    public void setFireUserNodeId(String fireUserNodeId) {
        this.fireUserNodeId = fireUserNodeId;
    }

    public double getRatingHelp() {
        return ratingHelp;
    }

    public double getRatingHelped() {
        return ratingHelped;
    }

    public int getNumOfHelp() {
        return numOfHelp;
    }

    public int getNumOfHelped() {
        return numOfHelped;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getStatus() {
        return status;
    }

    public String getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setNeedlist(ArrayList<Need> needlist) {
        this.needlist = needlist;
    }

    public void setNumOfHelp(int numOfHelp) {
        this.numOfHelp = numOfHelp;
    }

    public void setNumOfHelped(int numOfHelped) {
        this.numOfHelped = numOfHelped;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setRatingHelp(double ratingHelp) {
        this.ratingHelp = ratingHelp;
    }

    public void setRatingHelped(double ratingHelped) {
        this.ratingHelped = ratingHelped;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
