package com.simranjeet.growise.data.model

import androidx.compose.ui.graphics.Color


data class CategoryExpense(
    val category: String,
    val amount: Double,
    val percentage: Float,
    val color: Color
)

data class MonthlyExpense(
    val month: String,
    val amount: Double
)