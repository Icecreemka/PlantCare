package com.uliana.plantcare.ui

import androidx.compose.runtime.mutableStateOf

object LocationHolder {
    private val state = mutableStateOf<Pair<Double, Double>?>(null)

    var current: Pair<Double, Double>?
        get() = state.value
        set(value) { state.value = value }
}
