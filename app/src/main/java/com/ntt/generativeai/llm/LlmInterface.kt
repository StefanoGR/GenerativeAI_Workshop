package com.ntt.generativeai.llm

import kotlinx.coroutines.flow.SharedFlow

interface LlmInterface {
    val MODEL_PATH: String
    val partialResults: SharedFlow<Pair<String, Boolean>>
    suspend fun generateResponseAsync(text: String)
}