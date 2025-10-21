package com.simranjeet.growise.presentation.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.simranjeet.growise.data.client.SupabaseClient
import com.simranjeet.growise.data.model.TodoItem
import com.simranjeet.growise.presentation.ui.composables.MainScreen
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun TodoList() {
    var items by remember { mutableStateOf<List<TodoItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                items = SupabaseClient.client
                    .from("todos")
                    .select()
                    .decodeList<TodoItem>()

                Log.d("TodoList", "Successfully loaded ${items.size} items")
            }
        } catch (e: Exception) {
            Log.e("TodoList", "Error loading todos", e)
            errorMessage = e.message ?: "Unknown error occurred"
        } finally {
            isLoading = false
        }
    }

    when {
        isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val composition by rememberLottieComposition(
                    LottieCompositionSpec.Asset("future_tech.lottie")
                )
                val progress by animateLottieCompositionAsState(
                    composition,
                    iterations = LottieConstants.IterateForever
                )

                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(-1f)
                )
            }
        }

        errorMessage != null -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        items.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No todos found")
            }
        }

        else -> {
            LazyColumn {
                items(
                    items = items,
                    key = { item -> item.id!! }
                ) { item ->
                    Text(
                        text = item.title,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}