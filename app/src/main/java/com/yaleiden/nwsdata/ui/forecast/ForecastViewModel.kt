package com.yaleiden.nwsdata.ui.forecast

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaleiden.nwsdata.ForecastHourlyData
import com.yaleiden.nwsdata.NwsApi
import com.yaleiden.nwsdata.NwsApiService
//import com.yaleiden.nwsdata.pointLocations
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import kotlin.collections.ArrayList


class ForecastViewModel : ViewModel() {

    private val TAG = "ForecastViewModel"
    var listResult: String = "No Data"

    private val _data = MutableLiveData<List<ForecastHourlyData>>().apply {
        value = emptyList<ForecastHourlyData>()
    }

    val data: LiveData<List<ForecastHourlyData>> = _data

    //  Location name to go along with point forecast
    lateinit var location: String
    //val location: String = NwsApi.location
    //NwsApi.location

    fun getNwsHourlyForecast() {

        //NwsApi.listLocation = 0
        location = NwsApi.location
        Log.d(TAG, "location = " + location)
        viewModelScope.launch {
            Log.d(TAG, "viewModelScope.launch")

            var errorMsg: String = "No Error"
            try {

                val response: Response<String> = NwsApi.retrofitService.getHourlyForecast()
                if (response.isSuccessful) {
                    Log.d(TAG, "response.isSuccessful ")
                    Log.d(TAG, response.toString())
                    lateinit var resultJson: JSONObject

                    try {
                        Log.d(TAG, "getNwsHourlyForecast() try")
                        resultJson =
                            JSONObject(response.body())
                    } catch (e: Exception) {
                        Log.d(TAG, "catch " + e.message)
                        Log.e(TAG, e.toString())
                        errorMsg = e.message.toString()
                        val errorData = ForecastHourlyData()
                        errorData.number = 999
                        errorData.shortForecast = errorMsg
                        _data.value = _data.value?.plus(errorData)
                    }

                    val propertiesJson = resultJson.getJSONObject("properties")
                    Log.d(TAG, "propertiesJson " + propertiesJson)

                    val periodsJson = propertiesJson.getJSONArray("periods")
                    //Log.d(TAG, "periodsJson " + periodsJson)
                    //Log.d(TAG, "periodsJson length" + periodsJson.length())
                    _data.value = makePeriods(periodsJson)
                } else {
                    Log.d(TAG, "getNwsHourlyForecast() else")
                    val errorData = ForecastHourlyData()
                    errorData.number = 999
                    errorData.shortForecast = errorMsg
                    _data.value = _data.value?.plus(errorData)

                }

            } catch (e: Exception) {
                Log.d(TAG, "error " + listResult)
                Log.e(TAG, e.toString())
                val errorData = ForecastHourlyData()
                errorData.number = 999
                errorData.shortForecast = e.message.toString()
                _data.value = _data.value?.plus(errorData)

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
            //Log.d(TAG, "make periods " + hourlyForecast.icon)
            hourlyForecast.shortForecast = forecast.get("shortForecast") as String
            //hourlyForecast.detailedForecast = forecast.get("detailedForecast") as String
            val probPrecip = forecast.getJSONObject("probabilityOfPrecipitation")
            hourlyForecast.probabilityOfPrecipitation = probPrecip.getInt("value")
            //hourlyForecast.probabilityOfPrecipitation = forecast.get("probabilityOfPrecipitation") as Int
            val relHum = forecast.getJSONObject("dewpoint")
            hourlyForecast.relativeHumidity = relHum.getInt("value")
            //hourlyForecast.relativeHumidity = hourlyForecast.relativeHumidity = forecast.get("relativeHumidity") as Int
            val icn: String = getIcon(forecast.get("icon") as String)
            hourlyForecast.icon = icn

            forecastList.add(hourlyForecast)
        }

        return forecastList

    }

    /**
     * @param String
     * @return String
     * Parses local resource image name from url returned by NWS API
     * "icon": "https://api.weather.gov/icons/land/night/few?size=small"
     * "icon":"https://api.weather.gov/icons/land/night/skc,0?size=small"
     */
    fun getIcon(url: String): String {
        //Get this - "icon": "https://api.weather.gov/icons/land/night/skc
        val first = url.substringBefore("?")
        //Get this - night/skc
        val second = first.substringAfter("land/")
        //Get this - skc
        val final = second.substringAfter("/")
        //Add "n" if value of second starts with "n"
        //So, night/skc would return nskc
        if (second.startsWith("n")) {
            return "n" + final  //night icon name
        }
        return final  //day icon name
    }

}