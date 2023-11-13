package com.hero.base

import android.app.Application


fun app() = Holder.app

object Holder {
    lateinit var app: Application
        private set

    fun holderApplication(application: Application) {
        app = application
    }
}