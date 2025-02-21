package com.example.dayplanner

import android.app.Application
import com.example.dayplanner.data.AppContainer
import com.example.dayplanner.data.DefaultAppContainer

/**
 * application that contains an [AppContainer]
 */
class DayPlannerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(context = this)
    }
}