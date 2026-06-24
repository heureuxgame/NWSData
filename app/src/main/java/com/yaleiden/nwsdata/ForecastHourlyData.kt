package com.yaleiden.nwsdata

/**
 * Clean data class representation for an hourly weather forecast period.
 * * Using the 'data' keyword automatically generates structural [equals],
 * [hashCode], and [toString] methods based on these fields, instantly
 * fixing the ListAdapter DiffUtil warnings.
 */
data class ForecastHourlyData(
    var number: Int = 0,
    var name: String = "null",
    var startTime: String = "null",
    var endTime: String = "null",
    var isDaytime: Boolean = true,
    var temperature: Int = 111,
    var temperatureUnit: String = "null",
    var temperatureTrend: String = "null",
    var windSpeed: String = "null",
    var windDirection: String = "null",
    var icon: String = "null",
    var shortForecast: String = "null",
    var probabilityOfPrecipitation: Int = 100,
    var relativeHumidity: String = "null",
    var dewpoint: String = "null"
)