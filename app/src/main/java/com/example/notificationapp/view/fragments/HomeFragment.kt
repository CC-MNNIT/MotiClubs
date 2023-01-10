package com.example.notificationapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.data.adapters.ClubListAdapter
import com.example.notificationapp.data.network.API
import com.example.notificationapp.data.network.ClubModel
import com.example.notificationapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var clubs: List<ClubModel> = ArrayList()
    private lateinit var clubListAdapter: ClubListAdapter

    private lateinit var _binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = _binding.root
        _binding.clubRecycler.layoutManager = GridLayoutManager(requireContext(), 2)

        getClubsList()
        return root
    }

    private fun getClubsList() {
        API.getClubs(UserInstance.getAuthToken(requireContext()), {
            clubs = it
            clubListAdapter = ClubListAdapter(clubs, requireContext())
            _binding.clubRecycler.adapter = clubListAdapter
        }) {}
    }
}