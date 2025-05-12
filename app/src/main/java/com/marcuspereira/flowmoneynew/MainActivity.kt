package com.marcuspereira.flowmoneynew

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.marcuspereira.flowmoneynew.databinding.ActivityMainBinding
import com.marcuspereira.moneyflow.CategoryListAdapter
import com.marcuspereira.moneyflow.CategoryUiData
import com.marcuspereira.moneyflow.ExpenseListAdapter
import com.marcuspereira.moneyflow.ExpenseUiData

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

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

        categoryAdapter.setOnClickListener { selected->

            val categoryTemp = listOfCategory.map {item ->
                when{
                    item.icon == selected.icon && !item.isSelected -> item.copy(isSelected = true)
                    item.icon == selected.icon && item.isSelected -> item.copy(isSelected = true)
                    item.icon != selected.icon && item.isSelected -> item.copy(isSelected = false)
                    else -> item
                }
            }

            val extenseTemp = listOfExpense.filter { it.icon == selected.icon}

            categoryAdapter.submitList(categoryTemp)
            expenseAdapter.submitList(extenseTemp)
        }

        categoryAdapter.setOnLongClickListener {  }

        expenseAdapter.setOnClickListener {  }

        binding.rvCategories.adapter = categoryAdapter
        categoryAdapter.submitList(listOfCategory)

        binding.rvExpenses.adapter = expenseAdapter
        expenseAdapter.submitList(listOfExpense)
    }
}

private val listOfCategory = listOf(
    CategoryUiData(
        icon = R.drawable.ic_food,
        isSelected = false
    ),
    CategoryUiData(
        icon = R.drawable.ic_home,
        isSelected = false
    ),
    CategoryUiData(
        icon = R.drawable.ic_bus,
        isSelected = false
    ),
    CategoryUiData(
        icon = R.drawable.ic_shirt,
        isSelected = false
    ),
    CategoryUiData(
        icon = R.drawable.ic_network,
        isSelected = false
    ),
    CategoryUiData(
        icon = R.drawable.ic_supermarket_basket,
        isSelected = false
    )
)

private val listOfExpense = listOf(
    ExpenseUiData(
        name = "Pastel",
        icon = R.drawable.ic_food,
        value = 49.90
    ),
    ExpenseUiData(
        name = "Hamburguer",
        icon = R.drawable.ic_food,
        value = 15.90
    ),
    ExpenseUiData(
        name = "Conta de internet",
        icon = R.drawable.ic_network,
        value = 99.00
    ),
    ExpenseUiData(
        name = "Cortinas",
        icon = R.drawable.ic_home,
        value = 28.97
    ),
    ExpenseUiData(
        name = "Uber até o trabalho",
        icon = R.drawable.ic_bus,
        value = 21.84
    ),
    ExpenseUiData(
        name = "Compra mensal do supermercado",
        icon = R.drawable.ic_supermarket_basket,
        value = 458.00
    ),
    ExpenseUiData(
        name = "Tênis",
        icon = R.drawable.ic_shirt,
        value = 21.84
    )
)

