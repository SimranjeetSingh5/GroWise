package com.simranjeet.growise.data.model

data class ExpenseSummary(
    val totalExpense: Double,
    val byCategory: Map<String, Double>,
    val monthOverMonthChange: Double
)