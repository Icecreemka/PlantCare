package com.uliana.plantcare.domain

import com.uliana.plantcare.data.local.entity.PlantCategory
import com.uliana.plantcare.data.remote.WeatherOutlook
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.pow

object WateringCalculator {

    private const val REFERENCE_VOLUME_ML = 1000.0
    private const val REFERENCE_INTERVAL_DAYS = 7.0

    private const val MIN_INTERVAL_DAYS = 1.0
    private const val MAX_INTERVAL_DAYS = 45.0

    private const val MIN_ADJUSTMENT_FACTOR = 0.4
    private const val MAX_ADJUSTMENT_FACTOR = 2.5
    private const val FEEDBACK_STEP = 0.12

    fun potVolumeMl(diameterCm: Double, heightCm: Double): Double {
        val radiusCm = diameterCm / 2.0

        val cylinderVolumeCm3 = PI * radiusCm.pow(2) * heightCm
        return cylinderVolumeCm3 * 0.75
    }

    fun calculateBaseIntervalDays(diameterCm: Double, heightCm: Double, category: PlantCategory): Double {
        val volumeMl = potVolumeMl(diameterCm, heightCm)

        val volumeFactor = (volumeMl / REFERENCE_VOLUME_ML).pow(0.5)
        val raw = REFERENCE_INTERVAL_DAYS * volumeFactor / category.baseEvaporationCoefficient
        return raw.coerceIn(MIN_INTERVAL_DAYS, MAX_INTERVAL_DAYS)
    }

    fun weatherFactor(outlook: WeatherOutlook): Double {
        val tempEffect = 1.0 - (outlook.avgTempMax - 20.0) * 0.02
        val humidityEffect = 1.0 + (outlook.avgHumidity - 60.0) * 0.01
        val windEffect = 1.0 - (outlook.avgWindSpeed - 10.0) * 0.015
        val combined = tempEffect * humidityEffect * windEffect
        return combined.coerceIn(0.5, 1.6)
    }

    fun applyWeather(currentIntervalDays: Double, outlook: WeatherOutlook?): Double {
        if (outlook == null) return currentIntervalDays
        val factor = weatherFactor(outlook)
        return (currentIntervalDays * factor).coerceIn(MIN_INTERVAL_DAYS, MAX_INTERVAL_DAYS)
    }

    fun updateAdjustmentFactor(currentFactor: Double, driedEarly: Boolean): Double {
        val delta = if (driedEarly) -FEEDBACK_STEP else FEEDBACK_STEP
        return (currentFactor + delta).coerceIn(MIN_ADJUSTMENT_FACTOR, MAX_ADJUSTMENT_FACTOR)
    }

    fun effectiveIntervalDays(baseIntervalDays: Double, adjustmentFactor: Double, outlook: WeatherOutlook?): Double {
        val afterFeedback = baseIntervalDays * adjustmentFactor
        return applyWeather(afterFeedback, outlook).let { max(it, MIN_INTERVAL_DAYS) }
    }
}
