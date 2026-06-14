package com.ctyeung.prompt

import android.app.VoiceInteractor
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

/*
 * https://developer.android.com/ai/gemini/developer-api
 */
class GeminiViewModel : ViewModel() {

    private val _event = MutableSharedFlow<GeminiEvent>()
    val event: SharedFlow<GeminiEvent> = _event

    val generativeModel = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-3.5-flash")

    fun askGemini(prompt: String) {
        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                println(response.text) // Handle the UI update here
                _event.emit(GeminiEvent.Prompt(response.text?:""))

            } catch (e: Exception) {
                println("Error: ${e.message}")
                _event.emit(GeminiEvent.PromptFailed("Error: ${e.message}"))
            }
        }
    }

    fun analyzeImage(bitmap: Bitmap, userPrompt: String) {
        viewModelScope.launch {
            try {
                val response = generativeModel.generateContent(
                    content {
                        image(bitmap)
                        text(userPrompt)
                    }
                )
                println(response.text)
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }
}

sealed class GeminiEvent() {
    class Prompt(val response:String): GeminiEvent()
    class PromptFailed(val error:String): GeminiEvent()
}