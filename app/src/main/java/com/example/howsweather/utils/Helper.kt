package com.example.howsweather.utils

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.TimeUnit

class Helper {

    companion object{

        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            return activeNetworkInfo != null && activeNetworkInfo?.isConnected
        }

         fun timeFormat(millisSeconds:Int, lang:String ): String {
            val calendar = Calendar.getInstance()
            calendar.setTimeInMillis((millisSeconds * 1000).toLong())
            val format = SimpleDateFormat("h aaa", Locale(lang))
            return format.format(calendar.time)
        }

        fun getCityName(context: Context, lat: Double, lon: Double, lang:String): String {
            var city = ""
            val geocoder = Geocoder(context, Locale(lang))
            val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 1)
            if (addresses.isNotEmpty()) {
                val state = addresses[0].adminArea // damietta
                val country = addresses[0].countryName
                city = "$state, $country"
            }
            return city
        }

        fun ArabicToEnglish(str: String, lang:String):String {
            if(lang.equals("ar")) {
                var result = ""
                var en = '0'
                for (ch in str) {
                    en = ch
                    when (ch) {
                        '0' -> en = '۰'
                        '1' -> en = '۱'
                        '2' -> en = '۲'
                        '3' -> en = '۳'
                        '4' -> en = '٤'
                        '5' -> en = '۵'
                        '6' -> en = '٦'
                        '7' -> en = '۷'
                        '8' -> en = '۸'
                        '9' -> en = '۹'
                    }
                    result = "${result}$en"
                }
                return result
            }else{
                return str
            }
        }

        // return now day history
        fun convertToDate(dt: Long, lang: String): String {
            val date = Date(dt * 1000)
            val format = SimpleDateFormat("d MMM, yyyy", Locale(lang))
            return format.format(date)
        }


        fun convertCalenderToDayDate(time: Long, language: String): String {
            val date = Date(time)
            val format = SimpleDateFormat("d MMM, yyyy", Locale(language))
            return format.format(date)
        }


        fun convertLongToTime(time: Long, language: String): String {
            val date = Date(TimeUnit.SECONDS.toMillis(time))
            val format = SimpleDateFormat("h:mm a", Locale(language))
            return format.format(date)
        }
    }

    /*fun convertCalenderToDayDate(time: Long, language: String): String {
        val date = Date(time)
        val format = SimpleDateFormat("d MMM, yyyy", Locale(language))
        return format.format(date)
    }*/





}