package com.yaleiden.nwsdata

import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

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
    @GET("json")
    suspend fun getSunriseTime(
        @Query("lat") latitude: Float?,
        @Query("lng") longitude: Float?,
        @Query("date") date: String = "today",
        @Query("tzid") timezone: String? //"America/New_York" "America/Boise"
    ): Response<String>

}

object SunApi {
    val retrofitServiceSun: SunApiService by lazy {
        retrofitsun.create(SunApiService::class.java)
    }
}