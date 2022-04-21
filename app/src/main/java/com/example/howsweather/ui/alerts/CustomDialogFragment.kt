package com.example.howsweather.ui.alerts

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TimePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.howsweather.R
import com.example.howsweather.databinding.FragmentDialogBinding
import com.example.howsweather.manager.AlertPeriodicManager
import com.example.howsweather.model.CustomAlert
import com.example.howsweather.utils.Helper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

class CustomDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentDialogBinding
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var customAlert: CustomAlert
    val SETTINGS_SHARED_PREFS = "settings"
    val LANG = "language"
    lateinit var lang: String
    lateinit var alertPeriodicManager: AlertPeriodicManager
    lateinit var customDialogViewModel: CustomDialogViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var customDialogFactory = CustomDialogFactory(requireContext())
        customDialogViewModel =
            ViewModelProvider(this, customDialogFactory).get(CustomDialogViewModel::class.java)
        binding = FragmentDialogBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawableResource(R.drawable.notification_card)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences =
            requireActivity().getSharedPreferences(SETTINGS_SHARED_PREFS, Context.MODE_PRIVATE)
        lang = sharedPreferences.getString(LANG, "en")!!
        val calendar = Calendar.getInstance().timeInMillis
        setCardInitialization(calendar, lang!!)

        binding.btnFrom.setOnClickListener(View.OnClickListener {
            showDatePicker(true)
        })
        binding.btnTo.setOnClickListener(View.OnClickListener {
            showDatePicker(false)
        })

        binding.btnSave.setOnClickListener(View.OnClickListener {
            customDialogViewModel.insertAlert(customAlert)
            customDialogViewModel.editableID.observe(viewLifecycleOwner) {
                setPeriodic(it.toInt())
                dialog?.dismiss()

            }
        })
    }


    fun setPeriodic(id: Int) {
        var data = Data.Builder()
        data.putInt("id", id)
        var constraint = Constraints.Builder().setRequiresBatteryNotLow(true).build()
        val periodicWorkRequest =
            PeriodicWorkRequest.Builder(AlertPeriodicManager::class.java, 24, TimeUnit.HOURS)
                .setInputData(data.build())
                .setConstraints(constraint).build()
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "${id}",
            ExistingPeriodicWorkPolicy.REPLACE,
            periodicWorkRequest
        )
    }

    private fun setCardInitialization(calendar: Long, lang: String) {
        val timeNow = Helper.convertLongToTime(calendar / 1000L, lang)
        val currentDate = Helper.convertCalenderToDayDate(calendar, lang)
        val oneHour: Long = 60 * 60 * 60
        val afterOneHour = calendar + oneHour
        val timeAfterOneHour = Helper.convertLongToTime(afterOneHour / 1000L, lang)
        binding.btnFrom.text = currentDate.plus("\n").plus(timeNow)
        binding.btnTo.text = currentDate.plus("\n").plus(timeAfterOneHour)
        customAlert = CustomAlert(null, calendar / 1000, afterOneHour / 1000, calendar, calendar)
    }

    private fun showTimePicker(isFrom: Boolean, datePicker: Long) {
        Locale.setDefault(Locale(lang))
        val currentHour = Calendar.HOUR_OF_DAY
        val currentMinute = Calendar.MINUTE
        val listener: (TimePicker?, Int, Int) -> Unit =
            { _: TimePicker?, hour: Int, minute: Int ->
                val time = TimeUnit.MINUTES.toSeconds(minute.toLong()) +
                        TimeUnit.HOURS.toSeconds(hour.toLong()) - (3600L * 2)
                val dateString = Helper.convertCalenderToDayDate(datePicker, lang)
                val timeString = Helper.convertLongToTime(time, lang)
                val text = dateString.plus("\n").plus(timeString)
                if (isFrom) {
                    binding.btnFrom.text = text
                    customAlert.startTime = time
                } else {
                    binding.btnTo.text = text
                    customAlert.endTime = time
                }
            }

        val timePickerDialog = TimePickerDialog(
            requireContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            listener, currentHour, currentMinute, false
        )

        timePickerDialog.setTitle("Time")
        timePickerDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        timePickerDialog.show()
    }

    private fun getDateMillis(date: String): Long {
        val f = SimpleDateFormat("dd/MM/yyyy", Locale(lang))
        val d: Date = f.parse(date)
        return d.time
    }

    private fun showDatePicker(isFrom: Boolean) {
        Locale.setDefault(Locale(lang))
        val myCalender = Calendar.getInstance()
        val year = myCalender[Calendar.YEAR]
        val month = myCalender[Calendar.MONTH]
        val day = myCalender[Calendar.DAY_OF_MONTH]
        val myDateListener =
            DatePickerDialog.OnDateSetListener { view, year, month, day ->

                if (view.isShown) {
                    val date = "$day/${month + 1}/$year"
                    if (isFrom) {
                        customAlert.startDate = getDateMillis(date)
                    } else {
                        customAlert.endDate = getDateMillis(date)
                    }
                    showTimePicker(isFrom, getDateMillis(date))
                }
            }
        val datePickerDialog = DatePickerDialog(
            requireContext(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar,
            myDateListener, year, month, day
        )
        datePickerDialog.setTitle("Date")
        datePickerDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        datePickerDialog.show()
    }

}