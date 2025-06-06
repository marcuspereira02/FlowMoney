package com.marcuspereira.flowmoneynew

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CategoryEntity::class, ExpenseEntity::class], version = 1)
abstract class MoneyFlowDataBase : RoomDatabase() {
    abstract fun categoryDao() : CategoryDao
    abstract fun expenseDao(): ExpenseDao
}