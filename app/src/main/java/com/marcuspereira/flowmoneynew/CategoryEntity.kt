package com.marcuspereira.flowmoneynew

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey
    @ColumnInfo("key")
    val icon: Int,
    @ColumnInfo("color")
    val color: Int,
    @ColumnInfo("is_selected")
    val isSelected: Boolean
)
