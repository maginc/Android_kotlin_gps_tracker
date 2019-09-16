package com.ragazm.mototracker

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.ragazm.mototracker.model.Ride
import com.ragazm.mototracker.model.Point
import com.ragazm.mototracker.model.Route
import com.ragazm.mototracker.utilities.Utilities

class TrackingActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var showLatitude : TextView
    private lateinit var showLongitude: TextView
    private lateinit var showSpeed: TextView

    private val tag : String = "LOGGERTRACKIINGACTIVITY"

    private lateinit var marker: Marker
    private var mMap: GoogleMap? = null
    private var i : Int = 0
    private val isLocationEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager
            .isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    private val isPermissionGranted: Boolean
        get() {
            return if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager
                        .PERMISSION_GRANTED || checkSelfPermission(android.Manifest.permission
                        .ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                } else {
                    TODO("VERSION.SDK_INT < M")
                }
            ) {
                Log.v("mylog", "Permission is granted")
                true
            } else {
                Log.v("mylog", "Permission not granted")
                false
            }
        }

     private var listOfPoints : MutableList<Point> = arrayListOf()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        showLatitude = findViewById(R.id.textViewLatitude)
        showLongitude = findViewById(R.id.textViewLongitude)
        showSpeed = findViewById(R.id.textViewSpeed)



        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL)
        } else
            requestLocation()
        if (!isLocationEnabled) {
            showAlert(1)
        }

        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()




            var route = Route(listOfPoints)

            var ride = Ride(0,"1st ride","13.05.2033T22:04","14.05.2033T23:04",33000,36000,route ,"")


            replyIntent.putExtra(EXTRA_REPLY, ride)
            setResult(Activity.RESULT_OK, replyIntent)


                locationManager.removeUpdates(this);


            finish()
        }
    }

    override fun onMapReady(p0: GoogleMap?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun requestLocation() {
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.powerRequirement = Criteria.POWER_HIGH
        val provider = locationManager.getBestProvider(criteria, true)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(provider, 1000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {

        var myCoordinates = LatLng(location.latitude, location.longitude)
        var point = Point("%.6f".format(location.latitude).toDouble(), "%.6f".format(location.longitude).toDouble(), location.speed)
        listOfPoints.add(i,point)
        i++
        Log.e("Coordinates:", myCoordinates.toString())
        Log.e("Point: ", listOfPoints.toString())
        Log.e("PNT:" , point.toString())

        showLatitude.text = ("%.6f".format(location.latitude).toDouble()).toString()
        showLongitude.text = ("%.6f".format(location.longitude).toDouble()).toString()
        showSpeed.text = Utilities.mpsToKmh(location.speed).toString()
       // marker.position = myCoordinates
      //  mMap!!.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates))
      //  mMap!!.isMyLocationEnabled = true
    }

    override fun onStatusChanged(status: String?, p1: Int, p2: Bundle?) {
      when (p1) {
          LocationProvider . OUT_OF_SERVICE -> {
              Log.v(tag, "Status Changed: Out of Service")
              Toast.makeText(
                  this, "Status Changed: Out of Service",
                  Toast.LENGTH_SHORT
              ).show();
          }

            LocationProvider . TEMPORARILY_UNAVAILABLE -> {
                Log.v(tag, "Status Changed: Temporarily Unavailable");
              //  Toast.makeText(
              //      this, "Status Changed: Temporarily Unavailable",
              //      Toast.LENGTH_SHORT
               // ).show();
            }
             LocationProvider . AVAILABLE -> {
                 Log.v(tag, "Status Changed: Available");
               //  Toast.makeText(
               //      this, "Status Changed: Available",
               //      Toast.LENGTH_SHORT
               //  ).show();
             }
        }
    }

    override fun onProviderEnabled(p0: String?) {
        Log.v(tag, "Status Changed: Available");
          Toast.makeText(
              this, "GPS Enabled",
              Toast.LENGTH_SHORT
          ).show();
    }

    override fun onProviderDisabled(p0: String?) {
        Log.v(tag, "Status Changed: Available");
        Toast.makeText(
            this, "GPS Disabled",
            Toast.LENGTH_SHORT
        ).show();
    }



    companion object {
        const val EXTRA_REPLY = "com.ragazm.mototracker.trackingactivity.REPLY"
        internal const val PERMISSION_ALL = 1
        internal val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showAlert(status: Int) {
        val message: String
        val title: String
        val btnText: String
        if (status == 1) {
            message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app"
            title = "Enable Location"
            btnText = "Location Settings"
        } else {
            message = "Please allow this app to access location!"
            title = "Permission access"
            btnText = "Grant"
        }
        val dialog = AlertDialog.Builder(this)
        dialog.setCancelable(false)
        dialog.setTitle(title)
            .setMessage(message)
            .setPositiveButton(btnText) { _, _ ->
                if (status == 1) {
                    val myIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(myIntent)
                } else
                    requestPermissions(PERMISSIONS, PERMISSION_ALL)
            }
            .setNegativeButton("Cancel") { _, _ -> finish() }
        dialog.show()
    }


}



