package com.example.notificationapp.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.notificationapp.R
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.adapters.ClubListAdapter
import com.example.notificationapp.api.API
import com.example.notificationapp.api.ClubModel
import com.example.notificationapp.databinding.FragmentHomeBinding
import com.example.notificationapp.view.activities.MainActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.lang.Exception

class HomeFragment(private val mainActivity: MainActivity) : Fragment() {

    private var clubs: List<ClubModel> = ArrayList()
    private lateinit var clubListAdapter: ClubListAdapter

    private lateinit var _binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root = _binding.root
        _binding.clubRecycler.layoutManager = GridLayoutManager(requireContext(), 2)
        _binding.profileBtn.setOnClickListener { mainActivity.goToProfilePage() }

        getClubsList()
        setValues()
        return root
    }

    private fun getClubsList() {
        API.getClubs(UserInstance.getAuthToken(requireContext()), {
            clubs = it
            clubListAdapter = ClubListAdapter(clubs, requireContext())
            _binding.clubRecycler.adapter = clubListAdapter
        }) { Toast.makeText(requireContext(), "$it: Could not get clubs", Toast.LENGTH_SHORT).show() }
    }

    private fun setValues() {
        val avatar = UserInstance.getAvatar()
        if (avatar.isNotEmpty()) {
            Picasso.get().load(avatar).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.profile_icon)
                .into(_binding.profileIcon, object : Callback {
                    override fun onSuccess() {}
                    override fun onError(e: Exception?) {
                        Picasso.get().load(avatar).placeholder(R.drawable.profile_icon).into(_binding.profileIcon)
                    }
                })
        }
    }
}