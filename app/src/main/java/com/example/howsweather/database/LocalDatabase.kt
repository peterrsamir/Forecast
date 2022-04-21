package com.example.howsweather.database

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.howsweather.model.CustomAlert
import com.example.howsweather.model.Forecast
import kotlinx.coroutines.flow.Flow

class LocalDatabase private constructor(context: Context) : DatabaseInterface{

    var dao:Dao? = null
    init {
        val databaseBuilder: DatabaseBuilder = DatabaseBuilder.getInstance(context)
        dao = databaseBuilder.getDao()!!
    }

    companion object {
        private var database: LocalDatabase? = null
        public fun getDatabaseInstance(context: Context) :LocalDatabase{
            if (database == null) {
               database = LocalDatabase(context)
            }
            return database!!
        }
    }

    override suspend fun deleteForecast() {
        dao?.deleteForecast()
    }

    override suspend fun insertForecast(forecast: Forecast) {
        dao?.insertForecast(forecast)
    }

    override suspend fun getForecast(): LiveData<Forecast> {
        return dao?.getForecast()!!
    }

    override suspend fun deleteFavoriteForecast(id:Int) {
        dao?.deleteFavoriteForecast(id)
    }

    override suspend fun getFavorite(): Flow<List<Forecast>> {
       return dao?.getFavorite()!!
    }

    override suspend fun getAllData(): List<Forecast> {
        return dao?.getAllData()!!
    }

    override suspend fun getFavById(id: Int): LiveData<Forecast> {
        return dao?.getFavById(id)!!
    }

    override suspend fun updateFavByID(forecast: Forecast) {
        dao?.updateFavByID(forecast)
    }


    //custom alert ////////
    override suspend fun insertCustomAlert(customAlert: CustomAlert):Long {
        return dao?.insertCustomAlert(customAlert)!!
    }

    override suspend fun getCustomAlert(): Flow<List<CustomAlert>> {
        return dao?.getCustomAlert()!!
    }

    override suspend fun deleteCustomAlert(customAlert: CustomAlert) {
        dao?.deleteCustomAlert(customAlert)
    }

    override suspend fun getCustomAlertByID(id: Int) :CustomAlert{
        return  dao?.getCustomAlertByID(id)!!
    }

    override suspend fun getCustomForecast(): Forecast {
        return dao?.getCustomForecast()!!
    }

}