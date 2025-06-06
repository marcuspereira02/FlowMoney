package com.marcuspereira.flowmoneynew

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Transaction
    @Query("SELECT * FROM ExpenseEntity")
    fun getExpensesWithCategory(): List<ExpenseWithCategory>

    @Transaction
    @Query("SELECT * FROM expenseentity where category is :categoryName")
    fun getAllByCategories(categoryName: Int):  List<ExpenseWithCategory>

    @Query("Select * FROM expenseentity")
    fun getAll(): List<ExpenseEntity>

    @Query("Select * From expenseentity where category is :categoryName")
    fun getAllByCategory(categoryName: Int): List<ExpenseEntity>

    @Insert
    fun insertAll(expenseEntity: List<ExpenseEntity>)

    @Insert
    fun insert(expenseEntity: ExpenseEntity)

    @Update
    fun update(expenseEntity: ExpenseEntity)

    @Delete
    fun delete(expenseEntity: ExpenseEntity)

    @Delete
    fun deleteAll(expenseEntity: List <ExpenseEntity>)
}