package com.example.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.app.SelfCareApp
import com.example.app.data.DayRecord
import com.example.app.data.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val db = (app as SelfCareApp).db

    private fun monday(): Long =
        LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toEpochDay()

    val weekStart = MutableStateFlow(monday())
    val today = MutableStateFlow(LocalDate.now().toEpochDay())

    // epochDay -> status, hanya untuk minggu berjalan
    @OptIn(ExperimentalCoroutinesApi::class)
    val records: StateFlow<Map<Long, String>> = weekStart
        .flatMapLatest { db.dayRecordDao().observeFrom(it) }
        .map { list -> list.associate { it.epochDay to it.status } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    val notes: StateFlow<List<Note>> = db.noteDao().observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init { refresh() }

    /** Dipanggil saat aplikasi dibuka/dilanjutkan: update tanggal + reset mingguan. */
    fun refresh() {
        val ws = monday()
        weekStart.value = ws
        today.value = LocalDate.now().toEpochDay()
        viewModelScope.launch { db.dayRecordDao().deleteBefore(ws) }
    }

    fun mark(status: String) {
        viewModelScope.launch { db.dayRecordDao().upsert(DayRecord(today.value, status)) }
    }

    fun saveNote(id: Long, text: String) {
        viewModelScope.launch { db.noteDao().upsert(Note(id, text.trim(), System.currentTimeMillis())) }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch { db.noteDao().delete(note) }
    }
}