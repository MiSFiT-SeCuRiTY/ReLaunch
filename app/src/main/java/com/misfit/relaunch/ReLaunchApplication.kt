package com.misfit.relaunch

import android.app.Application
import com.misfit.relaunch.domain.CompatibilityChecker
import com.misfit.relaunch.domain.PowerMenuInvoker
import com.misfit.relaunch.domain.PowerMenuInvokerImpl

class ReLaunchApplication : Application() {

    lateinit var compatibilityChecker: CompatibilityChecker
        private set

    lateinit var powerMenuInvoker: PowerMenuInvoker
        private set

    override fun onCreate() {
        super.onCreate()
        compatibilityChecker = CompatibilityChecker(this)
        powerMenuInvoker = PowerMenuInvokerImpl(compatibilityChecker)
    }
}