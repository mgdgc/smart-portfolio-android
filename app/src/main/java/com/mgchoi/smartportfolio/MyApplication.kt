package com.mgchoi.smartportfolio

import android.app.Application

class MyApplication : Application() {

    companion object {
        @JvmStatic
        var login: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
    }
}