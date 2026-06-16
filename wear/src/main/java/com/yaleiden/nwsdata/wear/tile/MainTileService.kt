package com.yaleiden.nwsdata.wear.tile

//import android.R.attr.theme
import android.content.Context
import android.util.Log
import androidx.wear.protolayout.ColorBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.material3.Typography
//import androidx.wear.protolayout.material3.edgeContentLayout
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
//import androidx.wear.protolayout.material3.layoutColor
import androidx.wear.protolayout.LayoutElementBuilders.Box
import androidx.wear.protolayout.ModifiersBuilders.Modifiers
import androidx.wear.protolayout.ModifiersBuilders.Padding
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.guava.future
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

private const val RESOURCES_VERSION = "1"
private const val TAG = "MainTileService"

class MainTileService : TileService() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private val latestJsonUrl = "https://api.waterdata.usgs.gov/ogcapi/v0/collections/latest-continuous/items?monitoring_location_id=USGS-02193900&parameter_code=00062&f=json"

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest): ListenableFuture<TileBuilders.Tile> {
        // Tiles can perform an asynchronous background task if we wrap a Coroutine in a ListenableFuture
        return serviceScope.future {
            val tileData = try {
                downloadAndParseLatestJson(latestJsonUrl)
            } catch (e: Exception) {
                Log.e(TAG, "Tile failed to fetch network payload", e)
                TileData(siteName = "Error Loading", level = "⚠️ Failed")
            }

            buildTile(requestParams, this@MainTileService, tileData)
        }
    }

    override fun onTileResourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ListenableFuture<Resources> =
        Futures.immediateFuture(
            Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build()
        )

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    private fun downloadAndParseLatestJson(urlString: String): TileData {
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

            val siteName = when {
                properties.has("location_name") -> properties.getString("location_name")
                properties.has("site_name") -> properties.getString("site_name")
                properties.has("monitoring_location_name") -> properties.getString("monitoring_location_name")
                else -> "Clarks Hill near Plumb Branch"
            }

            val value = properties.getDouble("value")
            return TileData(siteName = siteName, level = "$value ft")
        }
        return TileData(siteName = "N/A", level = "N/A")
    }
}

// Data holder class specific to Tile state requirements
data class TileData(val siteName: String, val level: String)

//import androidx.wear.protolayout.material3.LayoutColor // Ensure this import is added

private fun buildTile(
    requestParams: RequestBuilders.TileRequest,
    context: Context,
    data: TileData
): TileBuilders.Tile {
    return TileBuilders.Tile.Builder()
        .setResourcesVersion(RESOURCES_VERSION)
        // Hours * Minutes * Seconds * Milliseconds
        .setFreshnessIntervalMillis(2 * 60 * 60 * 1000)
        .setTileTimeline(
            TimelineBuilders.Timeline.fromLayoutElement(
                materialScope(context, requestParams.deviceConfiguration) {
                    primaryLayout(
                        titleSlot = {
                            // Wrap the Title in a Box to apply specific Top and Horizontal Padding
                            Box.Builder()
                                .setModifiers(
                                    Modifiers.Builder()
                                        .setPadding(
                                            Padding.Builder()
                                                .setTop(dp(0f))       // Spacing from screen edge
                                                .setStart(dp(16f))     // Left / Start padding
                                                .setEnd(dp(16f))       // Right / End padding
                                                .build()
                                        )
                                        .build()
                                )
                                .addContent(
                                    // Use explicit Text.Builder to safely configure layout wrapping and alignment
                                    LayoutElementBuilders.Text.Builder()
                                        .setText(data.siteName)
                                        .setMaxLines(3) // Prevents clipping long names like "Clarks Hill near Plumb Branch"
                                        .setMultilineAlignment(LayoutElementBuilders.TEXT_ALIGN_CENTER)
                                        .setModifiers(
                                            Modifiers.Builder()

                                                // Pass your colorScheme typography/color style mappings here
                                                .build()
                                        )
                                        .build()
                                )
                                .build()
                        },
                        mainSlot = {
                            // Large Lake Level text (Using your colorScheme mapping)
                            text(
                                data.level.layoutString,
                                typography = Typography.DISPLAY_MEDIUM,
                                color = colorScheme.primary
                            )
                        },
                        bottomSlot = {
                            // Wrap the Status Text in a Box to apply specific Bottom Padding
                            Box.Builder()
                                .setModifiers(
                                    Modifiers.Builder()
                                        .setPadding(
                                            Padding.Builder()
                                                .setBottom(dp(12f))
                                                .build()
                                        )
                                        .build()
                                )
                                .addContent(
                                    text(
                                        "Auto-updates active".layoutString,
                                        typography = Typography.LABEL_SMALL,
                                        color = colorScheme.primary
                                    )
                                )
                                .build()
                        }
                    )
                }
            )
        )
        .build()
}

@Preview(device = WearDevices.SMALL_ROUND)
@Preview(device = WearDevices.LARGE_ROUND)
fun tilePreview(context: Context) = TilePreviewData({
    Resources.Builder().setVersion(RESOURCES_VERSION).build()
}) {
    buildTile(
        it,
        context,
        TileData(
            siteName = "Clarks Hill near Plumb Branch",
            level = "324.58 ft"
        )
    )
}