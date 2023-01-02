package com.example.notificationapp.modules;

public class UserModelWithAdmin {
    String name, registrationNumber, email, course, personalEmail, phoneNumber, admin;
    int graduationYear;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
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

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public int getGraduationYear() {
        return graduationYear;
    }

    public void setGraduationYear(int graduationYear) {
        this.graduationYear = graduationYear;
    }

    public UserModelWithAdmin(String name, String registrationNumber, String email, String course, String personalEmail, String phoneNumber, String admin, int graduationYear) {
        this.name = name;
        this.registrationNumber = registrationNumber;
        this.email = email;
        this.course = course;
        this.personalEmail = personalEmail;
        this.phoneNumber = phoneNumber;
        this.admin = admin;
        this.graduationYear = graduationYear;
    }
}
