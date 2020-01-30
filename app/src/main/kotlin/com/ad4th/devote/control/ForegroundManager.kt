package com.ad4th.devote.control

import android.app.Activity
import android.app.Application
import android.os.Bundle

class ForegroundManager private constructor() : Application.ActivityLifecycleCallbacks {

    var appStatus: AppStatus? = null
        private set

    // check if app is return foreground
    val isBackground: Boolean
        get() = appStatus!!.ordinal == AppStatus.BACKGROUND.ordinal


    // running activity count
    private var running = 0


    enum class AppStatus {
        BACKGROUND, // app is background
        RETURNED_TO_FOREGROUND, // app returned to foreground(or first launch)
        FOREGROUND
        // app is foreground
    }

    override fun onActivityCreated(activity: Activity?, bundle: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        if (++running == 1) {
            appStatus = AppStatus.RETURNED_TO_FOREGROUND
        } else if (running > 1) {
            appStatus = AppStatus.FOREGROUND
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {
        if (--running == 0) {
            appStatus = AppStatus.BACKGROUND
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    companion object {

        private var instance: ForegroundManager? = null

        fun init(app: Application) : ForegroundManager{
            if (instance == null) {
                instance = ForegroundManager()
                app.registerActivityLifecycleCallbacks(instance)
            }
            return instance!!
        }

        fun get(): ForegroundManager? {
            return instance
        }

    }

}