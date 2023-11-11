package com.hero.ambition.application

import android.app.Application
import com.hero.ambition.coretools.AppBootstrap

class AmbitionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppBootstrap.applicationOnCreate(this)
    }
}