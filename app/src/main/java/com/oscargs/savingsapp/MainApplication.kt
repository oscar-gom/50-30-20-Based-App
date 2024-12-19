package com.oscargs.savingsapp

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import com.oscargs.savingsapp.db.MovementDatabase

class MainApplication:Application() {
    companion object {
        lateinit var database: MovementDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            MovementDatabase::class.java,
            MovementDatabase.DATABASE_NAME
        ).build()
    }
}