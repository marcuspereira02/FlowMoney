package com.marcuspereira.flowmoneynew

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.room.Room
import com.marcuspereira.flowmoneynew.databinding.ActivityMainBinding
import com.marcuspereira.moneyflow.CategoryListAdapter
import com.marcuspereira.moneyflow.CategoryUiData
import com.marcuspereira.moneyflow.ExpenseListAdapter
import com.marcuspereira.moneyflow.ExpenseUiData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            MoneyFlowDataBase::class.java, "MoneyFlowDataBase"
        ).build()
    }

    private var categories = listOf<CategoryUiData>()
    private var categoriesEntity = listOf<CategoryEntity>()
    private var expenses = listOf<ExpenseUiData>()

    val categoryDao: CategoryDao by lazy {
        db.categoryDao()
    }
    val expenseDao: ExpenseDao by lazy {
        db.expenseDao()
    }

    private val expenseAdapter = ExpenseListAdapter()
    private val categoryAdapter = CategoryListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        categoryAdapter.setOnLongClickListener { categoryToBeDelete ->
            if (categoryToBeDelete.icon != R.drawable.ic_plus && categoryToBeDelete.icon != R.drawable.ic_all) {
                val title = this.getString(R.string.delete_category)
                val btnText = this.getString(R.string.delete)
                val description = this.getString(R.string.category_delete_description)

                showInfoBottomSheet(
                    title, description, btnText
                ) {
                    val categoryEntityToBeDelete = CategoryEntity(
                        categoryToBeDelete.icon,
                        categoryToBeDelete.color,
                        categoryToBeDelete.isSelected
                    )
                    deleteCategory(categoryEntityToBeDelete)
                }
            }

        }

        categoryAdapter.setOnClickListener { selected ->

            if (selected.icon == R.drawable.ic_plus) {

                showCreateCategoryBottomSheet()

            } else {

                val categoryTemp = categories.map { item ->
                    when {
                        item.icon == selected.icon && !item.isSelected -> item.copy(isSelected = true)
                        item.icon == selected.icon && item.isSelected -> item.copy(isSelected = true)
                        item.icon != selected.icon && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }

                if (selected.icon != R.drawable.ic_all) {
                    filterExpenseByCategoryIcon(selected.icon)
                } else {
                    GlobalScope.launch(Dispatchers.IO) {
                        getExpensesFromDataBase()
                    }
                }

                categoryAdapter.submitList(categoryTemp)
            }
        }

        expenseAdapter.setOnClickListener { expense ->
            showCreateOrUpdateTaskBottomSheet(expense)
        }

        binding.btnAddExpense.setOnClickListener {
            showCreateOrUpdateTaskBottomSheet()
        }

        binding.rvCategories.adapter = categoryAdapter
        GlobalScope.launch(Dispatchers.IO) {
            getCategoriesFromDataBase()
        }

        binding.rvExpenses.adapter = expenseAdapter
        GlobalScope.launch(Dispatchers.IO) {
            getExpensesFromDataBase()
        }

        binding.btnCategoryEmpty.setOnClickListener {
            showCreateCategoryBottomSheet()
        }

    }

    private fun getCategoriesFromDataBase() {
        val categoriesFromDb: List<CategoryEntity> = categoryDao.getAll()

        categoriesEntity = categoriesFromDb

        GlobalScope.launch(Dispatchers.Main) {
            if (categoriesEntity.isEmpty()) {
                binding.ivBgTotalExpenses.isVisible = false
                binding.tvTotalExpenseLabel.isVisible = false
                binding.tvTotalExpense.isVisible = false
                binding.tvCategoriesLabel.isVisible = false
                binding.rvCategories.isVisible = false
                binding.tvExpensesLabel.isVisible = false
                binding.ivBgExpenses.isVisible = false
                binding.rvExpenses.isVisible = false
                binding.btnAddExpense.isVisible = false
                binding.llEmpty.isVisible = true
            } else {
                binding.ivBgTotalExpenses.isVisible = true
                binding.tvTotalExpenseLabel.isVisible = true
                binding.tvTotalExpense.isVisible = true
                binding.tvCategoriesLabel.isVisible = true
                binding.rvCategories.isVisible = true
                binding.tvExpensesLabel.isVisible = true
                binding.ivBgExpenses.isVisible = true
                binding.rvExpenses.isVisible = true
                binding.btnAddExpense.isVisible = true
                binding.llEmpty.isVisible = false
            }

        }

        val categoriesUiData = categoriesFromDb.map {
            CategoryUiData(
                icon = it.icon,
                color = it.color,
                isSelected = false
            )
        }.toMutableList()

        categoriesUiData.add(
            CategoryUiData(
                icon = R.drawable.ic_plus,
                color = 0,
                isSelected = false
            )
        )

        val categoryListTemp = mutableListOf(
            CategoryUiData(
                icon = R.drawable.ic_all,
                color = 0,
                isSelected = true
            )
        )

        categoryListTemp.addAll(categoriesUiData)
        GlobalScope.launch(Dispatchers.Main) {
            categories = categoryListTemp
            categoryAdapter.submitList(categories)
        }

    }

    private fun getExpensesFromDataBase() {
        val expensesFromDb = expenseDao.getExpensesWithCategory()

        val expensesUiData = expensesFromDb.map {
            ExpenseUiData(
                id = it.expense.id,
                text = it.expense.text,
                category = it.expense.category,
                value = it.expense.value,
                color = it.categoryEntity.color
            )
        }

        GlobalScope.launch(Dispatchers.Main) {
            expenses = expensesUiData
            expenseAdapter.submitList(expenses)

            binding.tvTotalExpense.text = totalAmountOfExpenses()
        }
    }

    private fun insertCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insertCategory(categoryEntity)
            getCategoriesFromDataBase()
            getExpensesFromDataBase()
        }
    }

    private fun showCreateOrUpdateTaskBottomSheet(expenseUiData: ExpenseUiData? = null) {
        val createOrUpdateExpenseBottomSheet = CreateOrUpdateExpenseBottomSheet(
            categoryList = categoriesEntity,
            expense = expenseUiData,
            onCreateClicked = { expenseToBeCreated ->
                val expenseEntityToBeInsert = ExpenseEntity(
                    text = expenseToBeCreated.text,
                    category = expenseToBeCreated.category,
                    value = expenseToBeCreated.value,
                    color = expenseToBeCreated.color
                )
                insertExpense(expenseEntityToBeInsert)
            },
            onUpdateClicked = { expenseToBeUpdated ->
                val expenseEntityToBeUpdate = ExpenseEntity(
                    id = expenseToBeUpdated.id,
                    text = expenseToBeUpdated.text,
                    category = expenseToBeUpdated.category,
                    value = expenseToBeUpdated.value,
                    color = expenseToBeUpdated.color
                )
                updateExpense(expenseEntityToBeUpdate)
            },
            onDeleteClicked = { expenseToBeDeleted ->
                val expenseEntityToBeDelete = ExpenseEntity(
                    id = expenseToBeDeleted.id,
                    text = expenseToBeDeleted.text,
                    category = expenseToBeDeleted.category,
                    value = expenseToBeDeleted.value,
                    color = expenseToBeDeleted.color
                )
                deleteExpense(expenseEntityToBeDelete)
            })

        createOrUpdateExpenseBottomSheet.show(
            supportFragmentManager, "createOrUpdateExpenseBottomSheet"
        )
    }

    private fun insertExpense(expenseEntity: ExpenseEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseDao.insert(expenseEntity)
            getExpensesFromDataBase()
        }
    }

    private fun updateExpense(expenseEntity: ExpenseEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseDao.update(expenseEntity)
            getExpensesFromDataBase()
        }
    }

    private fun deleteExpense(expenseEntity: ExpenseEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            expenseDao.delete(expenseEntity)
            getExpensesFromDataBase()
        }
    }

    private fun showCreateCategoryBottomSheet() {
        val createCategoryBottomSheet = CreateCategoryBottomSheet { icon, color, isSelected ->
            val categoryEntity = CategoryEntity(
                icon = icon,
                color = color,
                isSelected = false,
            )

            insertCategory(categoryEntity)

        }
        createCategoryBottomSheet.show(supportFragmentManager, "create_category")
    }

    private fun deleteCategory(categoryEntity: CategoryEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            val expensesToBeDeleted = expenseDao.getAllByCategory(categoryEntity.icon)
            expenseDao.deleteAll(expensesToBeDeleted)
            categoryDao.delete(categoryEntity)
            getCategoriesFromDataBase()
            getExpensesFromDataBase()
        }
    }

    private fun showInfoBottomSheet(
        textTitle: String,
        textDescription: String,
        textBtn: String,
        onClick: () -> Unit
    ) {
        val infoBottomSheet = InfoBottomSheet(
            title = textTitle,
            description = textDescription,
            btnText = textBtn,
            onClick
        )
        infoBottomSheet.show(
            supportFragmentManager, "infoBottomSheet"
        )
    }

    private fun filterExpenseByCategoryIcon(category: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val expensesFromDb = expenseDao.getAllByCategories(category)

            val expenseUiData: List<ExpenseUiData> = expensesFromDb.map {
                ExpenseUiData(
                    id = it.expense.id,
                    text = it.expense.text,
                    category = it.expense.category,
                    value = it.expense.value,
                    color = it.categoryEntity.color
                )
            }

            GlobalScope.launch(Dispatchers.Main) {
                expenseAdapter.submitList(expenseUiData)
            }
        }
    }

    private fun totalAmountOfExpenses(): String {

        val sum = expenses.sumOf { it.value }
        val formattedSum = String.format("%.2f", sum)

        if (expenses.isEmpty()) {
            return "R$ 0"
        } else {
            return "R$ ${formattedSum}"
        }
    }

}


