package com.ragazm.mototracker

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.ragazm.mototracker.database.*
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope

class DetailViewActivity : AppCompatActivity(), OnMapReadyCallback{


    lateinit var  repository: RideRepository
    lateinit var textId : TextView
    lateinit var entity: DatabaseRideModel



   // private val viewModelScope: CoroutineScope = this.rideViewModel.viewModelScope
   // lateinit var rideViewModel: RideViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_view)

        textId = findViewById(R.id.textViewId)

        val ridesDAO = RideDatabase.getDatabase(application, this.lifecycleScope).ridesDao()
        repository = RideRepository(ridesDAO)

        val extra : Int = intent.getIntExtra(RideListAdapter.EXTRA_REPLY, 0)



        //TODO check how to do this using ViewModel and other architecture components
        AsyncTask.execute {
            entity = repository.getRide(extra)
            Log.e("SINGLE ENTITY:", entity.toString())


        }

        textId.text = entity.id.toString()

    }

    override fun onMapReady(googleMap: GoogleMap?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }



}
