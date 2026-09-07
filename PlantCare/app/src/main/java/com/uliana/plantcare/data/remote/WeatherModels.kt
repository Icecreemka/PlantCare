package com.uliana.plantcare.data.remote

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("daily") val daily: DailyData
)

data class DailyData(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m_max") val tempMax: List<Double>,
    @SerializedName("relative_humidity_2m_mean") val humidityMean: List<Double>,
    @SerializedName("wind_speed_10m_max") val windSpeedMax: List<Double>
)

data class WeatherOutlook(
    val avgTempMax: Double,
    val avgHumidity: Double,
    val avgWindSpeed: Double
)
