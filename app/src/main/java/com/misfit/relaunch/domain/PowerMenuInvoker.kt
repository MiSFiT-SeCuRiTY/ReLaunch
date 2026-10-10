package com.misfit.relaunch.domain

import com.misfit.relaunch.domain.model.PowerMenuResult
import com.misfit.relaunch.service.PowerAccessibilityService

interface PowerMenuInvoker {
    suspend fun invoke(): PowerMenuResult
}

class PowerMenuInvokerImpl(
    private val checker: CompatibilityChecker
) : PowerMenuInvoker {

    override suspend fun invoke(): PowerMenuResult {
        val status = checker.check()

        if (!status.accessibilityEnabled) {
            return PowerMenuResult.Unavailable(
                reason = "Accessibility service is not enabled.",
                alternatives = listOf(
                    "Enable ReLaunch in Settings → Accessibility",
                    "Use the physical power button"
                )
            )
        }

        val service = PowerAccessibilityService.instance
            ?: return PowerMenuResult.Unavailable(
                reason = "Accessibility service is enabled but not running. " +
                        "Try toggling Accessibility off and on.",
                alternatives = listOf(
                    "Toggle Accessibility off and on in Settings",
                    "Use the physical power button"
                )
            )

        return service.requestPowerMenu()
    }
}