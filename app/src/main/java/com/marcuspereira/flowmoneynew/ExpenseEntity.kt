package com.marcuspereira.flowmoneynew

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["key"],
            childColumns = ["category"]
        )
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo("key")
    val id: Long = 0,
    @ColumnInfo("expense")
    val text: String,
    @ColumnInfo("category")
    val category: Int,
    @ColumnInfo("value")
    val value: Double,
    @ColumnInfo("color")
    val color: Int
)
