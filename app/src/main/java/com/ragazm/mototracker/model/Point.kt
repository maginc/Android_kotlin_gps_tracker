package com.ragazm.mototracker.model

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

/**
 *Created by Andris on 02-Aug-19
 */

data class Point(var latitude : Double, var longitude : Double, var speed : Float) : Serializable