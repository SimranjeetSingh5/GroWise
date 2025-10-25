package com.simranjeet.growise.presentation.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simranjeet.growise.presentation.ui.extensions.formatDate
import com.simranjeet.growise.presentation.ui.theme.GroWiseTheme
import com.simranjeet.growise.presentation.ui.theme.LightBlack
import com.simranjeet.growise.presentation.ui.theme.primaryColor
import com.simranjeet.growise.presentation.ui.theme.white
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    selectedDate: String,
    onDateSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    val datePickerState =
        rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
    GroWiseTheme {
        if (showDialog) {
            DatePickerDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val milliseconds = datePickerState.selectedDateMillis
                            // Convert selected millis to a formatted date string
                            if (milliseconds != null) {
                                onDateSelected(Date(milliseconds).formatDate())
                            }
                            showDialog = false
                        },
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = LightBlack,
                            contentColor = Color.White
                        )
                    ) {
                        Text("OK", modifier = Modifier.background(LightBlack), color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog = false }, colors = ButtonDefaults.textButtonColors(
                            containerColor = LightBlack,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            "Cancel",
                            modifier = Modifier.background(LightBlack),
                            color = Color.White,

                            )
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState,
                    colors = DatePickerDefaults.colors(
                        containerColor = LightBlack,
                        titleContentColor = Color.White,
                        headlineContentColor = Color.White,
                        weekdayContentColor = Color.White,
                        subheadContentColor = Color.White,
                        navigationContentColor = Color.White,
                        yearContentColor = Color.White,
                        disabledYearContentColor = Color.Gray,
                        currentYearContentColor = primaryColor,
                        selectedYearContentColor = Color.White,
                        disabledSelectedYearContentColor = Color.Gray,
                        selectedYearContainerColor = primaryColor,
                        disabledSelectedYearContainerColor = Color.Gray,
                        dayContentColor = Color.White,
                        disabledDayContentColor = Color.Gray,
                        selectedDayContentColor = LightBlack,
                        disabledSelectedDayContentColor = Color.Gray,
                        selectedDayContainerColor = primaryColor,
                        disabledSelectedDayContainerColor = Color.Gray,
                        todayContentColor = Color.White,
                        todayDateBorderColor = primaryColor,
                        dayInSelectionRangeContentColor = Color.White,
                        dayInSelectionRangeContainerColor = primaryColor,
                        dividerColor = Color.White,
                        dateTextFieldColors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = LightBlack,
                            unfocusedTextColor = LightBlack
                        )
                    )
                )
            }
        }



        OutlinedTextField(
            value = selectedDate,
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDialog = true },
            label = { Text(text = "Date") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDialog = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                }
            },
            colors = TextFieldDefaults.colors(
                focusedTextColor = LightBlack,
                unfocusedTextColor = LightBlack,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                focusedContainerColor = white,
                unfocusedContainerColor = white
            ),
            shape = RoundedCornerShape(16.dp)
        )
    }
}
