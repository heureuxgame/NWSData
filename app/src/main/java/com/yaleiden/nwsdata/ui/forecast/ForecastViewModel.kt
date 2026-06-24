package com.yaleiden.nwsdata.ui.forecast

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.yaleiden.nwsdata.ForecastHourlyData
import com.yaleiden.nwsdata.LocationData
import com.yaleiden.nwsdata.NwsApi
import com.yaleiden.nwsdata.SunApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response

class ForecastViewModel : ViewModel() {

    private val TAG = "ForecastViewModel"

    private val _locationList = MutableLiveData<ArrayList<LocationData>>(ArrayList())
    val locationList: LiveData<ArrayList<LocationData>> = _locationList

    private val _data = MutableLiveData<List<ForecastHourlyData>>(emptyList())
    val data: LiveData<List<ForecastHourlyData>> = _data

    private val _suntime = MutableLiveData<String>()
    val suntime: LiveData<String> = _suntime

    private val _locationIndex = MutableLiveData(0)
    val locationIndex: LiveData<Int> = _locationIndex

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    val selectedForecastUrl: LiveData<String> = _locationIndex.map { index ->
        com.yaleiden.nwsdata.PointLocations.forecastUrls[index]
    }

    init {
        populateLocationData()
        // REMOVED: getNwsHourlyForecast() call from init block to stop duplicate requests
    }

    private fun populateLocationData() {
        val currentList = _locationList.value ?: ArrayList()
        currentList.add(LocationData(33.656433F, -82.42646F, "America/New_York"))
        currentList.add(LocationData(44.11283F, -110.09824F, "America/Boise"))
        currentList.add(LocationData(32.819923F, -81.483925F, "America/New_York"))
        currentList.add(LocationData(30.702417F, -89.447622F, "America/Chicago"))
        currentList.add(LocationData(71.287F, -156.767F, "America/Anchorage"))
        _locationList.value = currentList
    }

    fun setLocationIndex(index: Int) {
        _locationIndex.value = index
    }

    fun getCurrentLocationData(): LocationData? {
        val currentIndex = _locationIndex.value ?: return null
        val list = _locationList.value
        if (list != null && currentIndex >= 0 && currentIndex < list.size) {
            return list[currentIndex]
        }
        return null
    }

    fun getNwsHourlyForecast() {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. Fetch Sunrise details in background context
            try {
                val sunLoc = getCurrentLocationData()
                val responseSun: Response<String> = SunApi.retrofitServiceSun.getSunriseTime(
                    sunLoc?.latitude, sunLoc?.longitude, "today", sunLoc?.timezone
                )
                if (responseSun.isSuccessful) {
                    // Shift structural JSON breakdown processing cleanly onto background IO threads
                    withContext(Dispatchers.IO) {
                        val resultJsonSun = JSONObject(responseSun.body() ?: "")
                        val results = resultJsonSun.getJSONObject("results")
                        val sunriseTime = results.optString("sunrise")
                        val sunsetTime = results.optString("sunset")
                        _suntime.postValue("$sunriseTime     $sunsetTime")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sunrise load failed", e)
            }

            // 2. Fetch National Weather Service Metrics
            try {
                val url = selectedForecastUrl.value
                if (url != null) {
                    val response: Response<String> = NwsApi.retrofitService.getHourlyForecast(url)
                    if (response.isSuccessful) {

                        // Parse complex, heavy weather responses safely using Dispatchers.IO
                        val parsedPeriods = withContext(Dispatchers.IO) {
                            val resultJson = JSONObject(response.body() ?: "")
                            val propertiesJson = resultJson.getJSONObject("properties")
                            val periodsJson = propertiesJson.getJSONArray("periods")
                            makePeriods(periodsJson)
                        }
                        _data.value = parsedPeriods
                    } else {
                        showErrorRow("API Error: code ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "NWS load failed", e)
                showErrorRow(e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun showErrorRow(message: String) {
        val errorData = ForecastHourlyData().apply {
            number = 999
            shortForecast = message
        }
        _data.value = listOf(errorData)
    }

    private fun makePeriods(jsonArray: JSONArray): ArrayList<ForecastHourlyData> {
        val forecastList = ArrayList<ForecastHourlyData>()
        for (i in 0 until jsonArray.length()) {
            val forecast = jsonArray.getJSONObject(i)
            val hourlyForecast = ForecastHourlyData().apply {
                number = forecast.getInt("number")
                name = forecast.optString("name")
                startTime = forecast.getString("startTime")
                endTime = forecast.getString("endTime")
                isDaytime = forecast.getBoolean("isDaytime")
                temperature = forecast.getInt("temperature")
                temperatureUnit = forecast.optString("temperatureUnit")
                windSpeed = forecast.optString("windSpeed")
                windDirection = forecast.optString("windDirection")
                shortForecast = forecast.getString("shortForecast")
                probabilityOfPrecipitation = forecast.getJSONObject("probabilityOfPrecipitation").optInt("value", 0)
                relativeHumidity = forecast.getJSONObject("relativeHumidity").opt("value").toString()
                dewpoint = forecast.getJSONObject("dewpoint").opt("value").toString()
                icon = getIcon(forecast.getString("icon"))
            }
            forecastList.add(hourlyForecast)
        }
        return forecastList
    }

    fun getIcon(url: String): String {
        val first = url.substringBefore("?")
        val second = first.substringAfter("land/")
        val final = second.substringAfter("/")
        return if (second.startsWith("n")) "n$final" else final
    }
}