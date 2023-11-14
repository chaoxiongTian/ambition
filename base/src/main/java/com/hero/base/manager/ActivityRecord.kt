package com.hero.base.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.hero.base.app
import com.hero.base.ext.TAG
import com.hero.base.log.alogi
import com.hero.base.log.alogw

/**
 * it is used to hold key activity and collect statistics on activity.
 */
@SuppressLint("StaticFieldLeak")
object ActivityRecord {

    // resumed activity to easy use.
    private var resumedActivity: Activity? = null

    // record activity info use string list.
    private var createActivityRecord = ArrayDeque<String>()
    private var startActivityRecord = ArrayDeque<String>()
    var fragmentRecord = ArrayDeque<Pair<String, String>>()

    private val register = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            alogi(
                TAG(), "onActivityCreated: ${recodeName(activity)}, savedInstanceState: $savedInstanceState"
            )
            createActivityRecord.addFirst(recodeName(activity))
            if (activity !is FragmentActivity) {
                return
            }

        }

        override fun onActivityStarted(activity: Activity) {
            alogi(TAG(), "onActivityStarted: ${recodeName(activity)}")
            startActivityRecord.addFirst(recodeName(activity))
        }

        override fun onActivityResumed(activity: Activity) {
            alogi(TAG(), "onActivityResumed: ${recodeName(activity)}")
            resumedActivity = activity
        }

        override fun onActivityPaused(activity: Activity) {
            alogi(TAG(), "onActivityPaused: ${recodeName(activity)}")
            if (resumedActivity == activity) {
                resumedActivity = null
            } else {
                alogw(TAG(), "topActivity is not record")
            }
        }

        override fun onActivityStopped(activity: Activity) {
            alogi(TAG(), "onActivityStopped: ${recodeName(activity)}")
            if (startActivityRecord.first() == recodeName(activity)) {
                startActivityRecord.removeFirst()
            } else {
                alogw(TAG(), "true is ${startActivityRecord.first()}")
            }
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            alogi(TAG(), "onActivitySaveInstanceState: ${recodeName(activity)}")
        }

        override fun onActivityDestroyed(activity: Activity) {
            alogi(TAG(), "onActivityDestroyed: ${recodeName(activity)}")
            if (createActivityRecord.first() == recodeName(activity)) {
                createActivityRecord.removeFirst()
            } else {
                alogw(TAG(), "true is ${createActivityRecord.first()}")
            }
        }
    }

    fun register() {
        app().registerActivityLifecycleCallbacks(register)
    }

    fun createActivityInfo(): String {
        var result = ""
        createActivityRecord.forEach {
            result += "$it"
        }
        return result
    }

    fun startActivityInfo(): String {
        var result = ""
        startActivityRecord.forEach {
            result += "$it"
        }
        return result
    }
}

fun recodeName(activity: Activity?) = if (activity == null) {
    "null_activity"
} else if (activity.componentName == null) {
    "null_component"
} else {
    activity.componentName.className
}
