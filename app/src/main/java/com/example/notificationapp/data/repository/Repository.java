package com.example.notificationapp.data.repository;

import android.util.Log;

import com.example.notificationapp.data.network.api.RetrofitAccessObject;
import com.example.notificationapp.data.network.model.UserModel;
import com.example.notificationapp.data.network.model.UserResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {

    public UserResponse saveUser(String auth, UserModel userModel) {
        UserResponse userResponse = new UserResponse();
        RetrofitAccessObject.getRetrofitAccessObject().saveUser(auth, userModel).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                Log.i("RequestBody", call.request().url().toString() + " " + response.toString() + "\n" + userModel.toString());
                if (response.code() == 200) {
                    try {
                        if (response.body() == null) throw new Exception("Unqualified response");
                        if (response.isSuccessful() && response.body()!=null ) {
//                            userResponse = response.body();
                        }
                    } catch (Exception exception) {


                    }
                } else {

                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
            }
        });
return null;
    }
}
