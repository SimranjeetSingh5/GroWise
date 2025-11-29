package com.simranjeet.growise.presentation.ui.composables

import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.*
import com.simranjeet.growise.data.model.CategoryExpense
import com.simranjeet.growise.data.model.MonthlyExpense
import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.di.DIContainer
import com.simranjeet.growise.presentation.viewmodelfactory.transaction.TransactionViewModelFactory
import com.simranjeet.growise.presentation.viewmodels.transaction.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.YearMonth
import kotlin.collections.isNotEmpty
import kotlin.getValue

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseAnalyticsScreen(
    modifier: Modifier = Modifier
) {
    val factory: TransactionViewModelFactory by DIContainer.di.instance()
    val viewModel: TransactionViewModel = viewModel(factory = factory)
    var transactions by remember { mutableStateOf(listOf<TransactionEntity>()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.fetchAllExpenses()
        viewModel.getTransactionState().collectLatest { state ->
            when (state) {
                is TransactionViewModel.TransactionState.LoadedTransaction -> {
                    transactions = state.transaction
                    isLoading = false
                }

                TransactionViewModel.TransactionState.Loading -> isLoading = true
                is TransactionViewModel.TransactionState.ShowError -> {
                    errorMessage = state.message
                    isLoading = false
                }

                TransactionViewModel.TransactionState.Empty -> {
                    transactions = emptyList()
                    isLoading = false
                }

                else -> {}
            }
        }
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Overview", "Categories", "Trends")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Expense Analytics",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE8FF5C)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            // Tab Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color.Black
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Content
            when (selectedTab) {
                0 -> OverviewTab(transactions)
                1 -> CategoriesTab(transactions)
                2 -> TrendsTab(transactions)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun OverviewTab(transactions: List<TransactionEntity>) {
    val scrollState = rememberScrollState()
    val categoryExpenses = remember(transactions) {
        calculateCategoryExpenses(transactions)
    }
    val monthlyExpenses = remember(transactions) {
        calculateMonthlyExpenses(transactions)
    }
    val totalExpense = remember(transactions) {
        transactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Total Expense Card
        TotalExpenseCard(totalExpense)

        // Category Breakdown (Vertical Bar Chart inspired by the image)
        if (categoryExpenses.isNotEmpty()) {
            CategoryBarChart(categoryExpenses)
        }

        // Monthly Trend
        if (monthlyExpenses.isNotEmpty()) {
            MonthlyTrendCard(monthlyExpenses)
        }
    }
}

@Composable
fun CategoriesTab(transactions: List<TransactionEntity>) {
    val scrollState = rememberScrollState()
    val categoryExpenses = remember(transactions) {
        calculateCategoryExpenses(transactions)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Expense by Category",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        categoryExpenses.forEach { categoryExpense ->
            CategoryExpenseItem(categoryExpense)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TrendsTab(transactions: List<TransactionEntity>) {
    val scrollState = rememberScrollState()
    val monthlyExpenses = remember(transactions) {
        calculateMonthlyExpenses(transactions)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Spending Trends",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        if (monthlyExpenses.isNotEmpty()) {
            MonthlyLineChart(monthlyExpenses)
        }
    }
}

@Composable
fun TotalExpenseCard(totalExpense: Double) {
    var visible by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    LaunchedEffect(Unit) {
        visible = true
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8FF5C)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Total Expenses",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "₹${String.format("%,.2f", totalExpense)}",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun CategoryBarChart(categoryExpenses: List<CategoryExpense>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Categories Expenses",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            val barData = categoryExpenses.mapIndexed { index, expense ->
                BarData(
                    point = Point(index.toFloat(), expense.amount.toFloat()),
                    color = expense.color,
                    label = expense.category.take(3).uppercase()
                )
            }

            val maxValue = categoryExpenses.maxOfOrNull { it.amount }?.toFloat() ?: 1000f

            val xAxisData = AxisData.Builder()
                .axisStepSize(30.dp)
                .steps(categoryExpenses.size - 1)
                .bottomPadding(12.dp)
                .axisLabelAngle(0f)
                .labelData { index ->
                    if (index < categoryExpenses.size) {
                        categoryExpenses[index].category.take(3).uppercase()
                    } else ""
                }
                .build()

            val yAxisData = AxisData.Builder()
                .steps(5)
                .labelAndAxisLinePadding(20.dp)
                .axisOffset(10.dp)
                .labelData { i ->
                    val value = (maxValue * i / 5)
                    "₹${(value / 1000).toInt()}k"
                }
                .build()

            val barChartData = BarChartData(
                chartData = barData,
                xAxisData = xAxisData,
                yAxisData = yAxisData,
                barStyle = BarStyle(
                    paddingBetweenBars = 20.dp,
                    barWidth = 35.dp
                ),
                backgroundColor = Color.White
            )

            BarChart(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                barChartData = barChartData
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Percentage badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categoryExpenses.forEach { expense ->
                    PercentageBadge(
                        percentage = expense.percentage,
                        color = expense.color,
                        label = expense.category
                    )
                }
            }
        }
    }
}

@Composable
fun PercentageBadge(percentage: Float, color: Color, label: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = color.copy(alpha = 0.2f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                "${percentage.toInt()}%",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun MonthlyTrendCard(monthlyExpenses: List<MonthlyExpense>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "Monthly Trend",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            MonthlyLineChart(monthlyExpenses)
        }
    }
}

@Composable
fun MonthlyLineChart(monthlyExpenses: List<MonthlyExpense>) {
    val points = monthlyExpenses.mapIndexed { index, expense ->
        Point(index.toFloat(), expense.amount.toFloat())
    }

    val maxValue = monthlyExpenses.maxOfOrNull { it.amount }?.toFloat() ?: 10000f

    val xAxisData = AxisData.Builder()
        .axisStepSize(50.dp)
        .steps(monthlyExpenses.size - 1)
        .bottomPadding(12.dp)
        .labelData { index ->
            if (index < monthlyExpenses.size) {
                monthlyExpenses[index].month
            } else ""
        }
        .build()

    val yAxisData = AxisData.Builder()
        .steps(5)
        .labelAndAxisLinePadding(20.dp)
        .axisOffset(10.dp)
        .labelData { i ->
            val value = (maxValue * i / 5)
            "₹${(value / 1000).toInt()}k"
        }
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = points,
                    lineStyle = LineStyle(
                        color = Color(0xFF6C63FF),
                        lineType = LineType.SmoothCurve(isDotted = false)
                    ),
                    intersectionPoint = IntersectionPoint(
                        color = Color(0xFF6C63FF)
                    ),
                    selectionHighlightPoint = SelectionHighlightPoint(
                        color = Color(0xFF6C63FF)
                    ),
                    shadowUnderLine = ShadowUnderLine(
                        alpha = 0.3f,
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF6C63FF).copy(alpha = 0.4f),
                                Color.Transparent
                            )
                        )
                    ),
                    selectionHighlightPopUp = SelectionHighlightPopUp()
                )
            )
        ),
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        backgroundColor = Color.White
    )

    LineChart(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp),
        lineChartData = lineChartData
    )
}

@Composable
fun CategoryExpenseItem(categoryExpense: CategoryExpense) {
    var expanded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (expanded) 1.02f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(if (expanded) 8.dp else 2.dp),
        onClick = { expanded = !expanded }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(color = categoryExpense.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getCategoryIcon(categoryExpense.category),
                    contentDescription = null,
                    tint = categoryExpense.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    categoryExpense.category,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    "${categoryExpense.percentage.toInt()}% of total",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Text(
                text = "₹${String.format("%,.0f", categoryExpense.amount)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = categoryExpense.color
            )
        }

        if (expanded) {
            Divider()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Average per transaction",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    "₹${String.format("%,.0f", categoryExpense.amount / 5)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// Helper functions
fun calculateCategoryExpenses(transactions: List<TransactionEntity>): List<CategoryExpense> {
    val categoryTotals = transactions
        .groupBy { it.category }
        .mapValues { (_, transactions) ->
            transactions.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
        }

    val total = categoryTotals.values.sum()
    val colors = listOf(
        Color(0xFFFF6B6B), // Red
        Color(0xFF4ECDC4), // Teal
        Color(0xFF45B7D1), // Blue
        Color(0xFFFFA07A), // Orange
        Color(0xFF98D8C8), // Mint
        Color(0xFFFFD93D), // Yellow
        Color(0xFF6C5CE7)  // Purple
    )

    return categoryTotals.entries
        .sortedByDescending { it.value }
        .take(6)
        .mapIndexed { index, (category, amount) ->
            CategoryExpense(
                category = category,
                amount = amount,
                percentage = if (total > 0) ((amount / total) * 100).toFloat() else 0f,
                color = colors[index % colors.size]
            )
        }
}

@RequiresApi(Build.VERSION_CODES.O)
fun calculateMonthlyExpenses(transactions: List<TransactionEntity>): List<MonthlyExpense> {
    val formatter = DateTimeFormatter.ISO_DATE_TIME

    return transactions
        .mapNotNull { transaction ->
            try {
                val dateTime = LocalDateTime.parse(transaction.timestamp, formatter)
                val month = YearMonth.of(dateTime.year, dateTime.month)
                month to (transaction.amount.toDoubleOrNull() ?: 0.0)
            } catch (e: Exception) {
                null
            }
        }
        .groupBy { it.first }
        .mapValues { (_, values) -> values.sumOf { it.second } }
        .toSortedMap()
        .map { (month, amount) ->
            MonthlyExpense(
                month = month.month.toString().take(3),
                amount = amount
            )
        }
        .takeLast(6)
}

fun getCategoryIcon(category: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category.lowercase()) {
        "food", "grocery" -> Icons.Default.ShoppingCart
        "transport", "travel" -> Icons.Default.FavoriteBorder
        "shopping" -> Icons.Default.ShoppingCart
        "entertainment" -> Icons.Default.Star
        "health", "medical" -> Icons.Default.Favorite
        "bills", "utilities" -> Icons.Default.Notifications
        "education" -> Icons.Default.Phone
        else -> Icons.Default.Notifications
    }
}