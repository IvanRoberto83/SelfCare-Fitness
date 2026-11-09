package com.example.app.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.app.data.Note
import com.example.app.data.Status
import com.example.app.reminder.AlarmHelper
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.app.R

private val CardShape = RoundedCornerShape(24.dp)
private val dayNames = listOf("Sen", "Sel", "Rab", "Kam", "Jum", "Sab", "Min")

@Composable
fun HomeScreen(vm: HomeViewModel = viewModel(), onOpenChat: () -> Unit) {
    val context = LocalContext.current
    val records by vm.records.collectAsStateWithLifecycle()
    val notes by vm.notes.collectAsStateWithLifecycle()
    val weekStart by vm.weekStart.collectAsStateWithLifecycle()
    val today by vm.today.collectAsStateWithLifecycle()

    var showSkip by remember { mutableStateOf(false) }
    var showChecklist by remember { mutableStateOf(false) }
    var showNewNote by remember { mutableStateOf(false) }
    var editNote by remember { mutableStateOf<Note?>(null) }

    // Reset mingguan + update tanggal setiap aplikasi dilanjutkan
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { vm.refresh() }

    val todayStatus = records[today]
    val doneCount = (0..6).count { records[weekStart + it] == Status.DONE }
    val scheme = MaterialTheme.colorScheme

    Scaffold(
        containerColor = scheme.background,
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(scheme.primary, scheme.primary.copy(alpha = 0.78f))),
                        RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TODO: ganti emoji ini dengan logo aplikasi (Image(painterResource(R.drawable.logo), ...))
                Image(
                    painter = painterResource(R.drawable.icon_round),
                    contentDescription = "Logo SelfCare Fitness",
                    modifier = Modifier.size(36.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(10.dp))
                Text("SelfCare Fitness", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = scheme.onPrimary)
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onOpenChat,
                containerColor = scheme.primaryContainer,
                contentColor = scheme.onPrimaryContainer,
                icon = { Text("💬", fontSize = 20.sp) },
                text = { Text("Tanya Chatbot", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- Status harian ---
            val (emoji, container, content) = when (todayStatus) {
                Status.DONE -> Triple("🔥", scheme.primaryContainer, scheme.onPrimaryContainer)
                Status.SKIPPED -> Triple("😴", scheme.tertiaryContainer, scheme.onTertiaryContainer)
                else -> Triple("🏃", scheme.surface, scheme.onSurface)
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = container, contentColor = content),
                border = if (todayStatus == null) BorderStroke(1.dp, scheme.outlineVariant) else null
            ) {
                Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(emoji, fontSize = 40.sp)
                    Spacer(Modifier.width(16.dp))
                    Text(
                        when (todayStatus) {
                            Status.DONE -> "Hebat! Hari ini kamu sudah berolahraga!"
                            Status.SKIPPED -> "Hari ini kamu istirahat. Semangat besok!"
                            else -> "Hari ini kamu belum berolahraga!"
                        },
                        fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp
                    )
                }
            }

            if (todayStatus != Status.DONE) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilledTonalButton(
                        onClick = { showSkip = true },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = scheme.tertiaryContainer,
                            contentColor = scheme.onTertiaryContainer
                        )
                    ) { Text("Besok saja!", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    Button(
                        onClick = { showChecklist = true },
                        modifier = Modifier.weight(1f).height(56.dp)
                    ) { Text("Sudah kok!", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                }
            }

            Card(
                onClick = {
                    if (!AlarmHelper.openClockApp(context)) {
                        Toast.makeText(context, "Aplikasi Jam tidak ditemukan di HP ini.", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = scheme.surface),
                border = BorderStroke(1.dp, scheme.outlineVariant)
            ) {
                Row(Modifier.padding(horizontal = 20.dp, vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier.size(44.dp).clip(CircleShape).background(scheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) { Text("⏰", fontSize = 20.sp) }
                    Spacer(Modifier.width(14.dp))
                    Text(
                        "Aku akan olahraga, ingatkan aku!",
                        fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = scheme.onSurface
                    )
                }
            }

            // --- Weekly Report ---
            SectionTitle("Weekly Report")
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = CardShape,
                colors = CardDefaults.cardColors(containerColor = scheme.surface),
                border = BorderStroke(1.dp, scheme.outlineVariant)
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        for (i in 0..6) {
                            val day = weekStart + i
                            val status = records[day]
                            val bg = when (status) {
                                Status.DONE -> scheme.primary
                                Status.SKIPPED -> scheme.tertiary
                                else -> scheme.surfaceVariant
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    Modifier.size(38.dp).clip(CircleShape).background(bg)
                                        .then(
                                            if (day == today) Modifier.border(2.5.dp, scheme.onSurface, CircleShape)
                                            else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (status) {
                                        Status.DONE -> Text("✓", color = scheme.onPrimary, fontWeight = FontWeight.Bold)
                                        Status.SKIPPED -> Text("✕", color = scheme.onTertiary, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    dayNames[i], fontSize = 12.sp,
                                    fontWeight = if (day == today) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (day == today) scheme.onSurface else scheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            if (doneCount >= 5) "Kamu sudah mencapai target! 5/5"
                            else "Progress minggu ini: $doneCount/5",
                            fontSize = 15.sp, fontWeight = FontWeight.Bold, color = scheme.onSurface
                        )
                        LinearProgressIndicator(
                            progress = { (doneCount / 5f).coerceAtMost(1f) },
                            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                            color = scheme.primary,
                            trackColor = scheme.surfaceVariant,
                            drawStopIndicator = {}
                        )
                    }
                }
            }

            // --- Notes Pengingat ---
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f)) { SectionTitle("Notes Pengingat") }
                TextButton(onClick = { showNewNote = true }) { Text("+ Tambah catatan") }
            }
            if (notes.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = scheme.surface),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Text(
                        "Belum ada catatan.", color = scheme.onSurfaceVariant,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
            notes.forEach { n ->
                Card(
                    onClick = { editNote = n },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CardShape,
                    colors = CardDefaults.cardColors(containerColor = scheme.surface),
                    border = BorderStroke(1.dp, scheme.outlineVariant)
                ) {
                    Text(
                        n.text, maxLines = 3, overflow = TextOverflow.Ellipsis, color = scheme.onSurface,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)
                    )
                }
            }
        }
    }

    // --- Dialog ---
    if (showSkip) SkipConfirmDialog(
        onConfirm = { vm.mark(Status.SKIPPED); showSkip = false },
        onDismiss = { showSkip = false }
    )
    if (showChecklist) ChecklistDialog(
        onConfirm = { vm.mark(Status.DONE); showChecklist = false },
        onDismiss = { showChecklist = false }
    )

    if (showNewNote) NoteDialog(
        initial = "", onDelete = null,
        onSave = { vm.saveNote(0, it); showNewNote = false },
        onDismiss = { showNewNote = false }
    )
    editNote?.let { n ->
        NoteDialog(
            initial = n.text,
            onSave = { vm.saveNote(n.id, it); editNote = null },
            onDelete = { vm.deleteNote(n); editNote = null },
            onDismiss = { editNote = null }
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
}
