package com.misfit.relaunch.service

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.misfit.relaunch.domain.model.PowerMenuResult

class PowerAccessibilityService : AccessibilityService() {

    companion object {
        private const val TAG = "PowerAccessibility"

        @Volatile
        var instance: PowerAccessibilityService? = null
            private set

        @Volatile
        var isRunning: Boolean = false
            private set
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        isRunning = true
        Log.i(TAG, "Accessibility service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Command-driven service — no event handling required.
    }

    override fun onInterrupt() {
        // No ongoing feedback to interrupt.
    }

    override fun onDestroy() {
        instance = null
        isRunning = false
        Log.i(TAG, "Accessibility service destroyed")
        super.onDestroy()
    }

    fun requestPowerMenu(): PowerMenuResult {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return PowerMenuResult.Unavailable(
                reason = "Power menu API requires Android 11 or later. " +
                        "This device runs Android ${Build.VERSION.RELEASE}.",
                alternatives = listOf(
                    "Use the physical power button",
                    "Check your manufacturer's power shortcuts"
                )
            )
        }

        return try {
            val ok = performGlobalAction(GLOBAL_ACTION_POWER_DIALOG)
            if (ok) {
                PowerMenuResult.Success(
                    "Power menu request sent. If nothing appeared, your device " +
                            "manufacturer may block this."
                )
            } else {
                PowerMenuResult.Unavailable(
                    reason = "The system rejected the power menu request. " +
                            "Your device manufacturer may block accessibility " +
                            "services from opening it.",
                    alternatives = listOf(
                        "Use the physical power button",
                        "Check Device Settings → Accessibility for restrictions"
                    )
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to perform power dialog action", e)
            PowerMenuResult.Error("Unexpected error: ${e.message ?: "unknown"}")
        }
    }
}