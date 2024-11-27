package com.ntt.generativeai.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ntt.generativeai.createPdfFromText
import com.ntt.generativeai.ui.theme.GenerativeAITheme

@Composable
fun SummaryScreen(result: String, hasFinished: Boolean) {

    Text(
        modifier = Modifier.padding(top = 24.dp),
        text = "Hello!",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleLarge,
        color = Color.Black
    )
}

@Preview
@Composable
fun SummaryScreenPreview() {
    GenerativeAITheme {
        SummaryScreen("This is a summary", false)
    }
}
