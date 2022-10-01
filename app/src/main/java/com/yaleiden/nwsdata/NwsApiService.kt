package com.yaleiden.nwsdata

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
    private val BASE_URL = "https://api.weather.gov/gridpoints/CAE/15,40/forecast/"
    //private val LAT_LON =        "38.6270,-90.1994"

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    interface NwsApiService {
        @GET("hourly")
        suspend fun getHourlyForecast(): Response<String>
        //suspend fun getHourlyForecast(): Response<ForecastHourlyData>
    }

    object NwsApi {
        val retrofitService : NwsApiService by lazy {
            retrofit.create(NwsApiService::class.java)
        }
    }
