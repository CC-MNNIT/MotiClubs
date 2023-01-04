package com.example.notificationapp.data.network.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserResponse {
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("registrationNumber")
    @Expose
    private String registrationNumber;
    @SerializedName("graduationYear")
    @Expose
    private String graduationYear;
    @SerializedName("course")
    @Expose
    private String course;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("personalEmail")
    @Expose
    private String personalEmail;

    @SerializedName("avatar")
    @Expose
    private String avatar;

    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;
    @SerializedName("admin")
    @Expose
    private List<String> admin = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(String graduationYear) {
        this.graduationYear = graduationYear;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPersonalEmail() {
        return personalEmail;
    }

    public void setPersonalEmail(String personalEmail) {
        this.personalEmail = personalEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<String> getAdmin() {
        return admin;
    }

    public void setAdmin(List<String> admin) {
        this.admin = admin;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String email) {
        this.avatar = avatar;
    }
}