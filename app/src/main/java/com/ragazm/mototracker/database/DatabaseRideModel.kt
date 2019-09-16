package com.ragazm.mototracker.database

import android.text.TextUtils
import androidx.room.*
import com.google.gson.Gson
import com.ragazm.mototracker.model.Route


/**
 *Created by Andris on 13-Jul-19
 */

@Entity(tableName = "rides_table")

data class DatabaseRideModel(
    @PrimaryKey(autoGenerate = true) var id: Int,
    @ColumnInfo(name = "name") var name: String,
                //json
    @ColumnInfo(name = "start_date") var startDate: String,
    @ColumnInfo(name = "finish_date") var finishDate: String,
                //distance in metres
    @ColumnInfo(name = "distance") var distance: Int,
                //duration in seconds
    @ColumnInfo(name= "duration") var duration: Int,


    @ColumnInfo(name = "route")
    @TypeConverters(RouteTypeConverter::class)
    var route: String,


    @ColumnInfo(name = "comment") var comment: String
)

class RouteTypeConverter {

    @TypeConverter
    fun stringToRoute(string: String): Route? {


        if (TextUtils.isEmpty(string))
            return null
        return Gson().fromJson(string, Route::class.java)
    }

    @TypeConverter
    private fun routeToString(list : List<String>): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}

