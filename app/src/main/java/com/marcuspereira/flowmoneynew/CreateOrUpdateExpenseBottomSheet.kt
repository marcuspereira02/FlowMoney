package com.marcuspereira.flowmoneynew

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.marcuspereira.moneyflow.CategoryListAdapter
import com.marcuspereira.moneyflow.CategoryUiData
import com.marcuspereira.moneyflow.ExpenseUiData

class CreateOrUpdateExpenseBottomSheet(
    private val categoryList: List<CategoryEntity>,
    private val expense: ExpenseUiData? = null,
    private val onCreateClicked: (ExpenseUiData) -> Unit,
    private val onUpdateClicked: (ExpenseUiData) -> Unit,
    private val onDeleteClicked: (ExpenseUiData) -> Unit,
    ) : BottomSheetDialogFragment() {

    private var  selectedCategory: Int? = null
    private val categoryAdapter = CategoryListAdapter()
    private val selectedCategoryColor = categoryList.find { it.icon == selectedCategory }?.color ?: 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_expense_bottom_sheet, container, false)

        val tvTitle = view.findViewById<TextView>(R.id.tv_title_expense_bottom_sheet)
        val tieTextExpense = view.findViewById<TextInputEditText>(R.id.tie_text_expense)
        val tieValue = view.findViewById<TextInputEditText>(R.id.tie_value_expense)
        val rvCategory = view.findViewById<RecyclerView>(R.id.rv_categories_expense_bottom_sheet)

        val btnCreateOrUpdate = view.findViewById<Button>(R.id.btn_create_or_update_expense)
        val btnDelete = view.findViewById<Button>(R.id.btn_delete_expense)


        categoryAdapter.setOnClickListener { category ->
            val listTemp = categoryAdapter.currentList.map{
                it.copy(isSelected = it.icon == category.icon)
            }
            categoryAdapter.submitList(listTemp)
            selectedCategory = category.icon
        }

        val categoryListTemp = if (expense != null) {
            categoryList.map {
                CategoryUiData(
                    icon = it.icon,
                    color = it.color,
                    isSelected = it.icon == expense.category
                ).also { uiData ->
                    if (uiData.isSelected) selectedCategory = uiData.icon
                }
            }
        } else {
            categoryList.map {
                CategoryUiData(
                    icon = it.icon,
                    color = it.color,
                    isSelected = false
                )
            }
        }

        categoryAdapter.submitList(categoryListTemp)

        rvCategory.adapter = categoryAdapter
        categoryAdapter.submitList(categoryListTemp)

        if (expense == null) {
            btnDelete.isVisible = false
            tvTitle.setText(R.string.create_expense_title)
            btnCreateOrUpdate.setText(R.string.create)
        } else {
            btnDelete.isVisible = true
            tvTitle.setText(R.string.update_expense_title)
            btnCreateOrUpdate.setText(R.string.update)
            tieTextExpense.setText(expense.text)
            tieValue.setText(expense.value.toString())

        }

        btnCreateOrUpdate.setOnClickListener {

            val messageExpense = getString(R.string.write_expense)
            val messageValue = getString(R.string.write_value)
            val messageCategory = getString(R.string.select_category)

            val textExpense = tieTextExpense.text.toString().trim()
            val valueExpense = tieValue.text.toString().trim()

            if (textExpense.isEmpty()) {
                Snackbar.make(tieTextExpense, messageExpense, Snackbar.LENGTH_LONG)
                    .show()
            } else if (valueExpense.isEmpty()) {
                Snackbar.make(tieTextExpense, messageValue, Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (selectedCategory != null) {
                    if (expense == null) {
                        onCreateClicked.invoke(
                            ExpenseUiData(
                                id = 0,
                                text = textExpense,
                                category = requireNotNull(selectedCategory),
                                value = valueExpense.toDouble(),
                                color = selectedCategoryColor
                            )
                        )
                    } else {
                        onUpdateClicked.invoke(
                            ExpenseUiData(
                                id = expense.id,
                                text = textExpense,
                                category = requireNotNull(selectedCategory),
                                value = valueExpense.toDouble(),
                                color = selectedCategoryColor
                            )
                        )
                    }
                    dismiss()
                } else {
                    Snackbar.make(
                        btnCreateOrUpdate,
                        messageCategory,
                        Snackbar.LENGTH_LONG
                    )
                        .show()
                }
            }
        }

        btnDelete.setOnClickListener {
            if(expense != null){
                onDeleteClicked.invoke(expense)
                dismiss()
            }
        }

        return view
    }

}
