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
        try {
            instance = this
            // 初始化数据库
            database
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("MeetingApp", "初始化失败", e)
        }
    }

    companion object {
        lateinit var instance: MeetingApp
            private set
    }
}

