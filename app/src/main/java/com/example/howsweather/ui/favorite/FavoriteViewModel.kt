package com.example.howsweather.ui.favorite

import android.content.Context
import androidx.lifecycle.*
import com.example.howsweather.model.Forecast
import com.example.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel (context: Context): ViewModel() {

    var favoriteList = MutableLiveData<List<Forecast>>()
    var repository :Repository ?= null

    init {
        repository = Repository.getInstance(context)
    }

    fun getFavorite(favoriteFragment: FavoriteFragment){
        viewModelScope.launch(Dispatchers.IO) {
            var favorite = repository?.getFavorite()?.collect {
                favoriteList.postValue(it)
            }
        }
    }

    fun deleteFavorite(id:Int){
        viewModelScope.launch (Dispatchers.IO){
            repository?.deleteFavoriteForecast(id)
        }
    }


}