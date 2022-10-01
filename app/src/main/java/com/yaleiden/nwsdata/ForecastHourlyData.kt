package com.yaleiden.nwsdata

import androidx.recyclerview.widget.DiffUtil


class ForecastHourlyData {


    var number: Int = 0 //": 1, Time Period number

    var name:String = "null" //": "This Afternoon",

    var startTime:String = "null"//": "",

    var endTime:String = "null"//": "2022-09-15T18:00:00-04:00",

    var isDaytime:Boolean = true//": true,

    var temperature:Int = 111//": 79,

    var temperatureUnit:String = "null"//": "F",

    var temperatureTrend:String = "null"//": null,

    var windSpeed:String = "null"//": "9 mph",

    var windDirection:String = "null"//": "N",

    var icon: String = "null"//": "https://api.weather.gov/icons/land/day/sct?size=medium",

    var shortForecast:String = "null"//": "Mostly Sunny",

    var detailedForecast:String = "null"//":

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<ForecastHourlyData>() {
            override fun areItemsTheSame(oldItem: ForecastHourlyData, newItem: ForecastHourlyData) =
                oldItem.startTime == newItem.startTime

            override fun areContentsTheSame(oldItem: ForecastHourlyData, newItem: ForecastHourlyData) =
                oldItem == newItem
        }
    }
}
