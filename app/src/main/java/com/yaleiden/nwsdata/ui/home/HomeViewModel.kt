package com.yaleiden.nwsdata.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaleiden.nwsdata.ForecastHourlyData
import com.yaleiden.nwsdata.IconDecoder
import com.yaleiden.nwsdata.NwsApi
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import kotlin.collections.ArrayList


class HomeViewModel : ViewModel() {

    private val TAG = "HomeViewModel"
    var listResult: String = "No Data"

    //val startData = ForecastHourlyData

    private val _data = MutableLiveData<List<ForecastHourlyData>>().apply {
        value = emptyList<ForecastHourlyData>()
    }
    val data: LiveData<List<ForecastHourlyData>> = _data

    private fun getNwsHourlyForecast() {

        viewModelScope.launch {

            try {
                //listResult = NwsApi.retrofitService.getHourlyForecast()
                val response: Response<String> = NwsApi.retrofitService.getHourlyForecast()
               if (response.isSuccessful){
                   Log.d(TAG, response.toString())
                   lateinit var resultJson: JSONObject
                    //listResult = response.body()
                   try {
                       resultJson =
                           JSONObject(response.body())
                   } catch (e: Exception) {
                       Log.e(TAG, e.toString())
                   }
                   //TODO check resultJson for String error message

                   val propertiesJson = resultJson.getJSONObject("properties")
                   Log.d(TAG, "propertiesJson " + propertiesJson)

                   val periodsJson = propertiesJson.getJSONArray("periods")
                   Log.d(TAG, "periodsJson " + periodsJson)
                   Log.d(TAG, "periodsJson length" + periodsJson.length())
                   _data.value = makePeriods(periodsJson)
               }else{
                   _data.value = null
               }

            } catch (e: Exception) {
                Log.d(TAG, "error " + listResult)
                Log.e(TAG, e.toString())

            }

            Log.d(TAG, "response " + listResult)

        }
    }

    /**
     * Call getNwsHourlyForecast() on init so we can display status immediately.
     */
    init {
        getNwsHourlyForecast()
    }

    private fun makePeriods(jsonArray: JSONArray): ArrayList<ForecastHourlyData> {
        //val decoder: IconDecoder = IconDecoder()
        val forecastList: ArrayList<ForecastHourlyData> = ArrayList()
        for (i in 0 until jsonArray.length()) {
            val hourlyForecast = ForecastHourlyData()

            val forecast = jsonArray.getJSONObject(i)

            hourlyForecast.number = forecast.get("number") as Int
            hourlyForecast.name = forecast.getString("name")
            hourlyForecast.startTime = forecast.get("startTime") as String
            hourlyForecast.endTime = forecast.get("endTime") as String
            hourlyForecast.isDaytime = forecast.get("isDaytime") as Boolean
            hourlyForecast.temperature = forecast.get("temperature") as Int
            //hourlyForecast.temperatureTrend = forecast.get("temperatureTrend") as String
            hourlyForecast.temperatureUnit = forecast.get("temperatureUnit") as String
            hourlyForecast.windSpeed = forecast.get("windSpeed") as String
            hourlyForecast.windDirection = forecast.get("windDirection") as String
            hourlyForecast.icon = forecast.get("icon") as String
            Log.d(TAG, "make periods " + hourlyForecast.icon)
            hourlyForecast.shortForecast = forecast.get("shortForecast") as String
            val icn: String = getIcon(forecast.get("icon") as String)
            hourlyForecast.icon = icn

            forecastList.add(hourlyForecast)
        }

        return forecastList

    }

    //"icon": "https://api.weather.gov/icons/land/night/skc?size=small"
    //"icon": "https://api.weather.gov/icons/land/day/skc?size=small"
    fun getIcon(url: String): String {
        //Get this - "icon": "https://api.weather.gov/icons/land/night/skc
        val first = url.substringBefore("?")
        //Get this - night/skc
        val second = first.substringAfter("land/")
        //Get this - skc
        val final = second.substringAfter("/")
        //Add "n" if value of second starts with "n"
        //So, night/skc would return nskc
        if(second.startsWith("n")){
            return "n" + final  //night icon name
        }
        return final  //day icon name
    }

}