package com.example.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.howsweather.database.LocalDatabase
import com.example.howsweather.model.CustomAlert
import com.example.howsweather.model.Forecast
import com.example.howsweather.network.Retro
import com.google.android.gms.common.api.Response
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

class Repository private constructor(context: Context) : RepositoryInterface {


    private var localDatabase :LocalDatabase ?= null
    var retrofit = Retro

    init {
        localDatabase = LocalDatabase.getDatabaseInstance(context)
    }

    companion object{
        private var repository:Repository? = null
        public fun getInstance(context: Context):Repository{
            if (repository == null){
                repository = Repository(context)
            }
            return repository!!
        }
    }

    override suspend fun getWeatherData(lat: Double, long: Double, units:String, lang:String): retrofit2.Response<Forecast> {
        return retrofit.getRetrofitInstance().getAllWeather(lat =lat,lon= long,units = units, lang= lang)
    }

    override suspend fun deleteForecast() {
        localDatabase?.deleteForecast()
    }

    override suspend fun insertForecast(forecast: Forecast) {
        localDatabase?.insertForecast(forecast)
    }

    override suspend fun getForecast(): LiveData<Forecast> {
        return localDatabase?.getForecast()!!
    }

    override suspend fun deleteFavoriteForecast(id:Int) {
        localDatabase?.deleteFavoriteForecast(id)
    }

    override suspend fun getFavorite(): Flow<List<Forecast>> {
        return localDatabase?.getFavorite()!!
    }

    override suspend fun getFavById(id: Int): LiveData<Forecast> {
        return localDatabase?.getFavById(id)!!
    }

    override suspend fun getAllData(): List<Forecast> {
        return localDatabase?.getAllData()!!
    }

    override suspend fun updateFavByID(forecast: Forecast) {
        localDatabase?.updateFavByID(forecast)
    }




    override suspend fun insertCustomAlert(customAlert: CustomAlert):Long {
       return  localDatabase?.insertCustomAlert(customAlert)!!
    }

    override suspend fun getCustomAlert(): Flow<List<CustomAlert>> {
        return localDatabase?.getCustomAlert()!!
    }

    override suspend fun deleteCustomAlert(customAlert: CustomAlert) {
        localDatabase?.deleteCustomAlert(customAlert)
    }

    override suspend fun getCustomAlertByID(id: Int) :CustomAlert{
        return localDatabase?.getCustomAlertByID(id)!!
    }

    override suspend fun getCustomForecast(): Forecast {
        return localDatabase?.getCustomForecast()!!
    }
}