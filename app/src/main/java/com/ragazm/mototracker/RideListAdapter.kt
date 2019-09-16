package com.ragazm.mototracker

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.ragazm.mototracker.database.DatabaseRideModel
import com.ragazm.mototracker.model.Route

/**
 *Created by Andris on 09-Aug-19
 */

class RideListAdapter  internal constructor(context: Context): RecyclerView.Adapter<RideListAdapter.RideViewHolder>(){

    var context = context

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    //cashed copy
    private var rides = emptyList<DatabaseRideModel>()

    inner class RideViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val rideItemView: TextView = itemView.findViewById(R.id.textView)
        val rideItemView2: TextView = itemView.findViewById(R.id.textView2)
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return RideViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return rides.size
    }

    override fun onBindViewHolder(holder: RideViewHolder, position: Int) {


      val current = rides[position]
        Log.e("RIDEEEE:", current.route.toString())

        val text = current.route

        val route = Gson().fromJson(text, Route::class.java)
       // holder.rideItemView.text = route.route[position].longitude.toString()
        holder.rideItemView.text = current.id.toString()
        holder.rideItemView2.text = current.duration.toString()


        holder.itemView.setOnClickListener {
            val intent = Intent(context,DetailViewActivity::class.java)
            intent.putExtra("RIDE_ID", current.id)
            Log.e("RIDE_ID: ", current.id.toString())
            context.startActivity(intent)
        }

    }

    internal fun setRides(databaseRideModels: List<DatabaseRideModel>){
        this.rides = databaseRideModels
        notifyDataSetChanged()
    }

    companion object {
        const val EXTRA_REPLY = "RIDE_ID"
    }


}