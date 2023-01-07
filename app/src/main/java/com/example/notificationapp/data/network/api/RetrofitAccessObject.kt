package com.example.notificationapp.data.network.api

import com.example.notificationapp.utils.Constants
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAccessObject {

    private var mApi: Api? = null

    fun getRetrofitAccessObject(): Api {
        if (mApi == null) {
            val gson = GsonBuilder().setLenient().create()
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            mApi = retrofit.create(Api::class.java)
        }
        return mApi!!
    }
}