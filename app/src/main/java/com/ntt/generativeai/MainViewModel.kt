package com.ntt.generativeai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.ntt.generativeai.llm.BaseLlm
import com.ntt.generativeai.llm.LlamaModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class MainViewModel(
    val llm: BaseLlm = LlamaModel.getInstance()
) : ViewModel() {

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    fun analyzeText(scanned: List<File>){
        viewModelScope.launch(Dispatchers.IO) {
            runCatching {
                val results = scanned.mapIndexed { index, file ->
                    index to processImage(file)
                }.toMap()

                executePrompt(results.toSortedMap().values.joinToString(separator =" "))
            }
        }
    }

    private suspend fun processImage(file: File): String = suspendCancellableCoroutine { continuation ->
        try {
            val image = InputImage.fromBitmap(file.getBitmap(), 0)
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (!continuation.isCompleted) {
                        continuation.resume(visionText.text)
                    }
                }
                .addOnFailureListener { e ->
                    if (!continuation.isCompleted) {
                        continuation.resumeWithException(e)
                    }
                }
        } catch (e: Exception) {
            if (!continuation.isCompleted) {
                continuation.resumeWithException(e)
            }
        }
    }


    suspend fun executePrompt(text: String) {
            kotlin.runCatching {
                llm.generateResponseAsync(text)
            }.onFailure { error ->
                // Handle error
            }
    }
}

