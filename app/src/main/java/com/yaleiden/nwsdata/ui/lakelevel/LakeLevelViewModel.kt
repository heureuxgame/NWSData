package com.yaleiden.nwsdata.ui.lakelevel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yaleiden.nwsdata.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class WaterRecord(val date: String, val value: String)

data class LakeUiData(
    val siteName: String,
    val maxList: List<WaterRecord>,
    val minList: List<WaterRecord>,
    val meanList: List<WaterRecord>
)

class LakeLevelViewModel : ViewModel() {

    private val TAG = "LakeLevelViewModel"

    private val _uiState = MutableLiveData<LakeUiData?>()
    val uiState: LiveData<LakeUiData?> = _uiState

    private val _latestLevel = MutableLiveData<String>()
    val latestLevel: LiveData<String> = _latestLevel

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // API Keys and Target Base URLs stay contained in the logic layer
    private val apiKey = BuildConfig.WEATHER_API_KEY
    private val BASE_XML_URL = "https://waterservices.usgs.gov/nwis/dv/?site=02193900&format=waterml,1.1&ParameterCd=00062&"
    private val LATEST_JSON_URL = "https://api.waterdata.usgs.gov/ogcapi/v0/collections/latest-continuous/items?monitoring_location_id=USGS-02193900&parameter_code=00062&f=json"

    /**
     * Compute required dates internally within the data layout layer
     */
    private fun generateHistoricalUrl(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val calendar = Calendar.getInstance()
        val todayStr = dateFormat.format(calendar.time)

        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val sevenDaysAgoStr = dateFormat.format(calendar.time)

        return "${BASE_XML_URL}startDT=${sevenDaysAgoStr}&endDT=${todayStr}"
    }

    /**
     * Public command execution trigger invoked cleanly by UI components
     */
    fun refreshLakeMetrics() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = ""
            try {
                val historicalXmlUrl = generateHistoricalUrl()
                Log.d(TAG, "Target XML API Request: $historicalXmlUrl")

                // Concurrently pull data in IO threads
                val historicalResult = withContext(Dispatchers.IO) {
                    downloadAndParseXml(historicalXmlUrl)
                }
                val latestValueResult = withContext(Dispatchers.IO) {
                    downloadAndParseLatestJson(LATEST_JSON_URL)
                }

                _uiState.value = historicalResult
                _latestLevel.value = latestValueResult
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching stream payload data", e)
                _errorMessage.value = "Failed to synchronize parameters: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun downloadAndParseXml(urlString: String): LakeUiData {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("Historical API returned HTTP ${connection.responseCode}")
        }
        return connection.inputStream.use { stream -> parseXml(stream) }
    }

    private fun parseXml(inputStream: InputStream): LakeUiData {
        val factory = XmlPullParserFactory.newInstance()
        factory.isNamespaceAware = true
        val parser = factory.newPullParser()
        parser.setInput(inputStream, null)

        var siteName = "Unknown Site"
        val maxRecords = mutableListOf<WaterRecord>()
        val minRecords = mutableListOf<WaterRecord>()
        val meanRecords = mutableListOf<WaterRecord>()

        var currentStatisticCode = ""
        var eventType = parser.eventType
        var inTimeSeries = false

        while (eventType != XmlPullParser.END_DOCUMENT) {
            val name = parser.name
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    if (name == "timeSeries") inTimeSeries = true
                    if (inTimeSeries) {
                        when (name) {
                            "siteName" -> siteName = parser.nextText()
                            "option" -> {
                                if (parser.getAttributeValue(null, "name") == "Statistic") {
                                    currentStatisticCode = parser.getAttributeValue(null, "optionCode") ?: ""
                                }
                            }
                            "value" -> {
                                val dateTimeAttr = parser.getAttributeValue(null, "dateTime") ?: ""
                                val dateStr = if (dateTimeAttr.contains("T")) dateTimeAttr.substringBefore("T") else dateTimeAttr
                                val valueStr = parser.nextText()
                                val record = WaterRecord(dateStr, valueStr)
                                when (currentStatisticCode) {
                                    "00001" -> maxRecords.add(record)
                                    "00002" -> minRecords.add(record)
                                    "00003" -> meanRecords.add(record)
                                }
                            }
                        }
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (name == "timeSeries") inTimeSeries = false
                }
            }
            eventType = parser.next()
        }
        return LakeUiData(siteName, maxRecords, minRecords, meanRecords)
    }

    private fun downloadAndParseLatestJson(urlString: String): String {
        // Optional placeholder: Append your apiKey query parameter or headers here safely if needed!

        // 1. Combine the base URL with your apiKey property securely
        val completeUrlString = "$urlString&api_key=$apiKey"
        //val url = URL(urlString)
        // 2. Use the new complete URL string to open the connection
        val url = URL(completeUrlString)

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