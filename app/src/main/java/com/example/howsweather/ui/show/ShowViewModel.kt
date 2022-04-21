package com.example.howsweather.ui.show

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.howsweather.model.Daily
import com.example.howsweather.model.Forecast
import com.example.repository.Repository
import com.example.weatherforecast.model.Hourly
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class ShowViewModel(val context: Context): ViewModel() {
    var hourlyList = MutableLiveData<List<Hourly>>()
    var dailyList = MutableLiveData<List<Daily>>()
    var forecast = MutableLiveData<Forecast>()
    var repository: Repository? = null
    lateinit var sharedPreferences: SharedPreferences

    init {
        repository = Repository.getInstance(context)
    }

    fun getFavById(id:Int, show: Show){
        viewModelScope.launch (Dispatchers.Main){
            val response = repository?.getFavById(id)
            response?.observe(show, Observer {
                if(it !=null){
                    hourlyList.value = it.hourly
                    dailyList.value = it.daily
                    forecast.value = it
                }
            })
        }
    }

    fun getWeatherDataAndInsertInDatabase(lat: Double, long: Double, units:String, lang:String) {
        viewModelScope.launch(Dispatchers.IO) {
            //get context for repo by the above method before
            val response = repository!!.getWeatherData(lat, long, units, lang)
            if (response.isSuccessful) {
               val result = response.body()
                sharedPreferences = context.getSharedPreferences("idFavPRefs", Context.MODE_PRIVATE)
                result?.isFavorite = 1
                result?.id = sharedPreferences.getInt("id", 1)
                repository?.insertForecast(result!!)
            }else{
                throw Exception("Network Failed")
            }
        }
    }

    fun getWeatherFromRetroAndInsertFavoriteDb(id:Int, lat: Double, long: Double, units:String, lang:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository!!.getWeatherData(lat, long, units, lang)
            if (response.isSuccessful) {
                response.body()?.isFavorite = 1
                response.body()?.id = id
                repository?.insertForecast(response.body()!!)
            }
        }
    }
}