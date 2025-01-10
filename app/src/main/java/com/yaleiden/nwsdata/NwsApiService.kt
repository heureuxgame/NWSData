package com.yaleiden.nwsdata

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


/*
How do I determine the gridpoint for my location?
You can retrieve the metadata for a given latitude/longitude
coordinate with the /points endpoint (https://api.weather.gov/points/{lat},{lon}).
use only 4 decimals in lat_lon
For example, St Louis would be
https://api.weather.gov/points/38.6270,-90.1994
enter that URL in a web browser and from the response get
https://api.weather.gov/gridpoints/LSX/94,73/forecast/ as your BASE_URL
from properties:  forecast
this is your gridpoint
 */

// Location Evans, GA
//private val BASE_URL = "https://api.weather.gov/gridpoints/CAE/15,40/forecast/"

// Location for Clay Hill, GA
var LOCATION = "Clay Hill, GA"
var BASE_URL = "https://api.weather.gov/gridpoints/CAE/14,41/forecast/"

val loc_array = arrayOf("Clay Hill, GA","Hawks Rest BTNF, WY")
val url_array = arrayOf("https://api.weather.gov/gridpoints/CAE/14,41/forecast/","https://api.weather.gov/gridpoints/RIW/67,165/forecast/")
// Location for Hawks Rest USFS Patrol Cabin, Bridger-Teton NF, WY
//const val LOCATION = "Hawks Rest BTNF, WY"
//private val BASE_URL = "https://api.weather.gov/gridpoints/RIW/67,165/forecast/"

//private val LAT_LON =        "38.6270,-90.1994"
//private val LAT_LON =        "33.5358,-82.1676"  //Local
//private val BASE_URL = "https://api.weather.gov/gridpoints/CAE/25,36/forecast"

//private val UA = System.getProperty("http.agent")  // Get android user agent.


val userAgentInterceptor = UserAgentInterceptor()

// OkHttpClient. Be conscious with the order
var okHttpClient = OkHttpClient()
    .newBuilder()
    .addInterceptor(userAgentInterceptor) // interceptor for adding User-Agent - recommended by NWS
    .build()

public var retrofit = Retrofit.Builder()
    .client(okHttpClient)
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(PointLocations.instance.getLoc())
    .build()

interface NwsApiService {
    @GET("hourly")
    suspend fun getHourlyForecast(): Response<String>

}

object NwsApi {

    val retrofitService: NwsApiService by lazy {
        retrofit.create(NwsApiService::class.java)
    }

}
