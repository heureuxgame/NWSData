package com.yaleiden.nwsdata

import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import java.util.logging.Logger

class ForecastHourlyData {
private val TAG = "ForecastHourlyData"

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

    var icon:String = "null"//": "https://api.weather.gov/icons/land/day/sct?size=medium",

    var shortForecast:String = "null"//": "Mostly Sunny",

    //var detailedForecast:String = "null"//":

    var probabilityOfPrecipitation:Int = 100
    /*
    "probabilityOfPrecipitation": {
        "unitCode": "wmoUnit:percent",
        "value": 40
    }
     */
    var relativeHumidity:String = "null"
    /*
    "relativeHumidity": {
        "unitCode": "wmoUnit:percent",
        "value": 64
    }
    */
    var dewpoint:String = "null"
    /*
    "dewpoint":{"unitCode":"wmoUnit:degC","value":0}
*/
/*
    companion object {
        private val TAG: String = "ForecastHourlyData CO"
        val LOG = Logger.getLogger(ForecastHourlyData::class.java.name)
        val DiffCallback = object : DiffUtil.ItemCallback<ForecastHourlyData>() {

            override fun areItemsTheSame(
                oldItem: ForecastHourlyData,
                newItem: ForecastHourlyData
            ): Boolean {
                LOG.warning(TAG +" areItemsTheSame " + oldItem.number +" "+ newItem.number)
                return oldItem.number == newItem.number
            }

            override fun areContentsTheSame(
                oldItem: ForecastHourlyData,
                newItem: ForecastHourlyData
            ): Boolean {
                LOG.warning( TAG +" areContentsTheSame " + oldItem.startTime +" "+ newItem.startTime)
                return oldItem.startTime.equals(newItem.startTime)
                        && oldItem.endTime.equals(newItem.endTime)
                        && oldItem.shortForecast.equals(newItem.shortForecast)
                        && oldItem.temperature.equals(newItem.temperature)
                        && oldItem.icon.equals(newItem.icon)
            }

        }
    }

*/
}
