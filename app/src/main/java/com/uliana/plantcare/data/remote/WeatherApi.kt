package com.uliana.plantcare.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("daily") daily: String = "temperature_2m_max,relative_humidity_2m_mean,wind_speed_10m_max",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}
