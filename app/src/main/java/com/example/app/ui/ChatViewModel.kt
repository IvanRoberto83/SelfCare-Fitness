package com.example.app.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.BuildConfig
import com.example.app.SelfCareApp
import com.example.app.data.GeminiConfig
import com.example.app.data.GeminiContent
import com.example.app.data.GeminiPart
import com.example.app.data.GeminiRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class ChatMessage(val text: String, val fromUser: Boolean, val isError: Boolean = false)

class ChatViewModel(app: Application) : AndroidViewModel(app) {

    // Hanya di memori: hilang saat aplikasi benar-benar ditutup
    val messages = mutableStateListOf<ChatMessage>()
    var loading by mutableStateOf(false)
        private set

    fun send(text: String) {
        if (text.isBlank() || loading) return
        messages.add(ChatMessage(text.trim(), fromUser = true))
        loading = true

        viewModelScope.launch {
            val reply = try {
                val key = BuildConfig.GEMINI_API_KEY
                if (key.isBlank()) error("API key belum diisi di local.properties")

                val history = messages.filter { !it.isError }.map {
                    GeminiContent(if (it.fromUser) "user" else "model", listOf(GeminiPart(it.text)))
                }
                val request = GeminiRequest(
                    contents = history,
                    systemInstruction = GeminiContent(parts = listOf(GeminiPart(GeminiConfig.SYSTEM_PROMPT)))
                )
                val res = getApplication<SelfCareApp>().geminiApi
                    .generate(GeminiConfig.MODEL, key, request)
                val answer = res.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ChatMessage(answer ?: "Maaf, tidak ada jawaban. Coba ulangi pertanyaannya.", fromUser = false)
            } catch (e: HttpException) {
                val body = e.response()?.errorBody()?.string()
                ChatMessage("HTTP ${e.code()}: ${body ?: e.message()}", fromUser = false, isError = true)
            } catch (e: Exception) {
                ChatMessage("Gagal terhubung: ${e.message}", fromUser = false, isError = true)
            }
            messages.add(reply)
            loading = false
        }
    }
}