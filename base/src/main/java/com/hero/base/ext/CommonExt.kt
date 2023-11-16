package com.hero.base.ext


import android.content.ClipData
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import androidx.annotation.RequiresApi


/**
 * Whether horizontal layout direction of this view is from Right to Left.
 */
val Context.isRTLLayout: Boolean
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    get() = resources.configuration.layoutDirection == View.LAYOUT_DIRECTION_RTL

fun fromKitKat() = fromSpecificVersion(Build.VERSION_CODES.KITKAT)
fun fromM() = fromSpecificVersion(Build.VERSION_CODES.M)
fun beforeM() = beforeSpecificVersion(Build.VERSION_CODES.M)
fun fromN() = fromSpecificVersion(Build.VERSION_CODES.N)
fun beforeN() = beforeSpecificVersion(Build.VERSION_CODES.N)
fun fromO() = fromSpecificVersion(Build.VERSION_CODES.O)
fun beforeO() = beforeSpecificVersion(Build.VERSION_CODES.O)
fun fromP() = fromSpecificVersion(Build.VERSION_CODES.P)
fun beforeP() = beforeSpecificVersion(Build.VERSION_CODES.P)
fun fromQ() = fromSpecificVersion(Build.VERSION_CODES.Q)
fun beforeQ() = beforeSpecificVersion(Build.VERSION_CODES.Q)
fun fromT() = fromSpecificVersion(Build.VERSION_CODES.TIRAMISU)
fun beforeT() = beforeSpecificVersion(Build.VERSION_CODES.TIRAMISU)
fun fromSpecificVersion(version: Int): Boolean = Build.VERSION.SDK_INT >= version
fun beforeSpecificVersion(version: Int): Boolean = Build.VERSION.SDK_INT < version
fun buildVersion() = Build.VERSION.SDK_INT

fun View.dp2px(dp: Int) = (dp * resources.displayMetrics.density + 0.5f).toInt()

fun View.px2dp(px: Int) = (px / resources.displayMetrics.density + 0.5f).toInt()

/**
 * Sets the [text] on the clipboard
 */
fun Context.copyToClipboard(text: String, label: String = "KTX") {
    val clipData = ClipData.newPlainText(label, text)
//    clipboardManager?.primaryClip = clipData
}

/**
 * Check if the accessibility Service which name is [serviceName] is enabled
 */
fun Context.checkAccessibilityServiceEnabled(serviceName: String): Boolean {
    val settingValue =
        Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
    return settingValue.notNull({
        var result = false
        val splitter = TextUtils.SimpleStringSplitter(':')
        while (splitter.hasNext()) {
            if (splitter.next().equals(serviceName, true)) {
                result = true
                break
            }
        }
        result
    }, { false })
}