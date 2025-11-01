package com.meetingminutes.app

import android.app.Application
import com.meetingminutes.app.data.database.AppDatabase

/**
 * 应用程序类
 */
class MeetingApp : Application() {
    
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }
    
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    
    companion object {
        lateinit var instance: MeetingApp
            private set
    }
}

