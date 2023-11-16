package com.hero.base.ext

inline fun <T : Any> T.TAG() = this::class.simpleName + "Log"

inline fun <T : Any> T?.isNull() = (this == null)

inline fun <T> Any?.notNull(f: () -> T, t: () -> T): T = (this != null).then(f(), t())

inline fun <T> Any?.notNull(f: () -> T, t: T): T = (this != null).then(f(), t)