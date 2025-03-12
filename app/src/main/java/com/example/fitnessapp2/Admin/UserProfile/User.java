package com.example.fitnessapp2.Admin.UserProfile;

import com.google.firebase.firestore.PropertyName;

public class User {
    private String uId;
    private String name;
    private String gender;
    private String age;
    @PropertyName("body_weight") private String bodyWeight;
    private String height;
    @PropertyName("diet_preference") private String dietPreference;

    public User() {}

    public User(String uId, String name, String gender, String age, String bodyWeight, String height, String dietPreference, String imageUri, String role) {
        this.uId = uId;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.bodyWeight = bodyWeight;
        this.height = height;
        this.dietPreference = dietPreference;
    }

    public String getuId() { return uId; }
    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getAge() { return age; }
    @PropertyName("body_weight")
    public String getBodyWeight() { return bodyWeight; }

    public String getHeight() { return height; }
    @PropertyName("diet_preference")
    public String getDietPreference() { return dietPreference; }

    public void setuId(String uId) { this.uId = uId; }
    public void setName(String name) { this.name = name; }
    public void setAge(String age) { this.age = age; }
    public void setGender(String gender) { this.gender = gender; }
    @PropertyName("body_weight")
    public void setBodyWeight(String bodyWeight) { this.bodyWeight = bodyWeight; }
    public void setHeight(String height) { this.height = height; }
    @PropertyName("diet_preference")
    public void setDietPreference(String dietPreference) { this.dietPreference = dietPreference; }
}
