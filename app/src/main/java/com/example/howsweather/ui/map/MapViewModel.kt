package com.example.howsweather.ui.map

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class MapViewModel(context: Context) : ViewModel() {

    var repository: Repository? = null

    init {
        repository = Repository.getInstance(context)
    }


    fun getWeatherFromRetroAndInsertDb(lat: Double, long: Double, units:String, lang:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository!!.getWeatherData(lat, long, units, lang)
//            withContext(Dispatchers.IO) {
                if (response.isSuccessful) {
                    repository?.deleteForecast()
                    repository?.insertForecast(response.body()!!)
                }else{
                    throw Exception("Error")
                }
//                repository?.insertForecast(response!!)
//            }
        }
    }

    fun getWeatherFromRetroAndInsertFavoriteDb(lat: Double, long: Double, units:String, lang:String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repository!!.getWeatherData(lat, long, units, lang)
                if (response.isSuccessful) {
                    response.body()?.isFavorite = 1
                    repository?.insertForecast(response.body()!!)
                }
        }
    }
}