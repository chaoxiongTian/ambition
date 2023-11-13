package com.hero.base.ext

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes
import com.orhanobut.logger.BuildConfig

fun Context.toast(content: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, content, duration).apply {
        show()
    }
}

fun Context.debugToast(content: String) {
    toast(content)
}

fun Context.toast(@StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(getString(id), duration)
}

fun Context.longToast(content: String) {
    toast(content, Toast.LENGTH_LONG)
}

fun Context.longToast(@StringRes id: Int) {
    toast(id, Toast.LENGTH_LONG)
}

fun Any.toast(context: Context, content: String, duration: Int = Toast.LENGTH_SHORT) {
    context.toast(content, duration)
}

fun Any.toast(context: Context, @StringRes id: Int, duration: Int = Toast.LENGTH_SHORT) {
    context.toast(id, duration)
}

fun Any.longToast(context: Context, content: String) {
    context.longToast(content)
}

fun Any.debugToast(context: Context, content: String, duration: Int = Toast.LENGTH_SHORT) {
    if (!BuildConfig.DEBUG) {
        return
    }
    context.toast(content)
}

fun Any.longToast(context: Context, @StringRes id: Int) {
    context.longToast(id)
}