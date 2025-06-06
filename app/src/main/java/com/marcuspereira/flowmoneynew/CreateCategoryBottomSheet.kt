package com.marcuspereira.flowmoneynew

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.marcuspereira.moneyflow.CategoryListAdapter
import com.marcuspereira.moneyflow.CategoryUiData

class CreateCategoryBottomSheet(
    val onCreateClicked: (icon: Int, color: Int, isSelected: Boolean) -> Unit,
) :
    BottomSheetDialogFragment() {

    private val categoryAdapter = CategoryListAdapter()

    private var iconSelected: Int? = null
    private var colorSelected: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.create_category_bottom_sheet, container, false)

        val context = requireContext()

        val btnCreate = view.findViewById<Button>(R.id.btn_create_category)
        val rvCategory = view.findViewById<RecyclerView>(R.id.rv_categories_bottom_sheet)
        val circleBlue = view.findViewById<View>(R.id.circle_blue)
        val circleRed = view.findViewById<View>(R.id.circle_red)
        val circleGreen = view.findViewById<View>(R.id.circle_green)
        val circleYellow = view.findViewById<View>(R.id.circle_yellow)
        val circleOrange = view.findViewById<View>(R.id.circle_orange)
        val circlePurple = view.findViewById<View>(R.id.circle_purple)
        val circleMaroon = view.findViewById<View>(R.id.circle_maroon)

        val colorViews = listOf(
            circleMaroon,
            circlePurple,
            circleBlue,
            circleOrange,
            circleYellow,
            circleRed,
            circleGreen
        )

        rvCategory.adapter = categoryAdapter
        categoryAdapter.submitList(listOfCategory)

        categoryAdapter.setOnClickListener { category ->
            val listTemp = listOfCategory.map {
                it.copy(isSelected = it.icon == category.icon)
            }

            categoryAdapter.submitList(listTemp)
            iconSelected = category.icon
        }


        circleBlue.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.blue)
            updateColorSelection(it, colorViews)
        }

        circleRed.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.red)
            updateColorSelection(it, colorViews)
        }

        circleGreen.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.green)
            updateColorSelection(it, colorViews)
        }

        circleYellow.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.yellow)
            updateColorSelection(it, colorViews)
        }

        circleOrange.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.orange)
            updateColorSelection(it, colorViews)
        }

        circleMaroon.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.maroon)
            updateColorSelection(it, colorViews)
        }

        circlePurple.setOnClickListener {
            colorSelected = ContextCompat.getColor(context, R.color.purple)
            updateColorSelection(it, colorViews)
        }

        btnCreate.setOnClickListener {

            val messageColor = getString(R.string.select_color)
            val messageCategory = getString(R.string.select_category)

            val icon = iconSelected
            val color = colorSelected
            val isSelected = true

            if (icon != null) {
                if (color != null) {
                    onCreateClicked(icon, color, isSelected)
                    dismiss()
                } else {
                    Snackbar.make(btnCreate, messageColor, Snackbar.LENGTH_LONG).show()
                }
            } else {
                Snackbar.make(btnCreate, messageCategory, Snackbar.LENGTH_LONG).show()
            }
        }

        return view
    }


    fun updateColorSelection(selected: View, colorViews: List<View>) {
        colorViews.forEach { view ->
            view.isSelected = (view == selected)
        }
    }

    private val listOfCategory = listOf(
        CategoryUiData(
            icon = R.drawable.ic_food,
            color = 0,
            isSelected = false
        ),
        CategoryUiData(
            icon = R.drawable.ic_bus,
            color = 0,
            isSelected = false
        ),
        CategoryUiData(
            icon = R.drawable.ic_home,
            color = 0,
            isSelected = false
        ),
        CategoryUiData(
            icon = R.drawable.ic_network,
            color = 0,
            isSelected = false
        ),
        CategoryUiData(
            icon = R.drawable.ic_shirt,
            color = 0,
            isSelected = false
        ),
        CategoryUiData(
            icon = R.drawable.ic_school,
            color = 0,
            isSelected = false
        ),
        CategoryUiData(
            icon = R.drawable.ic_supermarket_basket,
            color = 0,
            isSelected = false
        )
    )

}