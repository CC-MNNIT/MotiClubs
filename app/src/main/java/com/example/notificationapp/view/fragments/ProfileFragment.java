package com.example.notificationapp.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.notificationapp.R;
import com.example.notificationapp.data.network.api.RetrofitAccessObject;
import com.example.notificationapp.data.network.model.UserResponse;
import com.example.notificationapp.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {
    UserResponse user;
    TextView email_tv,year_tv,regno_tv,name_tv1,name_tv,course_tv,mobile_tv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        setRefrences(root);
        getUserData();
        setListeners();
        return root;
    }

    private void setListeners() {
    }

    private void getUserData() {
        SharedPreferences preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        RetrofitAccessObject.getRetrofitAccessObject().getUserData(preferences.getString(Constants.TOKEN, "")).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    user = response.body();
                    setProfileValues();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
            }
        });
    }

    private void setProfileValues() {
        if(user == null) return;
        email_tv.setText(user.getEmail());
        name_tv.setText(user.getName());
        name_tv1.setText(user.getName());
        mobile_tv.setText(user.getPhoneNumber());
        regno_tv.setText(user.getRegistrationNumber());
        year_tv.setText(user.getGraduationYear());
        course_tv.setText(user.getCourse());
    }

    private void setRefrences(View root) {
        email_tv = root.findViewById(R.id.email_tv);
        name_tv1 = root.findViewById(R.id.name_tv1);
        name_tv = root.findViewById(R.id.tv_name);
        mobile_tv = root.findViewById(R.id.mobile_tv);
        regno_tv = root.findViewById(R.id.tv_regNo);
        year_tv = root.findViewById(R.id.year_tv);
        course_tv = root.findViewById(R.id.course_tv);
    }
}