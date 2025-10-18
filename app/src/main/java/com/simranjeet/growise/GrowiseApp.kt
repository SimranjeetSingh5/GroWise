package com.simranjeet.growise

import android.app.Application
import com.simranjeet.growise.di.DIContainer

class GrowiseApp : Application() {

    override fun onCreate() {
        super.onCreate()
        DIContainer.init(this)
    }
}
