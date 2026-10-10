package com.misfit.relaunch.domain.model

data class CompatibilityStatus(
    val apiLevel: Int,
    val accessibilityEnabled: Boolean,
    val powerMenuApiAvailable: Boolean,
    val powerMenuApiRestricted: Boolean,
    val canAttemptPowerMenu: Boolean,
    val restrictionReason: RestrictionReason?
)

enum class RestrictionReason {
    ACCESSIBILITY_DISABLED,
    SIGNATURE_PERMISSION_REQUIRED,
    DEVICE_POLICY_BLOCKED,
    UNKNOWN
}