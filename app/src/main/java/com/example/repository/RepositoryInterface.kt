package com.example.repository

import androidx.lifecycle.LiveData
import com.example.howsweather.model.CustomAlert
import com.example.howsweather.model.Forecast
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface RepositoryInterface {

    suspend fun getWeatherData(lat:Double, long:Double, units:String, lang:String): Response<Forecast>

    suspend fun deleteForecast()
    suspend fun insertForecast(forecast: Forecast)
    suspend fun getForecast(): LiveData<Forecast>
    suspend fun deleteFavoriteForecast(id:Int)
    suspend fun getFavorite(): Flow<List<Forecast>>
    suspend fun getFavById(id:Int):LiveData<Forecast>
    suspend fun getAllData():List<Forecast>
    suspend fun updateFavByID(forecast: Forecast)


    suspend fun insertCustomAlert(customAlert: CustomAlert):Long
    suspend fun getCustomAlert():Flow<List<CustomAlert>>
    suspend fun deleteCustomAlert(customAlert: CustomAlert)
    suspend fun getCustomAlertByID(id:Int):CustomAlert

    suspend fun getCustomForecast(): Forecast

}