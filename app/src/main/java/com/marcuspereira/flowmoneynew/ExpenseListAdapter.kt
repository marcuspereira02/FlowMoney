package com.marcuspereira.moneyflow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.marcuspereira.flowmoneynew.R
import java.text.NumberFormat
import java.util.Locale

class ExpenseListAdapter :
    ListAdapter<ExpenseUiData, ExpenseListAdapter.ExpenseViewHolder>(ExpenseListAdapter) {

    private lateinit var callback: (ExpenseUiData) -> Unit

    fun setOnClickListener(onClick: (ExpenseUiData) -> Unit) {
        callback = onClick
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ExpenseViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_expense, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = getItem(position)
        holder.bind(expense, callback)
    }

    class ExpenseViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val ivCategoryExpense = view.findViewById<ImageView>(R.id.iv_category_expense)
        private val tvTitleExpense = view.findViewById<TextView>(R.id.tv_title_expense)
        private val tvExpense = view.findViewById<TextView>(R.id.tv_expense)

        fun bind(
            expense: ExpenseUiData,
            callback: (ExpenseUiData) -> Unit,
        ) {
            val locale = Locale("pt","BR")
            val currencyFormat = NumberFormat.getCurrencyInstance(locale)
            val expenseFormatted = currencyFormat.format(expense.value)

            tvTitleExpense.text = expense.name
            ivCategoryExpense.setImageResource(expense.icon)
            tvExpense.text = "-$expenseFormatted"

            view.setOnClickListener {
                callback.invoke(expense)
            }
        }
    }

    companion object : DiffUtil.ItemCallback<ExpenseUiData>() {
        override fun areItemsTheSame(oldItem: ExpenseUiData, newItem: ExpenseUiData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: ExpenseUiData, newItem: ExpenseUiData): Boolean {
            return oldItem.name == newItem.name
        }

    }
}
