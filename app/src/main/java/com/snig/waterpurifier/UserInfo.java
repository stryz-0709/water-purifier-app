package com.snig.waterpurifier;

public class UserInfo {

    public String name, email, username, password, gender, dob, activity, climate;
    public int id, height, weight, dailyGoal, currentGoal;

    public UserInfo(int id, String name, String email, String username, String password, String gender,
                    String dob, String activity, String climate, int height, int weight, int dailyGoal, int currentGoal) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.activity = activity;
        this.climate = climate;
        this.dailyGoal = dailyGoal;
        this.currentGoal = currentGoal;
    }

    public UserInfo() {
        this.id = 0;
        this.name = "";
        this.email = "";
        this.username = "";
        this.password = "";
        this.gender = "";
        this.dob = "";
        this.height = 0;
        this.weight = 0;
        this.activity = "";
        this.climate = "";
        this.dailyGoal = 0;
        this.currentGoal = 0;
    }

    public void setInfo (int id, String name, String email, String username, String password, String gender,
                         String dob, String activity, String climate, int height, int weight, int dailyGoal, int currentGoal){
        this.id = id;
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.dob = dob;
        this.height = height;
        this.weight = weight;
        this.activity = activity;
        this.climate = climate;
        this.dailyGoal = dailyGoal;
        this.currentGoal = currentGoal;
    }

    public void setInfo (UserInfo userInfo){
        this.setInfo(userInfo.id, userInfo.name, userInfo.email, userInfo.username, userInfo.password,
                userInfo.gender, userInfo.dob, userInfo.activity, userInfo.climate, userInfo.height, userInfo.weight, userInfo.dailyGoal, userInfo.currentGoal);
    }
}
