package com.yaleiden.nwsdata

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class UserAgentInterceptor : Interceptor {

    /**
     * Recommended by NWS to have user agent when making API calls
     */
    val useragent = "Fish WX App, email address"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originRequest: Request = chain.request()
        val requestWithUserAgent: Request = originRequest.newBuilder()
            .removeHeader("User-Agent")
            .header("User-Agent", useragent)
            .build()
        return chain.proceed(requestWithUserAgent)
    }


}