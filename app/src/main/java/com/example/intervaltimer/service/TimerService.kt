package com.example.intervaltimer.service

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.intervaltimer.MainActivity
import com.example.intervaltimer.R
import com.example.intervaltimer.utils.NotificationUtils

class TimerService : Service() {
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    companion object {
        const val CHANNEL_ID = NotificationUtils.CHANNEL_ID
        const val NOTIFICATION_ID = 1
        const val ACTION_PAUSE = "com.example.intervaltimer.ACTION_PAUSE"
        const val ACTION_RESUME = "com.example.intervaltimer.ACTION_RESUME"
        const val ACTION_SKIP = "com.example.intervaltimer.ACTION_SKIP"
        const val ACTION_FINISH = "com.example.intervaltimer.ACTION_FINISH"
    }

    override fun onCreate() {
        super.onCreate()
        NotificationUtils.createNotificationChannel(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PAUSE -> {
                sendBroadcast(Intent(ACTION_PAUSE))
            }
            ACTION_RESUME -> {
                sendBroadcast(Intent(ACTION_RESUME))
            }
            ACTION_SKIP -> {
                sendBroadcast(Intent(ACTION_SKIP))
            }
            ACTION_FINISH -> {
                sendBroadcast(Intent(ACTION_FINISH))
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            else -> {
                // Start timer service
                val notification = createNotification("00:00", "Set 1 of 5", true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                    )
                } else {
                    startForeground(NOTIFICATION_ID, notification)
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /**
     * Create or update the foreground notification
     */
    fun updateNotification(
        timeRemaining: String,
        setInfo: String,
        isRunning: Boolean,
        isWorkMode: Boolean
    ) {
        val notification = createNotification(timeRemaining, setInfo, isRunning, isWorkMode)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * Build the notification with controls
     */
    private fun createNotification(
        timeRemaining: String,
        setInfo: String,
        isRunning: Boolean,
        isWorkMode: Boolean = true
    ): Notification {
        val mainIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, TimerService::class.java).setAction(ACTION_PAUSE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val resumeIntent = PendingIntent.getService(
            this,
            2,
            Intent(this, TimerService::class.java).setAction(ACTION_RESUME),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val skipIntent = PendingIntent.getService(
            this,
            3,
            Intent(this, TimerService::class.java).setAction(ACTION_SKIP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val finishIntent = PendingIntent.getService(
            this,
            4,
            Intent(this, TimerService::class.java).setAction(ACTION_FINISH),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val modeText = if (isWorkMode) "🔥 WORK" else "😮‍💨 REST"
        val statusText = if (isRunning) "Running" else "Paused"

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Interval Timer - $statusText")
            .setContentText("$modeText • $timeRemaining • $setInfo")
            .setContentIntent(mainIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$modeText\n$timeRemaining\n$setInfo")
            )

        // Add controls based on running state
        if (isRunning) {
            builder.addAction(
                android.R.drawable.ic_media_pause,
                "Pause",
                pauseIntent
            )
        } else {
            builder.addAction(
                android.R.drawable.ic_media_play,
                "Resume",
                resumeIntent
            )
        }

        builder.addAction(
            android.R.drawable.ic_menu_more,
            "Skip",
            skipIntent
        )

        builder.addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Finish",
            finishIntent
        )

        return builder.build()
    }
}
