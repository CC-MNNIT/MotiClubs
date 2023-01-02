package com.example.notificationapp;

import com.example.notificationapp.modules.ClubModel;
import com.example.notificationapp.modules.UserModel;
import com.example.notificationapp.modules.UserModelWithAdmin;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
        //    To load diffrent clubs
        @GET("/clubs")
        Call<List<ClubModel>> getClubs();

        //    To get details of current user
        @GET
        Call<UserModelWithAdmin> getUser();

        //    Adding user at time of registration
        @POST
        Call<UserModel> addUser();

}
