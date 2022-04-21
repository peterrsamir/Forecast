package com.example.howsweather.ui.favorite

import android.view.View
import com.example.howsweather.model.Forecast

interface OnHolderClicked {
    fun showFav(view: View, forecast: Forecast)
}