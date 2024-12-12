package com.example.sunnyweather.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.model.Place
import retrofit2.http.Query

class PlaceViewModel:ViewModel() {
    private val searchLiveData=MutableLiveData<String>()

    val placeList=ArrayList<Place>()

    val placeLiveData=searchLiveData.switchMap { query->
        Repository.searchPlaces(query)
    }
    
    fun searchPlaces(query: String){
        searchLiveData.value=query
    }

    fun savePlace(place: Place)=Repository.savePlace(place)

    fun getSavedPlace()=Repository.getSavedPlace()

    fun isPlaceSaved()=Repository.isPlaceSaved()
}