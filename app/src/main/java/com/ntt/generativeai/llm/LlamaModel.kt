package com.ntt.generativeai.llm
import android.content.Context

class LlamaModel : BaseLlm() {

    override suspend fun init(context: Context?) {
        validateModel()
    }

    override val MODEL_PATH: String
        get() = "/data/local/tmp/llm/<your model here!>"

    override suspend fun generateResponseAsync(text: String) {
    }

    companion object {
        private var instance: LlamaModel? = null
        fun getInstance(): LlamaModel = instance ?: LlamaModel().also { instance = it }
    }
}
