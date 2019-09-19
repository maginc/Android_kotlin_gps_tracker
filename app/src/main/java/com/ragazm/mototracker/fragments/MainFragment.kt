package com.ragazm.mototracker.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ragazm.mototracker.R
import com.ragazm.mototracker.RideListAdapter
import com.ragazm.mototracker.database.RideViewModel

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    lateinit var rideViewModel: RideViewModel
    lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)

       // rideViewModel = ViewModelProviders.of(this).get(RideViewModel::class.java)
        rideViewModel = ViewModelProvider(this).get(RideViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = this.activity?.let { RideListAdapter(it) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity) as RecyclerView.LayoutManager?


        rideViewModel.allRides.observe(this, Observer { rides ->
            rides?.let { adapter!!.setRides(it) }
        })
    }

}
