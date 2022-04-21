package com.example.howsweather.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.howsweather.model.Daily
import com.example.howsweather.model.Forecast
import com.example.howsweather.network.Retro
import com.example.repository.Repository
import com.example.weatherforecast.model.Hourly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class HomeViewModel(context: Context) : ViewModel() {

    var hourlyList = MutableLiveData<List<Hourly>>()
    var dailyList = MutableLiveData<List<Daily>>()
    var forecast = MutableLiveData<Forecast>()
    var repository: Repository? = null
    var myForecast: Forecast? = null

    init {
        repository = Repository.getInstance(context)
    }

    fun getWeatherDataAndInsertInDatabase(lat: Double, long: Double, units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //get context for repo by the above method before
            val response = repository!!.getWeatherData(lat, long, units, lang)
            if (response.isSuccessful) {
                repository!!.deleteForecast()
                repository!!.insertForecast(response.body()!!)
            } else {
                throw Exception(response.errorBody().toString())
            }
        }
    }

    fun getWeatherFromLocal(homeFragment: HomeFragment) {
        viewModelScope.launch {
            val resp = repository?.getForecast()!!
            resp.observe(homeFragment, Observer {
                if (it != null) {
                    hourlyList.value = it.hourly
                    dailyList.value = it.daily
                    forecast.value = it
                }
            })
        }
    }
}