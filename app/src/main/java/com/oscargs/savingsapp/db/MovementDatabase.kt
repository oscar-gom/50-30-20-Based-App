package com.oscargs.savingsapp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oscargs.savingsapp.models.Movement

@Database(entities = [Movement::class], version = 1)
@TypeConverters(Converter::class)
abstract class MovementDatabase: RoomDatabase() {
    abstract fun movementDAO(): MovementDAO

    companion object {
        const val DATABASE_NAME = "movement_db"
    }
}