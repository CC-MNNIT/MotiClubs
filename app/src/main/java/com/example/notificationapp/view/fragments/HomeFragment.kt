package com.example.notificationapp.view.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notificationapp.data.adapters.ClubListAdapter
import com.example.notificationapp.data.network.ClubModel
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import com.example.notificationapp.databinding.FragmentHomeBinding
import com.example.notificationapp.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var clubs: List<ClubModel> = ArrayList()
    private lateinit var clubListAdapter: ClubListAdapter

    private lateinit var _binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = _binding.root
        _binding.clubRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
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
                        _binding.clubRecycler.adapter = clubListAdapter
                        Log.d("Hello1", clubs.toTypedArray().toString())
                    }
                }

                override fun onFailure(call: Call<List<ClubModel>?>, t: Throwable) {}
            })
    }
}