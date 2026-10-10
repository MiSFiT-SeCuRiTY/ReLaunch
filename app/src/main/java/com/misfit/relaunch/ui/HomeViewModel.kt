package com.misfit.relaunch.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.misfit.relaunch.ReLaunchApplication
import com.misfit.relaunch.domain.model.CompatibilityStatus
import com.misfit.relaunch.domain.model.PowerMenuResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val compatibility: CompatibilityStatus? = null,
    val isInvoking: Boolean = false,
    val lastResult: PowerMenuResult? = null,
    val showDiagnostics: Boolean = false
)

class HomeViewModel(private val app: ReLaunchApplication) : AndroidViewModel(app) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshStatus()
    }

    fun refreshStatus() {
        _uiState.value = _uiState.value.copy(
            compatibility = app.compatibilityChecker.check()
        )
    }

    fun invokePowerMenu() {
        if (_uiState.value.isInvoking) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isInvoking = true,
                lastResult = null
            )

            val result = app.powerMenuInvoker.invoke()

            _uiState.value = _uiState.value.copy(
                isInvoking = false,
                lastResult = result,
                compatibility = app.compatibilityChecker.check()
            )
        }
    }

    fun toggleDiagnostics() {
        _uiState.value = _uiState.value.copy(
            showDiagnostics = !_uiState.value.showDiagnostics
        )
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(lastResult = null)
    }

    fun accessibilitySettingsIntent(): Intent =
        app.compatibilityChecker.accessibilitySettingsIntent()

    class Factory(private val app: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(app as ReLaunchApplication) as T
        }
    }
}