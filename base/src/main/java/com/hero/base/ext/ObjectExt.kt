package com.hero.base.ext

fun <T : Any> T.TAG() = this::class.simpleName + "Log"