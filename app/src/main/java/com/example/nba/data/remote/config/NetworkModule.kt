package com.example.nba.data.remote.config


import com.example.nba.BuildConfig
import com.example.nba.data.remote.api.BallDontLieApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.balldontlie.io/"

    @Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor {
        val apiKey = BuildConfig.BALLDONTLIE_API_KEY

        check(apiKey.isNotBlank()) {
            "BALLDONTLIE_API_KEY não foi configurada"
        }

        return Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .header("Authorization", apiKey)
                .build()

            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTeamsApi(
        retrofit: Retrofit
    ): BallDontLieApi {
        return retrofit.create(BallDontLieApi::class.java)
    }
}
