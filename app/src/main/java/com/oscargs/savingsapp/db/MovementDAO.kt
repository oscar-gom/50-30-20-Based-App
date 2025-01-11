package com.oscargs.savingsapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.oscargs.savingsapp.models.Movement
import com.oscargs.savingsapp.utilities.Category
import com.oscargs.savingsapp.utilities.MovementType

@Dao
interface MovementDAO {
    @Query("SELECT * FROM Movement")
    fun getAllMovements(): LiveData<List<Movement>>

    @Query("SELECT * FROM Movement WHERE id = :id")
    fun getMovementById(id: Int): LiveData<Movement>

    @Query("SELECT * FROM Movement WHERE type = :type")
    fun getMovementsByType(type: MovementType): LiveData<List<Movement>>

    @Query("SELECT * FROM Movement WHERE category = :category")
    fun getMovementsByCategory(category: Category): LiveData<List<Movement>>

    @Query("SELECT * FROM Movement WHERE date = :date")
    fun getMovementsByDate(date: String): LiveData<List<Movement>>

    @Upsert
    fun addMovement(movement: Movement)

    @Delete
    fun deleteMovement(movement: Movement)
}