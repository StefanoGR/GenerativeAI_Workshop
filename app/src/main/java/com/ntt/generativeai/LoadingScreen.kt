package com.ntt.generativeai

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ntt.generativeai.llm.BaseLlm
import com.ntt.generativeai.llm.LlamaModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun LoadingRoute(
    baseLlm: BaseLlm,
    onModelLoaded: () -> Unit = { }
) {
    val context = LocalContext.current.applicationContext
    var errorMessage by remember { mutableStateOf("") }

    if (errorMessage != "") {
        ErrorMessage(errorMessage)
    } else {
        LoadingIndicator()
    }

    LaunchedEffect(Unit, block = {
        // Create the LlmInference in a separate thread
        withContext(Dispatchers.IO) {
            try {
                baseLlm.init(context)
                // Notify the UI that the model has finished loading
                withContext(Dispatchers.Main) {
                    onModelLoaded()
                }
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "UnknownError"
            }
        }
    })
}

@Composable
fun LoadingIndicator() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Loading model",
            modifier = Modifier
                .padding(bottom = 8.dp),
            color = Color.Black
        )
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(
    errorMessage: String
) {
    Box(
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = errorMessage,
            textAlign = TextAlign.Center,
            color = Color.Black
        )
    }
}