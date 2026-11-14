package com.example.nba.data.remote

import com.example.nba.BuildConfig
import com.example.nba.data.remote.api.BallDontLieApi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {
    private const val BASE_URL = "https://api.balldontlie.io/"

    fun createApi(): BallDontLieApi {
        val authInterceptor = Interceptor { chain ->
            val request = chain.request().newBuilder()
                .apply {
                    if (BuildConfig.BALLDONTLIE_API_KEY.isNotBlank()) {
                        header("Authorization", BuildConfig.BALLDONTLIE_API_KEY)
                    }
                }
                .build()
            chain.proceed(request)
        }

        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BallDontLieApi::class.java)
    }
}
