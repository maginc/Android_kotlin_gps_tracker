package com.ragazm.mototracker.utilities

/**
 *Created by Andris on 31-Aug-19
 */

public class Utilities(){

    companion object{

        fun mpsToKmh(speed: Float)  = (speed * 3.6).toFloat()
        fun mpsToMph(speed: Float) = (speed * 2.237).toFloat()

    }

}