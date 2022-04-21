package com.example.howsweather.ui.home

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.provider.CalendarContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.howsweather.R
import com.example.howsweather.model.Daily
import com.example.howsweather.utils.Helper
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

class DailyAdapter(var context: Context, var daysList: List<Daily>) :
    RecyclerView.Adapter<DailyAdapter.DailyHolder>() {
    val SETTINGS_SHARED_PREFS = "settings"
    val LANG = "language"
    var myPreferences = context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    lateinit var lang:String
    var tempUnit = "°c"
    var windSpeedUnit = "m/sec"


    class DailyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtVDays: TextView = itemView.findViewById(R.id.txtVDays)
        val txtVWeatherState: TextView = itemView.findViewById(R.id.txtVWeatherStatePerDay)
        val txtVDegree: TextView = itemView.findViewById(R.id.txtVDegreePerDay)
        val img: ImageView = itemView.findViewById(R.id.imgDaily)
        val relative :RelativeLayout = itemView.findViewById(R.id.relative)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.days_custom_row, null)
        return DailyHolder(view)
    }

    override fun onBindViewHolder(holder: DailyHolder, position: Int) {
        lang = myPreferences.getString(LANG, "en").toString()

        if(position ==0){
            holder.relative.setBackgroundResource(R.drawable.gradient)
            holder.txtVDegree.setTextColor(Color.WHITE)
            holder.txtVWeatherState.setTextColor(Color.WHITE)
        }
        holder.txtVDegree.setText(Helper.ArabicToEnglish(daysList[position].temp.day.toInt().toString() + "${tempUnit}", lang))
        holder.txtVWeatherState.setText(daysList[position].weather.get(0).description)
        var url =
            "http://openweathermap.org/img/wn/${daysList.get(position).weather.get(0).icon}@2x.png"
        Glide.with(context).load(url).placeholder(R.drawable.clouds).error(R.drawable.network_4_bar_24).into(holder.img)

        if (daysList.size>1) {

            val today = LocalDate.now().plus(position.toLong(), ChronoUnit.DAYS)
            var days = today.format(DateTimeFormatter.ofPattern("EEE", Locale(lang)))
            holder.txtVDays.text = days.toString()
        }
    }

/*    fun days(Day: String, context: Context): String {
        return when (Day.trim()) {
            "Saturday" -> context.getString(R.string.Saturday)
            "Sunday" -> context.getString(R.string.Sunday)
            "Monday" -> context.getString(R.string.Monday)
            "Tuesday" -> context.getString(R.string.Tuesday)
            "Wednesday" -> context.getString(R.string.Wednesday)
            "Friday" -> context.getString(R.string.Friday)
            "Thursday" -> context.getString(R.string.Thursday)
            else -> Day
        }
    }*/

    public fun setDailyList(list: List<Daily>) {
        this.daysList = list
    }

    override fun getItemCount(): Int {
        return daysList.size
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