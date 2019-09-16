package com.ragazm.mototracker.model

import java.io.Serializable

/**
 *Created by Andris on 12-Aug-19
 */

data class Ride (

            val id: Int,
                  val name: String,
    //json
                 val startDate: String,
                  val finishDate: String,
    //distance in metres
                 val distance: Int,
    //duration in seconds
                 val duration: Int,
    //json with route coordinates
                 val route: Route,
                  val comment: String) : Serializable