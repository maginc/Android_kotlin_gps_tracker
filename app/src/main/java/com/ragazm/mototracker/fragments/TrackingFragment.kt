package com.ragazm.mototracker.fragments


import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.gson.GsonBuilder
import com.ragazm.mototracker.R
import com.ragazm.mototracker.RideListAdapter
import com.ragazm.mototracker.database.DatabaseRideModel
import com.ragazm.mototracker.database.RideViewModel
import com.ragazm.mototracker.model.Point
import com.ragazm.mototracker.model.Ride
import com.ragazm.mototracker.model.Route
import com.ragazm.mototracker.utilities.Utilities

/**
 * A simple [Fragment] subclass.
 */
class TrackingFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private lateinit var locationManager: LocationManager
    private lateinit var showLatitude: TextView
    private lateinit var showLongitude: TextView
    private lateinit var showSpeed: TextView

    lateinit var rideViewModel: RideViewModel

    private lateinit var marker: Marker
    private var mMap: GoogleMap? = null
    private var i: Int = 0


    private val isLocationEnabled: Boolean
        get() = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    private val isPermissionGranted: Boolean
        get() {
            return if (!(checkSelfPermission(
                    activity!!.application,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager
                    .PERMISSION_GRANTED && checkSelfPermission(
                    activity!!.application,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED)
            ) {
                Log.v("mylog", "Permission is granted")
                true
            } else {
                Log.v("mylog", "Permission not granted")
                false
            }
        }

    private var listOfPoints: MutableList<Point> = arrayListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showLatitude = view.findViewById(R.id.textViewLatitude)
        showLongitude = view.findViewById(R.id.textViewLongitude)
        showSpeed = view.findViewById(R.id.textViewSpeed)

        val button = view.findViewById<Button>(R.id.button_save)

        button.setOnClickListener {
            val replyIntent = Intent()


            var route = Route(listOfPoints)

            var ride = Ride(
                0, "1st ride", "13.05.2033T22:04", "14.05.2033T23:04", 33000, 36000,
                route, ""
            )


            replyIntent.putExtra(EXTRA_REPLY, ride)
            // setResult(Activity.RESULT_OK, replyIntent)


            locationManager.removeUpdates(this)


            var gson = GsonBuilder().disableHtmlEscaping().create()
            var rrt = gson.toJson(ride.route)

            var databaseRide = DatabaseRideModel(
                ride.id,
                ride.name,
                ride.startDate,
                ride.finishDate,
                ride.distance,
                ride.duration,
                // ride.route.toString(),
                rrt,
                ride.comment
            )

            rideViewModel.insert(databaseRide)

            activity!!.onBackPressed()


        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tracking, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //rideViewModel = ViewModelProviders.of(this).get(RideViewModel::class.java)
        rideViewModel = ViewModelProvider(this).get(RideViewModel::class.java)

        val adapter = RideListAdapter(activity!!.application)


        rideViewModel.allRides.observe(this, Observer { rides ->
            rides?.let { adapter.setRides(it) }
        })

        locationManager = activity!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager





        if (Build.VERSION.SDK_INT >= 23 && !isPermissionGranted) {
            requestPermissions(PERMISSIONS, PERMISSION_ALL)
        } else
            requestLocation()
        if (!isLocationEnabled) {
            showAlert(1)
        }


    }

    private fun requestLocation() {
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.powerRequirement = Criteria.POWER_HIGH
        val provider = locationManager.getBestProvider(criteria, true)
        if (ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager
                .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity!!.applicationContext,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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


    override fun onMapReady(p0: GoogleMap?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(location: Location?) {
        var myCoordinates = LatLng(location!!.latitude, location.longitude)
        var point = Point(
            "%.6f".format(location.latitude).toDouble(),
            "%.6f".format(location.longitude).toDouble(),
            location.speed.toInt()
        )
        listOfPoints.add(i, point)
        i++
        Log.e("Coordinates:", myCoordinates.toString())
        Log.e("Point: ", listOfPoints.toString())
        Log.e("PNT:", point.toString())

        showLatitude.text = ("%.6f".format(location.latitude).toDouble()).toString()
        showLongitude.text = ("%.6f".format(location.longitude).toDouble()).toString()
        showSpeed.text = Utilities.mpsToKmh(location.speed).toString()
        // marker.position = myCoordinates
        //  mMap!!.moveCamera(CameraUpdateFactory.newLatLng(myCoordinates))
        //  mMap!!.isMyLocationEnabled = true
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        when (status) {
            LocationProvider.OUT_OF_SERVICE -> {
                Log.v(tag, "Status Changed: Out of Service")
                Toast.makeText(
                    activity!!.application, "Status Changed: Out of Service",
                    Toast.LENGTH_SHORT
                ).show()
            }

            LocationProvider.TEMPORARILY_UNAVAILABLE -> {
                Log.v(tag, "Status Changed: Temporarily Unavailable")
                //  Toast.makeText(
                //      this, "Status Changed: Temporarily Unavailable",
                //      Toast.LENGTH_SHORT
                // ).show();
            }
            LocationProvider.AVAILABLE -> {
                Log.v(tag, "Status Changed: Available")
                //  Toast.makeText(
                //      this, "Status Changed: Available",
                //      Toast.LENGTH_SHORT
                //  ).show();
            }
        }
    }

    override fun onProviderEnabled(provider: String?) {
        Log.v(tag, "Status Changed: Available")
        Toast.makeText(
            activity!!.application, "GPS Enabled",
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onProviderDisabled(provider: String?) {
        Log.v(tag, "Status Changed: Available")
        Toast.makeText(
            activity!!.application, "GPS Disabled",
            Toast.LENGTH_SHORT
        ).show()
    }

    companion object {
        const val EXTRA_REPLY = "com.ragazm.mototracker.trackingactivity.REPLY"
        internal const val PERMISSION_ALL = 1
        internal val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showAlert(status: Int) {
        val message: String
        val title: String
        val btnText: String
        if (status == 1) {
            message =
                "Your Locations Settings is set to 'Off'.\nPlease Enable Location to " + "use this app"
            title = "Enable Location"
            btnText = "Location Settings"
        } else {
            message = "Please allow this app to access location!"
            title = "Permission access"
            btnText = "Grant"
        }
        val dialog = AlertDialog.Builder(activity!!.application)
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
            .setNegativeButton("Cancel") { _, _ -> activity!!.supportFragmentManager.popBackStack() }
        dialog.show()
    }


}
