package com.hero.ambition.application

import android.app.Application
import android.content.Context
import com.hero.ambition.coretools.AppBootstrap
import com.hero.base.Holder

class App : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        Holder.holderApplication(this)
    }

    override fun onCreate() {
        super.onCreate()
        AppBootstrap.applicationOnCreate(this)
    }
}