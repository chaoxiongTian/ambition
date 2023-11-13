package com.hero.base.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.hero.base.app

/**
 * it is used to hold key activity and collect statistics on activity.
 */
@SuppressLint("StaticFieldLeak")
object ActivityHelper {
    private const val TAG = "ActivityHelperLog"

    // resumed activity to easy use.
    private var resumedActivity: Activity? = null

    // record activity info use string list.
    private var activityRecord = mutableListOf<String>()
    private val register = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            com.hero.base.log.alogi(
                TAG, "onActivityCreated: ${recodeName(activity)}, savedInstanceState: $savedInstanceState"
            )
            activityRecord.add(recodeName(activity))
        }

        override fun onActivityStarted(activity: Activity) {
            com.hero.base.log.alogi(TAG, "onActivityStarted: ${recodeName(activity)}")
        }

        override fun onActivityResumed(activity: Activity) {
            com.hero.base.log.alogi(TAG, "onActivityResumed: ${recodeName(activity)}")
            resumedActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {
            com.hero.base.log.alogi(TAG, "onActivityPaused: ${recodeName(activity)}")
            if (resumedActivity == activity) {
                resumedActivity = null
            } else {
                com.hero.base.log.alogw(TAG, "topActivity is not record")
            }
        }

        override fun onActivityStopped(activity: Activity) {
            com.hero.base.log.alogi(TAG, "onActivityStopped: ${recodeName(activity)}")
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            com.hero.base.log.alogi(TAG, "onActivitySaveInstanceState: ${recodeName(activity)}")
        }

        override fun onActivityDestroyed(activity: Activity) {
            com.hero.base.log.alogi(TAG, "onActivityDestroyed: ${recodeName(activity)}")
            activityRecord.remove(recodeName(activity))
        }
    }

    fun register() {
        app().registerActivityLifecycleCallbacks(register)
    }
}

fun recodeName(activity: Activity?) = if (activity == null) {
    "null_activity"
} else if (activity.componentName == null) {
    "null_component"
} else {
    activity.componentName.className
}
