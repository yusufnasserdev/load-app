package com.udacity

import android.app.Application
import timber.log.Timber

class LoadApp  : Application() {

    /**
     * Create the application and plants the timber debugging tree.
     */

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}