package com.hero.ambition.coretools

import android.app.Activity
import android.app.Application
import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.hero.base.app
import com.hero.base.ext.TAG
import com.hero.base.ext.debugToast
import com.hero.base.ext.sdCardPermission
import com.hero.base.log.ALog
import com.hero.base.log.alogi
import com.hero.base.manager.ActivityRecord
import com.permissionx.guolindev.PermissionX


/**
 * 归拢 app 部分事件的启动时机
 */
object AppBootstrap {

    /**
     * 应用刚启动的时候调用
     */
    fun applicationOnCreate(application: Application) {
        initLogger(application)
        alogi(TAG(), "applicationOnCreate")
        ActivityRecord.register()
        alogi(TAG(), "applicationOnCreate end")
    }

    private fun initLogger(context: Context) {
        ALog.init(context)
    }

    /**
     * 主页页刚启动的时候调用
     */
    fun mainActivityCreate(activity: Activity) {
        alogi(TAG(), "mainActivityCreate")
        requestReadPermission(activity)
        alogi(TAG(), "mainActivityCreate end")
    }

    private fun requestReadPermission(activity: Activity) {
        if (activity !is FragmentActivity) {
            return
        }
        PermissionX.init(activity)
            .permissions(activity.applicationContext.sdCardPermission())
            .request { allGranted, grantedList, deniedList ->
                val notice = "allGranted $allGranted, grantedList:$grantedList deniedList:$deniedList"
                alogi(TAG(), notice)
                debugToast(app(), notice)
            }
    }
}