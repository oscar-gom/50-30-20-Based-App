package com.oscargs.savingsapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oscargs.savingsapp.utilities.Category
import com.oscargs.savingsapp.utilities.MovementType
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
data class Movement(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val amount: Double,
    val description: String,
    val date: LocalDate,
    val type: MovementType,
    val category: Category,
    val creationTime: LocalDateTime = LocalDateTime.now(),
    val modificationTime: LocalDateTime = LocalDateTime.now()
)
