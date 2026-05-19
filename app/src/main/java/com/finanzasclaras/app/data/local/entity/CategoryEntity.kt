package com.finanzasclaras.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nameEn: String,
    val icon: String,
    val type: String,
    val color: Int,
    val isDefault: Boolean = true,
    val orderIndex: Int = 0
)
