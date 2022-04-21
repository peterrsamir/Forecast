package com.example.howsweather.ui.alerts

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.howsweather.model.CustomAlert
import com.example.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AlertsViewModel(context: Context) : ViewModel() {

     var repository:Repository = Repository.getInstance(context)
    var customList = MutableLiveData<List<CustomAlert>>()

    fun getAllAlerts() {
        viewModelScope.launch(Dispatchers.IO) {
            val customList = repository.getCustomAlert().collect {
                customList.postValue(it)
            }
        }
    }

    fun deleteCustomAlert(customAlert: CustomAlert){
        viewModelScope.launch (Dispatchers.IO){
            repository.deleteCustomAlert(customAlert)
        }
    }
}