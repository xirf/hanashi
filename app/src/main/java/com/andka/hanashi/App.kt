package com.andka.hanashi

import android.app.Application
import com.andka.hanashi.utils.Locator

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Locator.initWith(this)
    }
}