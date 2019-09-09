package com.ragazm.mototracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ragazm.mototracker.database.DatabaseRideModel
import com.ragazm.mototracker.model.Ride
import com.ragazm.mototracker.model.Point
import com.ragazm.mototracker.model.Route

import com.ragazm.mototracker.database.RideViewModel

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val trackingActivityRequestCode = 1
    
    lateinit var rideViewModel: RideViewModel

    lateinit var point : Point
    var listOfPoints : MutableList<Point> = arrayListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        rideViewModel = ViewModelProviders.of(this).get(RideViewModel::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = RideListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)


        rideViewModel.allRides.observe(this, Observer { rides ->
            rides?.let {adapter.setRides(it)}
        })




        fab.setOnClickListener { view ->


            val intent = Intent(this@MainActivity, TrackingActivity::class.java)
            startActivityForResult(intent, trackingActivityRequestCode)

            point = Point(0.0, 0.0, 0)

            for (i in 0..100) {
                point.latitude = 56.888357 + i
                point.longitude = 24.150330 + i
                point.speed = 20 + i

                listOfPoints.add(i, point)
            }

            var route = Route(listOfPoints)

            var ride = Ride(0,"1st ride","13.05.2033T22:04","14.05.2033T23:04",33000,36000,route.toString() ,"")

            Log.e("RIDE:", ride.toString())

         //   rideViewModel.insert(ride)

            Log.e("RIDE:", "BUTTON PRESSED")
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()


        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == trackingActivityRequestCode && resultCode == Activity.RESULT_OK){
            data?.let {
                val ride: Ride = data.extras.getSerializable(TrackingActivity.EXTRA_REPLY) as Ride

                var databaseRide = DatabaseRideModel(
                    ride.id,
                    ride.name,
                    ride.startDate,
                    ride.finishDate,
                    ride.distance,
                    ride.duration,
                    ride.route.toString(),
                    ride.comment)

                rideViewModel.insert(databaseRide)

                Log.e("MYEXTRA: ", ride.toString())

            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
