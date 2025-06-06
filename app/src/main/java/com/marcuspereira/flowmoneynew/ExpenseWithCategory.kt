package com.marcuspereira.flowmoneynew

import androidx.room.Embedded
import androidx.room.Relation

data class ExpenseWithCategory(
    @Embedded val expense: ExpenseEntity,
    @Relation(
        parentColumn = "category",
        entityColumn = "key"
    )
    val categoryEntity: CategoryEntity
)
