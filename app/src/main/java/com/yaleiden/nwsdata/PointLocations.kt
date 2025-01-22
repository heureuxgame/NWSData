package com.yaleiden.nwsdata

class PointLocations private constructor() {

    companion object {
        val instance:PointLocations by lazy {
            PointLocations()
        }
    }

    fun getName() = names[position.toInt()]
    fun getLoc() = points[position.toInt()]

    var position:String = "0"

    var names = arrayListOf<String>("Clay Hill, GA", "Hawks Rest BTNF, WY")
    public var points = arrayListOf<String>(
        "https://api.weather.gov/gridpoints/CAE/14,41/forecast/",
        "https://api.weather.gov/gridpoints/RIW/67,165/forecast/"
    )

}