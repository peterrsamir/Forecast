package com.example.howsweather.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.weatherforecast.model.Hourly
import com.example.weatherforecast.model.Minutely

@Entity(tableName = "Forecast")
data class Forecast(
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    var id:Int,
    var isFavorite: Int,
    var lat: Double,
    var lon: Double,
    var timezone: String,
    var timezone_offset: Int,
    var current: Current,
    var hourly: List<Hourly>,
    var daily: List<Daily>,
    var alerts: List<Alerts>?
) {

    /*constructor(

    ) {
       *//* this.id = id
        this.isFavorite = isFavorite
        this.lat = lat
        this.lon = lon
        this.timezone = timezone
        this.timezone_offset = timezone_offset
        this.current = current
        this.minutely = minutely
        this.hourly = hourly
        this.daily = daily
        this.alerts = alerts*//*
    }*/

//    constructor()


    /*var isFavorite: Int = 0
        get() = field
    fun setIsFavorite(value: Int) {
        isFavorite = value
    }

    var lat: Double = 0.0
        get() = field


    var lon: Double = 0.0
        get() = field


    var timezone: String = ""
        get() = field

    var timezone_offset: Int = 0
        get() = field

    var current: Current = Current()
        get() = field

    var minutely: List<Minutely> = emptyList()
        get() = field

    var hourly: List<Hourly> = emptyList()
        get() = field

    var daily: List<Daily> = emptyList()
        get() = field

    var alerts: List<Alerts> = emptyList()
        get() = field*/

}