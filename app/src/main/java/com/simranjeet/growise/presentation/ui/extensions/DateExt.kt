package com.simranjeet.growise.presentation.ui.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun Date.formatDate(): String =
    SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        .format(this)