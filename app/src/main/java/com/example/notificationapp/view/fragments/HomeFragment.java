package com.example.notificationapp.view.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notificationapp.R;
import com.example.notificationapp.data.adapters.ClubListAdapter;
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
    private RecyclerView clubRecyclerView;
    private ClubListAdapter clubListAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        clubRecyclerView = root.findViewById(R.id.clubRecycler);
        clubRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        getClubsList();
        return root;
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
                            clubListAdapter = new ClubListAdapter(clubs, requireContext());
                            clubRecyclerView.setAdapter(clubListAdapter);
                            Log.d("Hello1",clubs.toArray().toString());
                        }
                    }
                    @Override
                    public void onFailure(Call<List<ClubModel>> call, Throwable t) {

                    }
                });
    }


}