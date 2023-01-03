package com.example.notificationapp.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.notificationapp.R;
import com.example.notificationapp.data.network.api.RetrofitAccessObject;
import com.example.notificationapp.data.network.model.ClubModel;
import com.example.notificationapp.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private List<ClubModel> clubs = new ArrayList<>();


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    private void getClubsList() {
        SharedPreferences preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        RetrofitAccessObject.getRetrofitAccessObject()
                .getClubs(preferences.getString(Constants.TOKEN, ""))
                .enqueue(new Callback<List<ClubModel>>() {
                    @Override
                    public void onResponse(Call<List<ClubModel>> call, Response<List<ClubModel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            clubs = response.body();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<ClubModel>> call, Throwable t) {

                    }
                });
    }
}