package com.example.app.reminder

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock

object AlarmHelper {

    /** Membuka aplikasi Jam bawaan HP untuk membuat alarm. Mengembalikan true jika berhasil dibuka. */
    fun openClockApp(context: Context): Boolean {
        // Cara 1: minta aplikasi Jam membuat alarm baru (layar pengaturan alarm terbuka)
        val setAlarm = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, "Waktunya olahraga!")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        try {
            context.startActivity(setAlarm)
            return true
        } catch (e: ActivityNotFoundException) {
            // lanjut ke cara 2
        }

        // Cara 2 (cadangan): buka daftar alarm
        val showAlarms = Intent(AlarmClock.ACTION_SHOW_ALARMS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            context.startActivity(showAlarms)
            true
        } catch (e: ActivityNotFoundException) {
            false
        }
    }
}