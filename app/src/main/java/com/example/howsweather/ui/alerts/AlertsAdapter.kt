package com.example.howsweather.ui.alerts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.howsweather.R
import com.example.howsweather.model.CustomAlert
import com.example.howsweather.model.Forecast
import com.example.howsweather.utils.Helper

class AlertsAdapter(
    val context: Context,
    var list: List<CustomAlert>,
    var onDeleteClicked: OnDeleteClicked
) : RecyclerView.Adapter<AlertsAdapter.AlertsHolder>() {


    val SETTINGS_SHARED_PREFS = "settings"
    val LANG = "language"
    var myPreferences = context.getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
    lateinit var lang:String

    class AlertsHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val from: TextView = itemView.findViewById(R.id.txtVFrom)
        val to: TextView = itemView.findViewById(R.id.txtVTo)
        val imgDelete: ImageView = itemView.findViewById(R.id.imgDeleteAlert)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertsHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.alert_custom_row, null)
        return AlertsHolder(view)
    }

    override fun onBindViewHolder(holder: AlertsHolder, position: Int) {
        lang = myPreferences.getString(LANG, "en").toString()

        holder.imgDelete.setOnClickListener(View.OnClickListener {
            onDeleteClicked.onDeleteClicked(list[position])
        })
        var startDate = Helper.convertCalenderToDayDate(list[position].startDate, lang)
        var starTime = Helper.convertLongToTime(list[position].startTime, lang)
        var endDate = Helper.convertCalenderToDayDate(list[position].startDate, lang)
        var endTime = Helper.convertLongToTime(list[position].startTime, lang)

        holder.from.text = "Start Date: $startDate $starTime"
        holder.to.text = "End Date: $endDate $endTime"
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setAlertList(alertList: List<CustomAlert>) {
        this.list = alertList
    }
}