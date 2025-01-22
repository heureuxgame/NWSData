package com.yaleiden.nwsdata

import android.util.Log
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.time.LocalTime
import java.time.format.DateTimeFormatter
/*
data class SunriseSunsetResponse(
    @SerializedName("results")
    val results: Results
)

data class Results(
    @SerializedName("sunrise")
    val sunrise: String,
    @SerializedName("sunset")
    val sunset: String
)
*/
val latitude: Float = 37.7749F
val longitude: Float = -122.4194F
val base_url = "https://api.sunrise-sunset.org/"


// OkHttpClient. Be conscious with the order
var okHttpClientSun = OkHttpClient()
    .newBuilder()
    //.addInterceptor(userAgentInterceptor) // interceptor for adding User-Agent - recommended by NWS
    .build()

private val retrofitsun = Retrofit.Builder()
    .client(okHttpClientSun)
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(base_url)

    //.baseUrl(pointLocations.points[NwsApi.listLocation])
    .build()
// 33.656435, -82.426461 Little River off Winfield CG boat ramp
interface SunApiService {
    @GET("json?lat=33.656435F&lng=-82.426461F&date=today&tzid=America/New_York")
    suspend fun getSuniseTime(): Response<String>

}

object SunApi {
    val retrofitServiceSun: SunApiService by lazy {
        retrofitsun.create(SunApiService::class.java)
    }
}