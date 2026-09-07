package com.uliana.plantcare.data.repository

import com.uliana.plantcare.data.remote.WeatherApi
import com.uliana.plantcare.data.remote.WeatherOutlook
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.min

class WeatherRepository {

    private val api: WeatherApi by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }

    suspend fun getOutlook(latitude: Double, longitude: Double, days: Int): WeatherOutlook? {
        return try {
            val response = api.getForecast(latitude, longitude)
            val n = min(days, response.daily.time.size)
            if (n <= 0) return null
            WeatherOutlook(
                avgTempMax = response.daily.tempMax.take(n).average(),
                avgHumidity = response.daily.humidityMean.take(n).average(),
                avgWindSpeed = response.daily.windSpeedMax.take(n).average()
            )
        } catch (e: Exception) {
            null
        }
    }
}
