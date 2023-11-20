package com.hero.base.ext

import com.hero.base.log.ALog

/**
 * fun to be easy to use.
 */
fun alogd(message: Any?) = ALog.d(message)

fun alogd(tag: String, message: Any?) = ALog.d(tag, message)

fun aloge(message: String?) = ALog.e(message)

fun aloge(tag: String, message: String?) = ALog.e(tag, message)

fun aloge(throwable: Throwable, message: String?) = ALog.e(throwable, message)

fun aloge(tag: String, throwable: Throwable, message: String?) = ALog.e(tag, throwable, message)

fun alogw(message: String?) = ALog.w(message)

fun alogw(tag: String, message: String?) = ALog.w(tag, message)

fun alogi(message: String?) = ALog.i(message)

fun alogi(tag: String, message: String?) = ALog.i(tag, message)

fun alogv(message: String?) = ALog.v(message)

fun alogv(tag: String, message: String?) = ALog.v(tag, message)

fun alogwtf(message: String?) = ALog.wtf(message)

fun alogwtf(tag: String, message: String?) = ALog.wtf(tag, message)

fun alogjson(json: String?) = ALog.json(json)

fun alogjson(tag: String, json: String?) = ALog.json(tag, json)

fun alogxml(xml: String?) = ALog.xml(xml)

fun alogxml(tag: String, xml: String?) = ALog.xml(tag, xml)