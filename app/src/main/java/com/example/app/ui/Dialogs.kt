package com.example.app.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SkipConfirmDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Text("😴", fontSize = 32.sp) },
        title = { Text("Lewati olahraga hari ini?", fontWeight = FontWeight.Bold) },
        text = { Text("Hari ini akan ditandai merah (dilewati) di Weekly Report. Yakin?") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("Ya, lewati") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

private data class WorkoutItem(val label: String, val required: Boolean)

private val workoutItems = listOf(
    WorkoutItem("Push-up (Target: 30x)", true),
    WorkoutItem("Sit-up / Crunch (Target: 30x)", true),
    WorkoutItem("Plank (Target: 1-2 menit)", true),
    WorkoutItem("Squat (Target: 30x)", true),
    WorkoutItem("Jogging / Kardio di tempat (15-30 menit) - opsional", false)
)

@Composable
fun ChecklistDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    val checked = remember { mutableStateListOf(*Array(workoutItems.size) { false }) }
    // Valid jika semua item wajib sudah dicentang (Jogging boleh kosong)
    val valid = workoutItems.indices.all { !workoutItems[it].required || checked[it] }
    val requiredTotal = workoutItems.count { it.required }
    val requiredDone = workoutItems.indices.count { workoutItems[it].required && checked[it] }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Checklist Olahraga", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    "Wajib selesai: $requiredDone/$requiredTotal",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                workoutItems.forEachIndexed { i, item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { checked[i] = !checked[i] },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(checked = checked[i], onCheckedChange = { checked[i] = it })
                        Text(item.label, fontSize = 15.sp)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onConfirm, enabled = valid) { Text("Konfirmasi") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@Composable
fun NoteDialog(
    initial: String,
    onSave: (String) -> Unit,
    onDelete: (() -> Unit)?,   // null = catatan baru (tidak ada tombol hapus)
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (onDelete == null) "Catatan baru" else "Edit catatan", fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = text, onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(), minLines = 3,
                shape = RoundedCornerShape(16.dp),
                placeholder = { Text("Contoh: minum air 2 liter, tidur jam 10") }
            )
        },
        confirmButton = { TextButton(onClick = { onSave(text) }, enabled = text.isNotBlank()) { Text("Simpan") } },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) {
                        Text("Hapus", color = MaterialTheme.colorScheme.error)
                    }
                }
                TextButton(onClick = onDismiss) { Text("Batal") }
            }
        }
    )
}
