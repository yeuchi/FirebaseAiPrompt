package com.ctyeung.prompt

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.ctyeung.prompt.ui.theme.PromptTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    val viewModel : GeminiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        invalidate("onCreate")
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.event.collect {
                when (it) {
                    is GeminiEvent.Prompt -> invalidate(it.response)
                    is GeminiEvent.PromptFailed -> invalidate(it.error)
                }
            }
        }
        viewModel.askGemini("Write a story about a magic backpack.")
    }

    fun invalidate(msg:String) {
        setContent {
            PromptTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = msg,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PromptTheme {
        Greeting("Android")
    }
}