package com.ragazm.mototracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *Created by Andris on 13-Jul-19
 */

@Entity(tableName = "rides_table")
data class DatabaseRideModel (
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "name") var name: String,
                //json
    @ColumnInfo(name = "start_date") var startDate: String,
    @ColumnInfo(name = "finish_date") var finishDate: String,
                //distance in metres
    @ColumnInfo(name = "distance") var distance: Int,
                //duration in seconds
    @ColumnInfo(name= "duration") var duration: Int,
                //json with route coordinates
    @ColumnInfo(name = "route") var route: String,
    @ColumnInfo(name = "comment") var comment: String
)