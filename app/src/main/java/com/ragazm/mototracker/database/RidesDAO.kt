package com.ragazm.mototracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 *Created by Andris on 16-Jul-19
 */

@Dao
interface RidesDAO {
    //LifeData query
    @Query("SELECT * from rides_table ORDER BY id ASC")
    fun getAllRides(): LiveData<List<DatabaseRideModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(databaseRideModel : DatabaseRideModel)

    @Query("DELETE FROM rides_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM rides_table  WHERE id = :id")
    fun getRide(id: Int) : DatabaseRideModel





}