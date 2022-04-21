package com.example.howsweather.manager

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.work.*
import com.example.howsweather.model.CustomAlert
import com.example.repository.Repository
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlertPeriodicManager(
    val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    lateinit var myPreferences: SharedPreferences
    val SETTINGS_SHARED_PREFS = "settings"
    val LANG = "language"
    lateinit var lang: String
    var repository = Repository.getInstance(appContext)

    override suspend fun doWork(): Result {

        var inputData = inputData
        var myID = inputData.getInt("id", 0)
        var description = ""

        var forecast = repository.getCustomForecast()

            if(forecast.alerts.isNullOrEmpty()) {
                description = (forecast.current.weather[0].description)
            } else {
                description = (forecast.alerts!![0].event)
            }

        var customAlert = repository.getCustomAlertByID(myID)
        if (checkDate(customAlert)) {
            val delay = getPeriod(customAlert)
            setOneTime(myID, delay, description)
            Log.i("TAG", "doWork: "+ delay)
        }
        return Result.success()
    }

    fun checkDate(customAlert: CustomAlert): Boolean {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val date = "$day/${month + 1}/$year"
        val dayNow = getDateMillis(date)
        return (dayNow in customAlert.startDate..customAlert.endDate)
    }

    private fun getDateMillis(date: String): Long {
        myPreferences = appContext.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        lang = myPreferences.getString(LANG, "en")!!
        val f = SimpleDateFormat("dd/MM/yyyy", Locale(lang!!))
        val d: Date = f.parse(date)
        return d.time
    }

    fun setOneTime(id: Int, delay: Long, description: String) {
        val data = Data.Builder()
        data.putString("key", description.toString())
        val constraint = Constraints.Builder().setRequiresBatteryNotLow(true).build()
        val oneTimeRequest = OneTimeWorkRequest.Builder(OneTimeRequest::class.java)
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .setConstraints(constraint)
            .setInputData(data.build()).build()
        WorkManager.getInstance(appContext)
            .enqueueUniqueWork("$id", ExistingWorkPolicy.REPLACE, oneTimeRequest)
    }

    private fun getPeriod(customAlert: CustomAlert): Long {
        val hour = TimeUnit.HOURS.toSeconds(Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toLong())
        val minute =
            TimeUnit.MINUTES.toSeconds(Calendar.getInstance().get(Calendar.MINUTE).toLong())
        return customAlert.startTime - (hour + minute - (3600*2))
    }
}