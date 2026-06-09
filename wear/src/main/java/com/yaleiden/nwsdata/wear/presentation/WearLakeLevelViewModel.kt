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

sealed class WearUiState {
    object Loading : WearUiState()
    data class Success(val latestLevel: String) : WearUiState()
    data class Error(val message: String) : WearUiState()
}

class WearLakeLevelViewModel : ViewModel() {

    private val TAG = "WearLakeViewModel"
    private val latestJsonUrl = "https://api.waterdata.usgs.gov/ogcapi/v0/collections/latest-continuous/items?monitoring_location_id=USGS-02193900&parameter_code=00062&f=json"

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
                _uiState.value = WearUiState.Success(result)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching stream payload data", e)
                _uiState.value = WearUiState.Error(e.localizedMessage ?: "Unknown Error")
            }
        }
    }

    private fun downloadAndParseLatestJson(urlString: String): String {
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
            val value = properties.getDouble("value")
            return "$value ft"
        }
        return "N/A"
    }
}