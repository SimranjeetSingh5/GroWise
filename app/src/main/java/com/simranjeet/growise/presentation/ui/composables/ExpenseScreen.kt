package com.simranjeet.growise.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.ArrowBack
import androidx.compose.material.icons.automirrored.sharp.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simranjeet.growise.GrowiseApp
import com.simranjeet.growise.data.model.TransactionEntity
import com.simranjeet.growise.presentation.ui.theme.GroWiseTheme
import com.simranjeet.growise.presentation.ui.theme.groWiseApp
import com.simranjeet.growise.presentation.ui.theme.primaryColor
import com.simranjeet.growise.presentation.viewmodels.transaction.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseScreen(
    transactionId: String? = null,  // null = add mode, non-null = edit mode
    viewModel: TransactionViewModel,
    onSaveClick: (TransactionEntity) -> Unit,
    onBackClick: () -> Unit,
    onShowListClick: () -> Unit
) {
    val localUser by GrowiseApp.instance.localUser.collectAsState()

    val categories = listOf("Food", "Travel", "Shopping", "Bills", "Health", "Other")
    val editTransaction by viewModel.editTransaction.collectAsState()

    var selectedCategory by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var transactionIdToEdit by remember { mutableStateOf<String?>(null) }

    // Fetch transaction when transactionId is provided
    LaunchedEffect(transactionId) {
        if (transactionId != null) {
            viewModel.fetchTransactionById(transactionId)
        } else {
            viewModel.clearEditTransaction()
            // Reset form for new expense
            selectedCategory = ""
            date = ""
            amount = ""
            notes = ""
            transactionIdToEdit = null
        }
    }

    // Pre-fill form when editing
    LaunchedEffect(editTransaction) {
        editTransaction?.let { transaction ->
            transactionIdToEdit = transaction.id
            selectedCategory = transaction.category
            date = transaction.timestamp
            amount = transaction.amount
            notes = transaction.note ?: ""
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
                    .padding(top = 10.dp, bottom = 40.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                viewModel.clearEditTransaction()
                                onBackClick()
                            },
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
                        Button(
                            onClick = onShowListClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(40.dp),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.size(54.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Sharp.List,
                                contentDescription = "List",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Text(
                        text = if (transactionIdToEdit != null) "Edit Expense" else "Add New Expense",
                        fontSize = 30.sp,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium,
                        fontFamily = groWiseApp,
                        modifier = Modifier.padding(start = 8.dp, top = 32.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        TextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(text = "Select Category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            categories.forEach { category ->
                                DropdownMenuItem(
                                    modifier = Modifier.background(Color.White),
                                    text = { Text(text = category) },
                                    onClick = {
                                        selectedCategory = category
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Date Picker
                    CustomDatePicker(
                        selectedDate = date,
                        onDateSelected = { pickedDate ->
                            date = pickedDate
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text(text = "Expense Amount") },
                        singleLine = true,
                        leadingIcon = {
                            Text(
                                text = "₹",
                                fontSize = 20.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Notes
                    TextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text(text = "Take Notes") },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                    )
                }

                // Save Button
                Button(
                    onClick = {
                        val userEmail = localUser?.email ?: return@Button

                        val transaction = TransactionEntity(
                            id = transactionIdToEdit ?: java.util.UUID.randomUUID().toString(),
                            userEmail = userEmail,
                            amount = amount,
                            category = selectedCategory,
                            subCategory = null,
                            note = notes.ifBlank { null },
                            timestamp = date,
                            synced = false
                        )
                        onSaveClick(transaction)
                    },
                    enabled = selectedCategory.isNotBlank() &&
                            date.isNotBlank() &&
                            amount.isNotBlank() &&
                            localUser != null,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = if (transactionIdToEdit != null) "Update Expense" else "Save Expense",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}