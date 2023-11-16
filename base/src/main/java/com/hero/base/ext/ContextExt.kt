package com.hero.base.ext

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.DimenRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import java.io.File

inline val Context.screenWidth get() = resources.displayMetrics.widthPixels
inline val Context.screenHeight get() = resources.displayMetrics.heightPixels

inline val Context.isNetworkAvailable: Boolean
    get() {
        val activeNetworkInfo = connectivityManager?.activeNetworkInfo
        return activeNetworkInfo?.isConnectedOrConnecting ?: false
    }

fun Context.dp2px(value: Int): Int = (value * resources.displayMetrics.density).toInt()
fun Context.dp2px(value: Float): Int = (value * resources.displayMetrics.density).toInt()
fun Context.sp2px(value: Int): Int = (value * resources.displayMetrics.scaledDensity).toInt()
fun Context.sp2px(value: Float): Int = (value * resources.displayMetrics.scaledDensity).toInt()
fun Context.px2dp(px: Int): Float = px.toFloat() / resources.displayMetrics.density
fun Context.px2sp(px: Int): Float = px.toFloat() / resources.displayMetrics.scaledDensity
fun Context.dimen2px(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)
fun Context.string(@StringRes id: Int): String = getString(id)
fun Context.color(@ColorRes id: Int): Int = resources.getColor(id)
fun Context.inflateLayout(@LayoutRes layoutId: Int, parent: ViewGroup? = null, attachToRoot: Boolean = false): View =
    LayoutInflater.from(this).inflate(layoutId, parent, attachToRoot)

/**
 * 获取当前app的版本号
 */
fun Context.getAppVersion(): String {
    val manager = applicationContext.packageManager
    try {
        val info = manager.getPackageInfo(applicationContext.packageName, 0)
        if (info != null)
            return info.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return ""
}

fun Context.getAppVersionCode(): Long {
    val manager = applicationContext.packageManager
    try {
        val info = manager.getPackageInfo(applicationContext.packageName, 0)
        if (info != null) {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                info.longVersionCode
            } else {
                info.versionCode.toLong()
            }
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return 0L
}

fun Context.targetSDk() = try {
    packageManager.getApplicationInfo(this.packageName, 0).targetSdkVersion
} catch (e: PackageManager.NameNotFoundException) {
    e.printStackTrace()
    0
}


data class ExtAppInfo(
    val apkPath: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val appName: String,
    val icon: Drawable
)

fun Context.getAppInfo(apkPath: String): ExtAppInfo {
    val packageInfo = packageManager.getPackageArchiveInfo(apkPath, PackageManager.GET_META_DATA) as PackageInfo
    packageInfo.applicationInfo.sourceDir = apkPath
    packageInfo.applicationInfo.publicSourceDir = apkPath
    val packageName = packageInfo.packageName
    val appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString()
    val versionName = packageInfo.versionName
    val versionCode = packageInfo.versionCode
    val icon = packageManager.getApplicationIcon(packageInfo.applicationInfo)
    return ExtAppInfo(apkPath, packageName, versionName, versionCode.toLong(), appName, icon)
}

fun Context.getAppInfos(apkFolderPath: String): List<ExtAppInfo> {
    val appInfoList = ArrayList<ExtAppInfo>()
    for (file in File(apkFolderPath).listFiles())
        appInfoList.add(getAppInfo(file.path))
    return appInfoList
}


/**
 * Get app signature by [packageName]
 */
fun Context.getAppSignature(packageName: String = this.packageName): ByteArray? {
    val packageInfo: PackageInfo =
        packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
    val signatures = packageInfo.signatures
    return signatures[0].toByteArray()
}

/**
 * Whether the application is installed
 */
fun Context.isPackageInstalled(pkgName: String): Boolean {
    return try {
        packageManager.getPackageInfo(pkgName, 0)
        true
    } catch (e: Exception) {
        false
    }
}