package com.hero.base.ext

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.getStackTraceText(): String {
    val sw = StringWriter()
    val pw = PrintWriter(sw)
    printStackTrace(pw)
    return sw.toString()
}