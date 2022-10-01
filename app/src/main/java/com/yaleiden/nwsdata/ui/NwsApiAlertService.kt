package com.yaleiden.nwsdata.ui

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


private val BASE_URL = "https://api.weather.gov/alerts/active?area=GA"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface NwsApiAlertService {

    @GET("alerts")
    suspend fun getAlerts(): Response<String>
    //suspend fun getHourlyForecast(): Response<ForecastHourlyData>
}

object NwsAlertApi {
    val retrofitService : NwsApiAlertService by lazy {
        retrofit.create(NwsApiAlertService::class.java)
    }
}
