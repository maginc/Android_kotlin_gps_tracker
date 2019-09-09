package com.ragazm.mototracker.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 *Created by Andris on 04-Jul-19
 */

class RideViewModel(application: Application): AndroidViewModel(application){
    private val repository: RideRepository
    val allRides: LiveData<List<DatabaseRideModel>>

    init {
        val ridesDAO = RideDatabase.getDatabase(application, viewModelScope).ridesDao()
        repository = RideRepository(ridesDAO)
        allRides = repository.allRides


    }

    fun insert(databaseRideModel: DatabaseRideModel) = viewModelScope.launch(Dispatchers.IO ) {
    repository.insert(databaseRideModel)
}
    fun getRide(id: Int) = viewModelScope.launch ( Dispatchers.IO ){

      repository.getRide(id)

    }


}