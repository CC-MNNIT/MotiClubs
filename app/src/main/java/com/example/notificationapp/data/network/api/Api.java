package com.example.notificationapp.data.network.api;

import com.example.notificationapp.data.network.model.UserModel;
import com.example.notificationapp.data.network.model.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface Api {
    @POST("/user")
    Call<UserResponse> saveUser(@Header("Authorization") String auth, @Body UserModel userModel);

}
