package com.example.notificationapp.view.fragments

import com.example.notificationapp.data.network.api.RetrofitAccessObject
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.example.notificationapp.R
import android.os.Bundle
import android.view.LayoutInflater
import com.example.notificationapp.data.adapters.ClubListAdapter
import android.annotation.SuppressLint
import androidx.recyclerview.widget.GridLayoutManager
import android.content.Context
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.notificationapp.data.network.ClubModel
import com.example.notificationapp.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class HomeFragment : Fragment() {

    private var clubs: List<ClubModel> = ArrayList()
    private lateinit var clubRecyclerView: RecyclerView
    private lateinit var clubListAdapter: ClubListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        clubRecyclerView = root.findViewById(R.id.clubRecycler)
        clubRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        //clubRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        getClubsList()
        return root
    }

    private fun getClubsList() {
        val preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        RetrofitAccessObject.getRetrofitAccessObject()
            .getClubs(preferences.getString(Constants.TOKEN, ""))
            .enqueue(object : Callback<List<ClubModel>?> {
                override fun onResponse(call: Call<List<ClubModel>?>, response: Response<List<ClubModel>?>) {
                    if (response.isSuccessful && response.body() != null) {
                        clubs = response.body()!!
                        clubListAdapter = ClubListAdapter(clubs, requireContext())
                        clubRecyclerView.adapter = clubListAdapter
                        Log.d("Hello1", clubs.toTypedArray().toString())
                    }
                }

                override fun onFailure(call: Call<List<ClubModel>?>, t: Throwable) {}
            })
    }
}