package com.yaleiden.nwsdata.wear.complication

import android.util.Log
import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "MainComplication"

class MainComplicationService : SuspendingComplicationDataSourceService() {

    private val latestJsonUrl = "https://api.waterdata.usgs.gov/ogcapi/v0/collections/latest-continuous/items?monitoring_location_id=USGS-02193900&parameter_code=00062&f=json"

    // The data shown when a user is picking a complication in the watch face customizer
    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        if (type != ComplicationType.SHORT_TEXT) {
            return null
        }
        return createComplicationData("324.5'", "Lake level placeholder")
    }

    // Triggered automatically by Wear OS when it wants fresh data
    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData {
        if (request.complicationType != ComplicationType.SHORT_TEXT) {
            return null!! // Return an empty/unsupported type enforcement if not SHORT_TEXT
        }

        // Run the network download safely off the main UI thread
        val levelText = withContext(Dispatchers.IO) {
            try {
                downloadAndParseLatestLevel(latestJsonUrl)
            } catch (e: Exception) {
                Log.e(TAG, "Complication network payload failure", e)
                "⚠️" // Fallback error icon string if network is timed out/offline
            }
        }

        return createComplicationData(levelText, "Lake level reading is $levelText")
    }

    private fun downloadAndParseLatestLevel(urlString: String): String {
        val url = URL(urlString)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw Exception("API HTTP ${connection.responseCode}")
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

            if (properties.has("value")) {
                val value = properties.getDouble("value")
                // Return shorthand string format suitable for a tiny round complication slot (e.g., 324.6')
                return "%.1f'".format(value)
            }
        }
        return "N/A"
    }

    private fun createComplicationData(text: String, contentDescription: String) =
        ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder(contentDescription).build()
        ).build()
}