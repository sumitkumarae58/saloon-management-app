package com.example.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    @Volatile
    var applicationContext: android.content.Context? = null

    private var lastUsedBaseUrl: String = "http://10.0.2.2:8080/"
    private var apiServiceInstance: ApiService? = null

    fun getSavedBaseUrl(context: android.content.Context?): String {
        val ctx = context ?: applicationContext
        if (ctx == null) return lastUsedBaseUrl
        val prefs = ctx.getSharedPreferences("salon_network_prefs", android.content.Context.MODE_PRIVATE)
        return prefs.getString("backend_url", "http://10.0.2.2:8080/") ?: "http://10.0.2.2:8080/"
    }

    fun saveBaseUrl(context: android.content.Context, newUrl: String) {
        val formattedUrl = if (newUrl.endsWith("/")) newUrl else "$newUrl/"
        context.getSharedPreferences("salon_network_prefs", android.content.Context.MODE_PRIVATE)
            .edit()
            .putString("backend_url", formattedUrl)
            .apply()
        // Force rebuild on next request
        apiServiceInstance = null
    }

    private var authToken: String? = null

    /**
     * Set/Update JWT token securely when user successfully registers or logs in.
     */
    fun setAuthToken(token: String?) {
        authToken = token
    }

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            
            // Append JWT token dynamically if logged in
            authToken?.let { token ->
                requestBuilder.header("Authorization", "Bearer $token")
            }
            
            chain.proceed(requestBuilder.build())
        }
        .build()

    val apiService: ApiService
        get() {
            val currentUrl = getSavedBaseUrl(applicationContext)
            if (apiServiceInstance == null || lastUsedBaseUrl != currentUrl) {
                lastUsedBaseUrl = currentUrl
                apiServiceInstance = Retrofit.Builder()
                    .baseUrl(currentUrl)
                    .client(okHttpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(ApiService::class.java)
            }
            return apiServiceInstance!!
        }
}
