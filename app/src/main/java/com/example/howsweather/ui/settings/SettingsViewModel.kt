package com.example.howsweather.ui.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class SettingsViewModel(context: Context) : ViewModel() {

    lateinit var repository: Repository

    init {
        repository = Repository.getInstance(context)
    }

    fun updateDatabase( units: String, lang: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var databaseList = repository.getAllData()
            withContext(Dispatchers.Main) {
                for (item in databaseList) {
                    if(item.lat!=0.0){
                        var response = repository.getWeatherData(item.lat,item.lon, units, lang)
                        if (response.isSuccessful) {
                            repository.insertForecast(response.body()!!)
                        }
                    }

                }
            }
        }
    }



}