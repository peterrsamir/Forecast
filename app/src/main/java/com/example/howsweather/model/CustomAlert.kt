package com.example.howsweather.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "CustomAlert")
data class CustomAlert(
    @PrimaryKey(autoGenerate = true) @NonNull val id: Int? = null,
    var startDate:Long, var startTime:Long, var endDate: Long, var endTime:Long
)