package com.ragazm.mototracker

import android.graphics.Color
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson

import com.ragazm.mototracker.database.*
import com.ragazm.mototracker.model.Route
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail_view.view.*
import kotlinx.coroutines.CoroutineScope

class DetailViewActivity : FragmentActivity(), OnMapReadyCallback{


     var  map: GoogleMap? =null

    lateinit var  cameraUpdate: CameraUpdate

   var pointsOfRoute : ArrayList<LatLng> = mutableListOf<LatLng>() as ArrayList<LatLng>


    lateinit var  repository: RideRepository
    lateinit var textId : TextView
    lateinit var entity: DatabaseRideModel

    private lateinit var mo: MarkerOptions
    private lateinit var marker: Marker


    private var mUiSettings: UiSettings? = null
    private val INITIAL_STROKE_WIDTH_PX = 10




   // private val viewModelScope: CoroutineScope = this.rideViewModel.viewModelScope
   // lateinit var rideViewModel: RideViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)



        mo = MarkerOptions().position(LatLng(56.879419, 24.168836)).title("My Current Location")



        textId = findViewById(R.id.textViewId)

        val ridesDAO = RideDatabase.getDatabase(application, this.lifecycleScope).ridesDao()
        repository = RideRepository(ridesDAO)

        val extra : Int = intent.getIntExtra(RideListAdapter.EXTRA_REPLY, 0)

        cameraUpdate = CameraUpdateFactory.newLatLngZoom( LatLng(56.879419, 24.168836), 10.0f)


        //TODO check how to do this using ViewModel and other architecture components
        AsyncTask.execute {


            entity = repository.getRide(extra)
            Log.e("SINGLE ENTITY:", entity.toString())


            val text = entity.route

            val route = Gson().fromJson(text, Route::class.java)



          route.route.forEach {
              Log.e("taag",it.toString())


              var latLng: LatLng = LatLng(it.latitude, it.longitude)

              pointsOfRoute.add(latLng)





          }


            Log.e("POLYLINE: ", pointsOfRoute.toString())

            textId.text = entity.id.toString()


        }



    }



    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap
        marker = map!!.addMarker(mo)
        mUiSettings = map!!.uiSettings
        mUiSettings!!.isZoomControlsEnabled = true
        mUiSettings!!.isMyLocationButtonEnabled = true


        map!!.animateCamera(cameraUpdate)

        with(googleMap) {

            // A geodesic polyline that goes around the world.
            addPolyline(PolylineOptions().apply {

                addAll(pointsOfRoute)
               // add( aklLatLng, jfkLatLng, laxLatLng,lhrLatLng,lhrLatLng2,lhrLatLng3)
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
