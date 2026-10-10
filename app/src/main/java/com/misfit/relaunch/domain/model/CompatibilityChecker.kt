package com.misfit.relaunch.domain

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.misfit.relaunch.domain.model.CompatibilityStatus
import com.misfit.relaunch.domain.model.RestrictionReason
import com.misfit.relaunch.service.PowerAccessibilityService

class CompatibilityChecker(private val context: Context) {

    fun check(): CompatibilityStatus {
        val apiLevel = Build.VERSION.SDK_INT
        val accessibilityEnabled = isAccessibilityServiceEnabled()
        val powerMenuApiAvailable = apiLevel >= Build.VERSION_CODES.R
        val powerMenuApiRestricted =
            powerMenuApiAvailable && !hasShowPowerMenuPermission()

        return CompatibilityStatus(
            apiLevel = apiLevel,
            accessibilityEnabled = accessibilityEnabled,
            powerMenuApiAvailable = powerMenuApiAvailable,
            powerMenuApiRestricted = powerMenuApiRestricted,
            canAttemptPowerMenu = accessibilityEnabled,
            restrictionReason = when {
                !accessibilityEnabled -> RestrictionReason.ACCESSIBILITY_DISABLED
                powerMenuApiRestricted -> RestrictionReason.SIGNATURE_PERMISSION_REQUIRED
                else -> null
            }
        )
    }

    fun isAccessibilityServiceEnabled(): Boolean {
        val serviceName =
            "${context.packageName}/${PowerAccessibilityService::class.java.name}"
        val enabled = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.split(':').any { it.equals(serviceName, ignoreCase = true) }
    }

    private fun hasShowPowerMenuPermission(): Boolean =
        context.checkSelfPermission("android.permission.SHOW_POWER_MENU") ==
                PackageManager.PERMISSION_GRANTED

    /**
     * Returns an Intent that opens the Accessibility settings screen.
     * Works on API 26+ across all major manufacturers.
     */
    fun accessibilitySettingsIntent(): Intent {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return intent
    }
}