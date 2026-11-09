package com.example.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.app.ui.AppTheme
import com.example.app.ui.ChatScreen
import com.example.app.ui.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                var showChat by rememberSaveable { mutableStateOf(false) }
                BackHandler(enabled = showChat) { showChat = false }

                if (showChat) ChatScreen(onBack = { showChat = false })
                else HomeScreen(onOpenChat = { showChat = true })
            }
        }
    }
}
