package com.hero.ambition.coretools

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.hero.base.ext.TAG
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
            .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    Toast.makeText(activity, "All permissions are granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                }
            }
    }

}