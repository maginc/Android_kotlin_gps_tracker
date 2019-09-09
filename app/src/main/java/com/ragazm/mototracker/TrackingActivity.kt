package com.ragazm.mototracker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import com.ragazm.mototracker.model.Ride
import com.ragazm.mototracker.model.Point
import com.ragazm.mototracker.model.Route

class TrackingActivity : AppCompatActivity() {

    var listOfPoints : MutableList<Point> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)








        val button = findViewById<Button>(R.id.button_save)
        button.setOnClickListener {
            val replyIntent = Intent()
            var point = Point(0.0, 0.0, 0)

            for (i in 0..100) {
                point.latitude = 56.888357 + i
                point.longitude = 24.150330 + i
                point.speed = 20 + i

                listOfPoints.add(i, point)
            }

            var route = Route(listOfPoints)

            var ride = Ride(0,"1st ride","13.05.2033T22:04","14.05.2033T23:04",33000,36000,route.toString() ,"")
            replyIntent.putExtra(EXTRA_REPLY, ride)
            setResult(Activity.RESULT_OK, replyIntent)


            finish()
        }
    }



    companion object {
        const val EXTRA_REPLY = "com.ragazm.mototracker.trackingactivity.REPLY"
    }
}
