package com.example.weatherpulse.data

import android.service.notification.Condition

data class WeatherModel(
    val city: String,
    val time : String,
    var tempCurrent: String,
    val condition: String,
    val imageCondition: String,
    val maxTemp : String,
    val minTemp : String,
    val hours : String
)



