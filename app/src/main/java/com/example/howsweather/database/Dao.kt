package com.example.howsweather.database

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Query
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.example.howsweather.model.*
import com.example.weatherforecast.model.Hourly
import kotlinx.coroutines.flow.Flow


@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: Forecast)

    @Query("Delete from forecast where isFavorite = 0")
    suspend fun deleteForecast()

    @Query("Select * FROM Forecast where isFavorite = 0")
    fun getForecast():LiveData<Forecast>

    @Query("Select * FROM Forecast where isFavorite = 1")
    fun getFavorite(): Flow<List<Forecast>>

    @Query("Delete from Forecast where id=:id")
    fun deleteFavoriteForecast(id:Int)

    @Query("select * from forecast where id=:id")
    fun getFavById(id:Int):LiveData<Forecast>

    @Query("SELECT * FROM Forecast")
    fun getAllData(): List<Forecast>

    @Update
    fun updateFavByID(forecast: Forecast)

    // custom alert queries//////////////
    @Insert
    fun insertCustomAlert(customAlert: CustomAlert):Long

    @Delete
    fun deleteCustomAlert(customAlert: CustomAlert)

    @Query("select * from CustomAlert")
    fun getCustomAlert():Flow<List<CustomAlert>>

    @Query("select * from CustomAlert where id=:id")
    fun getCustomAlertByID (id:Int):CustomAlert

    @Query("Select * FROM Forecast where isFavorite = 0")
    fun getCustomForecast():Forecast
}