package com.ragazm.mototracker.database

import androidx.lifecycle.LiveData

/**
 *Created by Andris on 09-Aug-19
 */

class RideRepository(private val ridesDAO: RidesDAO){

    val allRides: LiveData<List<DatabaseRideModel>> = ridesDAO.getAllRides()


    suspend fun insert(databaseRideModel: DatabaseRideModel){
        ridesDAO.insert(databaseRideModel)
    }

   // val ride : DatabaseRideModel = ridesDAO.getRide(id)
      fun getRide(id: Int): DatabaseRideModel {
      val rride: DatabaseRideModel = ridesDAO.getRide(id)
       return rride
   }




}