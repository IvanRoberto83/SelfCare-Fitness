package com.example.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

private val suggestions = listOf(
    "Cara mengecilkan perut di rumah",
    "Peregangan setelah olahraga",
    "Ide menu sarapan sehat"
)

private val UserBubble = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
private val BotBubble = RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(vm: ChatViewModel = viewModel(), onBack: () -> Unit) {
    var input by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scheme = MaterialTheme.colorScheme

    LaunchedEffect(vm.messages.size) {
        if (vm.messages.isNotEmpty()) listState.animateScrollToItem(vm.messages.lastIndex)
    }

    Scaffold(
        containerColor = scheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Tanya Chatbot", fontWeight = FontWeight.Bold)
                        Text("Asisten kesehatan & kebugaran", fontSize = 12.sp, color = scheme.onSurfaceVariant)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", fontSize = 24.sp, color = scheme.onSurface) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = scheme.background)
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).imePadding()) {
            // Disclaimer medis
            Surface(
                color = scheme.tertiaryContainer,
                contentColor = scheme.onTertiaryContainer,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
            ) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("ℹ️", fontSize = 14.sp)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Informasi dari chatbot hanya untuk edukasi dan bukan pengganti saran dokter atau tenaga medis.",
                        fontSize = 12.sp, lineHeight = 16.sp
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (vm.messages.isEmpty()) {
                    item {
                        Column(
                            Modifier.fillMaxWidth().padding(top = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("💬", fontSize = 44.sp)
                            Text(
                                "Tanyakan tips olahraga atau kesehatan, misalnya: gimana cara mengecilkan perut di rumah?",
                                color = scheme.onSurfaceVariant, textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                            )
                            suggestions.forEach { s ->
                                Surface(
                                    onClick = { vm.send(s) },
                                    shape = RoundedCornerShape(50),
                                    color = scheme.primaryContainer,
                                    contentColor = scheme.onPrimaryContainer
                                ) {
                                    Text(
                                        s, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                items(vm.messages) { m ->
                    Box(Modifier.fillMaxWidth(), contentAlignment = if (m.fromUser) Alignment.CenterEnd else Alignment.CenterStart) {
                        Surface(
                            shape = if (m.fromUser) UserBubble else BotBubble,
                            color = when {
                                m.isError -> scheme.errorContainer
                                m.fromUser -> scheme.primary
                                else -> scheme.surfaceVariant
                            },
                            contentColor = when {
                                m.isError -> scheme.onErrorContainer
                                m.fromUser -> scheme.onPrimary
                                else -> scheme.onSurface
                            },
                            modifier = Modifier.widthIn(max = 300.dp)
                        ) {
                            Text(m.text, modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
                        }
                    }
                }
                if (vm.loading) item {
                    Surface(shape = BotBubble, color = scheme.surfaceVariant) {
                        Text(
                            "Mengetik...", color = scheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(26.dp),
                    placeholder = { Text("Tulis pertanyaan...") },
                    maxLines = 3
                )
                Spacer(Modifier.width(8.dp))
                FilledIconButton(
                    onClick = { vm.send(input); input = "" },
                    enabled = input.isNotBlank() && !vm.loading,
                    modifier = Modifier.padding(2.dp)
                ) { Text("↑", fontSize = 22.sp, fontWeight = FontWeight.Bold) }
            }
        }
    }
}
