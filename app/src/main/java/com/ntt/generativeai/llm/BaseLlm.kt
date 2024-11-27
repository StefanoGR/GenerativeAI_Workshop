package com.ntt.generativeai.llm

import android.content.Context
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File

abstract class BaseLlm : LlmInterface {

    protected val _partialResults = MutableSharedFlow<Pair<String, Boolean>>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val partialResults: SharedFlow<Pair<String, Boolean>> = _partialResults.asSharedFlow()

    val modelExists: Boolean
        get() = File(MODEL_PATH).exists()

    protected fun validateModel() {
        if (!modelExists) {
            throw IllegalArgumentException("Model not found at path: $MODEL_PATH")
        }
    }

    abstract suspend fun init(context: Context? = null)

}