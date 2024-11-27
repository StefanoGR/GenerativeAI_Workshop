package com.ntt.generativeai.summary

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.flow.SharedFlow

@Composable
internal fun SummaryRoute(
    flow: SharedFlow<Pair<String, Boolean>>
) {
    val messages = remember { StringBuilder("") }

    val state by flow.collectAsState(initial = Pair("", false))

    LaunchedEffect(state) {
        messages.append(state.first)
    }

    SummaryScreen(
        messages.toString(),
        state.second
    )
}