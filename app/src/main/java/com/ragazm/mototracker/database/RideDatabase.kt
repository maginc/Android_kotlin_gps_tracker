package com.ragazm.mototracker.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ragazm.mototracker.model.Point
import com.ragazm.mototracker.model.Route
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 *Created by Andris on 16-Jul-19
 */
@Database(entities = [DatabaseRideModel::class], version = 1)
abstract class RideDatabase: RoomDatabase() {

    abstract fun ridesDao(): RidesDAO

    companion object{
        @Volatile
        private var INSTANCE: RideDatabase ?= null

        fun getDatabase(context: Context, scope: CoroutineScope): RideDatabase {


            Log.e("RIDE:", "getdatabase")
            return INSTANCE ?: synchronized(this){
                val instance  = Room.databaseBuilder(
                    context.applicationContext,
                    RideDatabase::class.java,
                    "rides_table"
                )
                    //.fallbackToDestructiveMigration()

                   // .addCallback(RideDatabaseCallback(scope))
                    .build()
                INSTANCE = instance

                instance
            }
        }
//TODO review this stuffn

        private class RideDatabaseCallback(private val scope: CoroutineScope): Callback() {


            override fun onOpen(db: SupportSQLiteDatabase) {
                Log.e("RIDE:", "ONOPEN")
                super.onOpen(db)

                INSTANCE?.let { database ->
                    scope.launch{
                        populateDatabase(database.ridesDao())
                    }
                }
            }
        }

        suspend fun populateDatabase(ridesDAO: RidesDAO){

            Log.e("RIDE:", "populatedatabase")
            ridesDAO.deleteAll()

           lateinit var point : Point
             var listOfPoints : MutableList<Point> = arrayListOf()

            point = Point(0.0, 0.0, 0)

            for (i in 0..100) {
                point.latitude = 56.888357 + i
                point.longitude = 24.150330 + i
                point.speed = 20 + i

                listOfPoints.add(i, point)
            }





            var route = Route(listOfPoints)

            var ride = DatabaseRideModel(0,"2nd ride","13.05.2033T22:04","14.05.2033T23:04",33000,36000,route.toString() ,"")
            ridesDAO.insert(ride)


            Log.e("RIDE: " , ride.toString())


        }


    }

}