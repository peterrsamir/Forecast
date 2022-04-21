package com.example.howsweather.network

import com.example.howsweather.model.Forecast
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitInterface {
    @GET("data/2.5/onecall?appid=44379a6ffaf4fd02206a072b5bffea52")
    suspend fun getAllWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String,
        @Query("lang") lang: String,
        @Query("exclude") exclude:String = "minutely"
    ): Response<Forecast>

}