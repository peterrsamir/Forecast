package com.example.howsweather.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.howsweather.R
import com.example.howsweather.utils.Helper
import com.example.weatherforecast.model.Hourly
import java.util.*

class HoursAdapter(var context: Context, var hourlylist: List<Hourly>) :
    RecyclerView.Adapter<HoursAdapter.MyHolder>() {

    val SETTINGS_SHARED_PREFS = "settings"
    val LANG = "language"
    var myPreferences = context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    lateinit var lang:String
    var tempUnit = "°c"
    var windSpeedUnit = "m/sec"

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtVHour: TextView = itemView.findViewById(R.id.txtVHour)
        val txtVTemp: TextView = itemView.findViewById(R.id.txtVDegreePerHour)
        val imgWeather: ImageView = itemView.findViewById(R.id.imgWeatherPerHours)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.hours_custom_row, null)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {

        lang = myPreferences.getString(LANG, "en").toString()

        var h = Helper.timeFormat(hourlylist.get(position).dt.toInt(), lang)

        holder.txtVHour.text = h.toString()
        holder.txtVTemp.text = Helper.ArabicToEnglish(hourlylist[position].temp.toInt().toString() + "${tempUnit}",lang)
        val url =
            "http://openweathermap.org/img/wn/${hourlylist.get(position).weather.get(0).icon}@2x.png"
        Glide.with(context).load(url).placeholder(R.drawable.clouds).error(R.drawable.network_4_bar_24).into(holder.imgWeather)
    }

    public fun setHourlyList(hourlyList: List<Hourly>) {
        this.hourlylist = hourlyList
    }

    override fun getItemCount(): Int {
        return hourlylist.size
    }

    fun setUnits(unit: String, lang: String) {
        if (lang.equals("en")) {
            when (unit) {
                "metric" -> {
                    tempUnit = "°c"
                    windSpeedUnit = "m/s"
                }
                "imperial" -> {
                    tempUnit = "°f"
                    windSpeedUnit = "m/h"
                }
                "standard" -> {
                    tempUnit = "°k"
                    windSpeedUnit = "m/s"
                }
            }
        } else {
            when (unit) {
                "metric" -> {
                    tempUnit = "°س"
                    windSpeedUnit = "متر/ث"
                }
                "imperial" -> {
                    tempUnit = "°ف"
                    windSpeedUnit = "ميل/ س"
                }
                "standard" -> {
                    tempUnit = "°ك"
                    windSpeedUnit = "متر/ س"
                }
            }
        }

    }

}