package com.hero.ambition.coretools

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.hero.ambition.coretools.log.ALog
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
        ALog.i( "AppBootstrap applicationOnCreate")

        ALog.i("AppBootstrap applicationOnCreate end")
    }

    private fun initLogger(context: Context) {
        ALog.init(context)
    }

    /**
     * 闪屏页刚启动的时候调用
     */
    fun splashOnCreate() {
        ALog.i("AppBootstrap splashOnCreate")
        ALog.i("AppBootstrap splashOnCreate end")
    }

    /**
     * 主页页刚启动的时候调用
     */
    fun mainActivityCreate(activity: Activity) {
        ALog.i("AppBootstrap mainActivityCreate")
        requestReadPermission(activity)
        ALog.i("AppBootstrap mainActivityCreate end")
    }

    private fun requestReadPermission(activity: Activity) {
        if(activity !is FragmentActivity){
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