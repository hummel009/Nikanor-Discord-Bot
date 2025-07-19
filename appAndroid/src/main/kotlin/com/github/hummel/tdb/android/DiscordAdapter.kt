package com.github.hummel.tdb.android

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import androidx.core.app.NotificationCompat
import com.github.hummel.tdb.core.controller.Controller
import com.github.hummel.tdb.core.controller.impl.ControllerImpl

class DiscordAdapter : Service() {
	private lateinit var wakeLock: WakeLock
	private val controller: Controller = ControllerImpl()

	override fun onCreate() {
		wakeLock = (getSystemService(POWER_SERVICE) as PowerManager).run {
			newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tdb::wake_lock")
		}
		controller.onCreate()
	}

	@SuppressLint("WakelockTimeout")
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		wakeLock.acquire()

		val channelId = "tdb::channel_id"
		val channelName = "tdb::channel_name"
		val notification = NotificationCompat.Builder(this, channelId).run {
			priority = NotificationCompat.PRIORITY_MAX
			setOngoing(true)
		}.build()
		val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
		val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.createNotificationChannel(channel)

		startForeground(1, notification)

		return START_STICKY
	}

	override fun onDestroy() {
		stopForeground(STOP_FOREGROUND_REMOVE)

		wakeLock.release()
	}

	override fun onBind(intent: Intent?): IBinder? = null
}