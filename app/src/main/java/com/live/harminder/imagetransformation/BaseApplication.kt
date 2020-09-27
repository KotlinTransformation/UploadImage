package com.live.harminder.imagetransformation

import android.app.Application
import android.content.Context

class BaseApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        instance = this

    }

    companion object {
        var context: Context? = null
        val TAG = BaseApplication::class.java.simpleName
        @get:Synchronized
        var instance: BaseApplication? = null
            private set
    }
}