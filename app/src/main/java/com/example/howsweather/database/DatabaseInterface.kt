package com.example.howsweather.database

import androidx.lifecycle.LiveData
import com.example.howsweather.model.CustomAlert
import com.example.howsweather.model.Forecast
import kotlinx.coroutines.flow.Flow

interface DatabaseInterface {
    suspend fun insertForecast(forecast: Forecast)
    suspend fun getForecast():LiveData<Forecast>
    suspend fun deleteFavoriteForecast(id:Int)
    suspend fun deleteForecast()
    suspend fun getFavorite(): Flow<List<Forecast>>
    suspend fun getAllData():List<Forecast>
    suspend fun getFavById(id:Int):LiveData<Forecast>
    suspend fun updateFavByID(forecast: Forecast)


    //custom alert Queries
    suspend fun insertCustomAlert(customAlert: CustomAlert):Long
    suspend fun getCustomAlert():Flow<List<CustomAlert>>
    suspend fun deleteCustomAlert(customAlert: CustomAlert)
    suspend fun getCustomAlertByID(id:Int):CustomAlert

    suspend fun getCustomForecast():Forecast
}
