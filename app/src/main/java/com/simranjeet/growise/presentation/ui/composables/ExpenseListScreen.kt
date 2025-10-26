package com.simranjeet.growise.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.di.DIContainer
import com.simranjeet.growise.presentation.ui.theme.GroWiseTheme
import com.simranjeet.growise.presentation.ui.theme.LightBlack
import com.simranjeet.growise.presentation.ui.theme.groWiseApp
import com.simranjeet.growise.presentation.ui.theme.primaryColor
import com.simranjeet.growise.presentation.viewmodelfactory.transaction.TransactionViewModelFactory
import com.simranjeet.growise.presentation.viewmodels.transaction.TransactionViewModel
import kotlinx.coroutines.flow.collectLatest
import org.kodein.di.instance

@Composable
fun ExpenseListScreen(
    onBackClick: () -> Unit,
    onTransactionClick: (String) -> Unit
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

    GroWiseTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(primaryColor)
                .padding(horizontal = 14.dp, vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(primaryColor)
                    .padding(top = 10.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(primaryColor),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    // Header as first item
                    item {
                        Button(
                            onClick = onBackClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(40.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Sharp.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = "Previous Expenses",
                            fontSize = 30.sp,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            fontFamily = groWiseApp,
                            modifier = Modifier.padding(start = 8.dp, top = 32.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Loading state
                    if (isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = androidx.compose.ui.Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color.White)
                            }
                        }
                    }

                    // Error state
                    if (errorMessage.isNotEmpty()) {
                        item {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    // Empty state
                    if (!isLoading && transactions.isEmpty()) {
                        item {
                            Text(
                                text = "No expenses found.",
                                color = LightBlack,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    // Transaction items - MAKE THEM CLICKABLE
                    items(transactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = { onTransactionClick(transaction.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    Surface(
        color = Color.White,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clickable(onClick = onClick),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "${transaction.category}${transaction.subCategory?.let { " - $it" } ?: ""}",
                fontSize = 18.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "₹${transaction.amount} ${transaction.currency}",
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            transaction.note?.let {
                Text(
                    text = it,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Text(
                text = transaction.timestamp,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}