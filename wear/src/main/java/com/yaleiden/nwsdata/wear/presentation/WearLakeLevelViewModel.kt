package com.yaleiden.nwsdata.wear.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

//import com.yaleiden.nwsdata.wear.BuildConfig
//import com.yaleiden.nwsdata.BuildConfig
//import com.yaleiden.nwsdata.wear.BuildConfig as WearBuildConfig

// 1. Updated Success state to carry both the site name and the current level
sealed class WearUiState {
    object Loading : WearUiState()
    data class Success(val siteName: String, val latestLevel: String) : WearUiState()
    data class Error(val message: String) : WearUiState()
}

// Data class wrapper to safely pass the combined parsed result from the network thread
data class ParsedLakeData(val siteName: String, val level: String)

class WearLakeLevelViewModel : ViewModel() {

    private val TAG = "WearLakeViewModel"
    private val latestJsonUrl = "https://api.waterdata.usgs.gov/ogcapi/v0/collections/latest-continuous/items?monitoring_location_id=USGS-02193900&parameter_code=00062&f=json"

    // 1️⃣ Pull the API Key safely from the shared BuildConfig
    //private val apiKey = BuildConfig.WEATHER_API_KEY
    // Use the explicit alias to bypass Android Studio's import confusion
    //private val apiKey = WearBuildConfig.WEATHER_API_KEY
    private val _uiState = MutableStateFlow<WearUiState>(WearUiState.Loading)
    val uiState: StateFlow<WearUiState> = _uiState

    init {
        fetchLatestLevel()
    }

    fun fetchLatestLevel() {
        viewModelScope.launch {
            _uiState.value = WearUiState.Loading
            try {
                val result = withContext(Dispatchers.IO) {
                    downloadAndParseLatestJson(latestJsonUrl)
                }
                _uiState.value = WearUiState.Success(
                    siteName = result.siteName,
                    latestLevel = result.level
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching stream payload data", e)
                _uiState.value = WearUiState.Error(e.localizedMessage ?: "Unknown Error")
            }
        }
    }

    private fun downloadAndParseLatestJson(urlString: String): ParsedLakeData {
        // 2️⃣ Combine the target endpoint with your query parameter seamlessly
        //val completeUrlString = "$urlString&api_key=$apiKey"
        //Log.d(TAG, "Target WearOS JSON Request: $completeUrlString")

        // 3️⃣ Open the connection using the complete authenticated string
        //val url = URL(completeUrlString)
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Latest Level API returned HTTP ${connection.responseCode}")
        }

        val reader = BufferedReader(InputStreamReader(connection.inputStream))
        val responseBuilder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            responseBuilder.append(line)
        }
        reader.close()

        val jsonObject = JSONObject(responseBuilder.toString())
        val featuresArray = jsonObject.getJSONArray("features")
        if (featuresArray.length() > 0) {
            val firstFeature = featuresArray.getJSONObject(0)
            val properties = firstFeature.getJSONObject("properties")

            // 👈 FIXED: Try both the GeoJSON dynamic key formats, fallback to a fallback if both fail
            val siteName = when {
                properties.has("location_name") -> properties.getString("location_name")
                properties.has("site_name") -> properties.getString("site_name")
                properties.has("monitoring_location_name") -> properties.getString("monitoring_location_name")

                else -> "Clarks Hill near Plumb Branch" // Hard fallback if metadata object is completely generic
            }

            val value = properties.getDouble("value")

            return ParsedLakeData(siteName = siteName, level = "$value ft")
        }
        return ParsedLakeData(siteName = "N/A", level = "N/A")
    }
}