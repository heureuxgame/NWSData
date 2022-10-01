package com.yaleiden.nwsdata

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat

class IconDecoder {
    val TAG:String = "IconDecoder"

    constructor()

    //"icon": "https://api.weather.gov/icons/land/night/few?size=small",
    fun getIcon(url: String) {
        val first = url.substringBefore("?")
        val second = first.substringAfter("land/")
        val final = second.substringAfter("/")
        Log.d(TAG, "final " + final)

    }



}