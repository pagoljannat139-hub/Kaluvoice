package com.app.voicechanger.audio

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.IBinder
import androidx.lifecycle.LifecycleService

class AudioStreamService : LifecycleService() {

    private val voiceModulator = VoiceModulator()

    override fun onCreate() {
        super.onCreate()
        startForeground(1001, createNotification())
        voiceModulator.startEngine()
    }

    private fun createNotification(): Notification {
        val channelId = "voice_service_channel"
        val channel = NotificationChannel(
            channelId, "Active Voice Changer", NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        return Notification.Builder(this, channelId)
            .setContentTitle("Voice Changer Active")
            .setContentText("Modulating microphone stream in background")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()
    }

    override fun onDestroy() {
        voiceModulator.stopEngine()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }
}
