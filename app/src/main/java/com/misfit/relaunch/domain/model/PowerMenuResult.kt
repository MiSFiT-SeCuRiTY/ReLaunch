package com.misfit.relaunch.domain.model

sealed class PowerMenuResult {
    data class Success(val message: String) : PowerMenuResult()
    data class Unavailable(val reason: String, val alternatives: List<String>) : PowerMenuResult()
    data class Error(val message: String) : PowerMenuResult()
}