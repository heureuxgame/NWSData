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
import com.yaleiden.nwsdata.PointLocations
import com.yaleiden.nwsdata.SunApi
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response
import kotlin.collections.ArrayList
// import androidx.lifecycle.Transformations


class ForecastViewModel : ViewModel() {



    private val TAG = "ForecastViewModel"
    //var listResult: String = "No Data"

    // 1. Private MutableLiveData: Used internally to modify the list.
    // We initialize it with an empty ArrayList.
    private val _locationList = MutableLiveData<ArrayList<LocationData>>(ArrayList())

    // 2. Public LiveData: Exposed to the Fragment for observation.
    // This is read-only, preventing the Fragment from modifying the data directly.
    val locationList: LiveData<ArrayList<LocationData>> = _locationList

    // 3. Initialization Block: Populate the list when the ViewModel is created.
    init {
        populateLocationData()
    }

    /**
     * Function to populate the list with the required LocationData objects.
     */
    private fun populateLocationData() {
        // Get the current list value (will be null or the empty ArrayList from init)
        val currentList = _locationList.value ?: ArrayList()

        // 1st Object (clay hill)
        currentList.add(
            LocationData(
                latitude = 33.656433F,
                longitude = -82.42646F,
                timezone = "America/New_York"
            )
        )

        // 2nd Object (hawks rest)
        currentList.add(
            LocationData(
                latitude = 44.11283F,
                longitude = -110.09824F,
                timezone = "America/Boise"
            )
        )

        // 3rd Object (Tuckahoe)
        currentList.add(
            LocationData(
                latitude = 32.819923F,
                longitude = -81.483925F,
                timezone = "America/New_York"
            )
        )

        // 4th Object (Poplarville)
        currentList.add(
            LocationData(
                latitude = 30.702417F,
                longitude = -89.447622F,
                timezone = "America/Chicago"
            )
        )

        // 5th Object (Utqiagk, AK)
        currentList.add(
            LocationData(
                latitude = 71.287F,
                longitude = -156.767F,
                timezone = "America/Anchorage"
            )
        )

        // Post the updated list to the LiveData. This notifies any observing Fragments.
        _locationList.value = currentList
    }

    private val _data = MutableLiveData<List<ForecastHourlyData>>().apply {
        value = emptyList<ForecastHourlyData>()
    }

    val data: LiveData<List<ForecastHourlyData>> = _data

    private val _suntime = MutableLiveData<String>()

    val suntime: LiveData<String> = _suntime


    //  Location name to go along with point forecast

    // 1. Mutable LiveData to hold the selected index (starts at 0)
    private val _locationIndex = MutableLiveData(0)
    val locationIndex: LiveData<Int> = _locationIndex

    // 2. Function to update the index from the Fragment
    fun setLocationIndex(index: Int) {
        _locationIndex.value = index
        // In a real app, you would likely fetch new data here using this index
        // e.g., fetchForecast(PointLocations.forecastUrls[index])
        Log.d(TAG, "setLocationIndex " + index)
    }

    /**
     * Retrieves a LocationData object at a specific index (0, 1, or 2).
     * @param index The position of the item (0, 1, or 2).
     * @return The LocationData object or null if the index is out of bounds.
     */
    fun getLocationAtIndex(index: Int): LocationData? {
        // 1. Get the current list value safely.
        val list = _locationList.value

        // 2. Check if the list is not null and the index is within the valid range.
        if (list != null && index >= 0 && index < list.size) {
            // 3. Return the object at the specified index.
            return list[index]
        }

        // 4. Return null if the list is null or the index is out of bounds.
        return null
    }

    // THIS LINE IS THE SOURCE OF TRUTH:
    val selectedForecastUrl: LiveData<String> = _locationIndex.map { index ->
        // 1. Ensure this line is correct:
        PointLocations.forecastUrls[index]
        Log.d("TAG", "url " + PointLocations.forecastUrls[index])

        // 2. We can make it safer by explicitly returning the value:
        return@map PointLocations.forecastUrls[index]
    }

    /**
     * NEW FUNCTION: Retrieves the LocationData object at the currently selected locationIndex.
     * @return The LocationData object at the current index, or null if the index is invalid or list is empty.
     */
    fun getCurrentLocationData(): LocationData? {
        // 1. Safely get the current Int value from the LiveData.
        val currentIndex = _locationIndex.value ?: return null // Return null if index LiveData is null

        // 2. Delegate the request to the existing function, passing the current index.
        return getLocationAtIndex(currentIndex)
    }

    // 1. LiveData to track loading status
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading // Expose as immutable LiveData

    //var latitude: Float = 33.656433F
    //val longitude: Float = -82.42646F
    fun getNwsHourlyForecast() {

        viewModelScope.launch {
            Log.d(TAG, "viewModelScope.launch")
            var errorMsg: String = "No Error"

            // --- START LOADING ---
            _isLoading.value = true

            try {
                val sunLoc = getCurrentLocationData()
                val lat = sunLoc?.latitude
                val lon = sunLoc?.longitude
                val tz = sunLoc?.timezone
                
                // val sunriseLocation = ForecastViewModel.getLocationAtIndex(locationIndex)
                val responsesun: Response<String> = SunApi.retrofitServiceSun.getSunriseTime(lat, lon, "today", tz)
                if (responsesun.isSuccessful){

                    Log.d(TAG, "responsesun " + responsesun.toString())
                    lateinit var resultJsonSun: JSONObject

                    resultJsonSun = JSONObject(responsesun.body())

                    Log.d(TAG, "Sunrise " + resultJsonSun.get("results"))
                    val results: JSONObject = resultJsonSun.get("results") as JSONObject
                    val sunriseTime = results.get("sunrise")
                    val sunsetTime = results.get("sunset")
                    _suntime.value = sunriseTime.toString().plus("     ").plus(sunsetTime)
                    Log.d(TAG, "_suntime.value     " + _suntime.value!!)

                }
            } catch (e: Exception) {
                //TODO("Not yet implemented")
                Log.e(TAG, e.toString())
                e.printStackTrace()
            }

            try {

                val url = selectedForecastUrl.value
                Log.d(TAG, "used selectedForecastUrl = " + url)
                if (url != null) {
                    val response: Response<String> = NwsApi.retrofitService.getHourlyForecast(url)

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
                        _data.value = makePeriods(periodsJson)

                    } else {
                        Log.d(TAG, "getNwsHourlyForecast() else")
                        val errorData = ForecastHourlyData()
                        errorData.number = 999
                        errorData.shortForecast = errorMsg
                        _data.value = _data.value?.plus(errorData)

                    }
                }

            } catch (e: Exception) {
                //Log.d(TAG, "error " + listResult)
                Log.e(TAG, e.toString())
                val errorData = ForecastHourlyData()
                errorData.number = 999
                errorData.shortForecast = e.message.toString()
                _data.value = _data.value?.plus(errorData)

            } finally {
                // --- STOP LOADING ---
                _isLoading.value = false
            }

            //Log.d(TAG, "response " + listResult)

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
            val relHum = forecast.getJSONObject("relativeHumidity")
            hourlyForecast.relativeHumidity = relHum.get("value").toString()
            val dewp = forecast.getJSONObject("dewpoint")
            hourlyForecast.dewpoint = dewp.get("value").toString()
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