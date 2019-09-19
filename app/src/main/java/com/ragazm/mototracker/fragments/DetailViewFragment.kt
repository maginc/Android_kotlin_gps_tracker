package com.ragazm.mototracker.fragments


import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import com.ragazm.mototracker.R
import com.ragazm.mototracker.database.DatabaseRideModel
import com.ragazm.mototracker.database.RideDatabase
import com.ragazm.mototracker.database.RideRepository
import com.ragazm.mototracker.model.Route


/**
 * A simple [Fragment] subclass.
 */
class DetailViewFragment : Fragment(), OnMapReadyCallback {


    var map: GoogleMap? = null
    lateinit var cameraUpdate: CameraUpdate
    var pointsOfRoute: ArrayList<LatLng> = mutableListOf<LatLng>() as ArrayList<LatLng>

    private lateinit var mo: MarkerOptions
    private lateinit var marker: Marker
    private lateinit var lastPointLatLang: LatLng


    private var mUiSettings: UiSettings? = null
    private val INITIAL_STROKE_WIDTH_PX = 10


    lateinit var repository: RideRepository
    lateinit var textId: TextView
    lateinit var entity: DatabaseRideModel
    //var entity = DatabaseRideModel(0, "", "", "", 0, 0,"","")

    lateinit var rideId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        //fab.setVisibility(View.GONE);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_view, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        textId = view.findViewById(R.id.textViewId)


        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapViewDetail) as SupportMapFragment?

        mapFragment?.getMapAsync(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ridesDAO =
            RideDatabase.getDatabase(this.activity!!.applicationContext, this.lifecycleScope)
                .ridesDao()
        repository = RideRepository(ridesDAO)


        rideId = arguments!!.getString("RIDE_ID")
        Log.e("RIDE_ID:", rideId)
        val extra: Int = rideId.toInt()
        //TODO check how to do this using ViewModel and other architecture components
        AsyncTask.execute {

            entity = repository.getRide(extra)

            Log.e("SINGLE ENTITY:", entity.toString())
            val text = entity.route

            val route = Gson().fromJson(text, Route::class.java)



            route.route.forEach {
                Log.e("taag", it.toString())


                var latLng: LatLng = LatLng(it.latitude, it.longitude)

                pointsOfRoute.add(latLng)

                lastPointLatLang = LatLng(route.route.last().latitude, route.route.last().longitude)
                mo = MarkerOptions().position(lastPointLatLang).title("My Current Location")

                //textId.text = entity.id.toString()
            }


        }


    }


    override fun onMapReady(googleMap: GoogleMap?) {
        map = googleMap
        marker = map!!.addMarker(mo)
        mUiSettings = map!!.uiSettings
        mUiSettings!!.isZoomControlsEnabled = true
        mUiSettings!!.isMyLocationButtonEnabled = true

        cameraUpdate = CameraUpdateFactory.newLatLngZoom(lastPointLatLang, 11.0f)
        map!!.animateCamera(cameraUpdate)

        with(googleMap) {

            // A geodesic polyline that goes around the world.
            this!!.addPolyline(PolylineOptions().apply {

                addAll(pointsOfRoute)
                width(INITIAL_STROKE_WIDTH_PX.toFloat())
                color(Color.BLUE)
                geodesic(true)
                clickable(true)
            })


            // Add a listener for polyline clicks that changes the clicked polyline's color.
            setOnPolylineClickListener { polyline ->
                // Flip the values of the red, green and blue components of the polyline's color.
                polyline.color = polyline.color xor 0x0000ffff
            }
        }

    }


}
