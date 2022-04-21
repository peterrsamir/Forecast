package com.example.howsweather.ui.alerts

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.howsweather.model.CustomAlert
import com.example.howsweather.model.Forecast
import com.example.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class CustomDialogViewModel (val context: Context) : ViewModel() {

    var repository: Repository
    var result: Long? = null
    private var myID : MutableLiveData<Long> = MutableLiveData()
    var editableID = myID


    init {
        repository = Repository.getInstance(context)
    }

    fun insertAlert(customAlert: CustomAlert){
        viewModelScope.launch(Dispatchers.IO) {
            var job = viewModelScope.launch(Dispatchers.IO){
                result = repository.insertCustomAlert(customAlert)
            }
            job.join()
            if(result!= null){
                myID.postValue(result)
            }
        }
    }

    fun getMyWeather(){
        viewModelScope.launch {
            repository.getForecast()
        }
    }

    /*fun deleteCustomAlert(customAlert: CustomAlert){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCustomAlert(customAlert)
        }
    }*/
}