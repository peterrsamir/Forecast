package com.example.howsweather.network

import com.example.howsweather.model.Forecast
import com.example.howsweather.ui.home.HomeFragment
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retro {

    lateinit var retrofitInterface: RetrofitInterface
    lateinit var retrofit: Retrofit

    private val BASE_URL = "https://api.openweathermap.org/"

    init {
        retrofit =
            Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create())
                .build()
        retrofitInterface = retrofit.create(RetrofitInterface::class.java)
    }

    fun getRetrofitInstance(): RetrofitInterface {
        return retrofitInterface
//        return runBlocking { retrofitInterface.getAllWeather(lat, long, "metric") }
    }
}