package com.example.intervaltimer.viewmodel

import android.app.Application
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.intervaltimer.MainActivity
import com.example.intervaltimer.TimerSettings
import com.example.intervaltimer.TimerUIState
import com.example.intervaltimer.data.PreferencesManager
import com.example.intervaltimer.service.TimerService
import com.example.intervaltimer.utils.SoundGenerator
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.min

class TimerViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private var wakeLock: PowerManager.WakeLock? = null

    // UI State
    private val _uiState = MutableStateFlow<TimerUIState>(
        TimerUIState.Setup(TimerSettings())
    )
    val uiState: StateFlow<TimerUIState> = _uiState.asStateFlow()

    // Timer control
    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    // Settings
    private var currentSettings = TimerSettings()
    private var customAlarmUri: Uri? = null

    // Timer state
    private var timerJob: Job? = null
    private var timeRemaining = 0L
    private var currentSet = 1
    private var isWorkMode = true
    private var mediaPlayer: MediaPlayer? = null
    private var isPaused = false

    /**
     * Load saved settings from SharedPreferences
     */
    suspend fun loadSavedSettings(preferencesManager: PreferencesManager) {
        withContext(Dispatchers.Default) {
            currentSettings = preferencesManager.getSettings()
            _uiState.value = TimerUIState.Setup(
                settings = currentSettings,
                customAlarmUri = customAlarmUri
            )
        }
    }

    /**
     * Update settings in Setup screen
     */
    fun updateSettings(newSettings: TimerSettings) {
        currentSettings = newSettings
        _uiState.value = TimerUIState.Setup(
            settings = newSettings,
            customAlarmUri = customAlarmUri
        )
    }

    /**
     * Set custom alarm sound URI from file picker
     */
    fun setCustomAlarmUri(uri: Uri) {
        customAlarmUri = uri
        val updatedSettings = currentSettings.copy(useCustomAlarm = true, soundType = "custom")
        updateSettings(updatedSettings)
    }

    /**
     * Start the timer session
     */
    fun startTimer() {
        if (_isTimerRunning.value) return

        currentSet = 1
        isWorkMode = true
        timeRemaining = currentSettings.workDuration
        isPaused = false

        _isTimerRunning.value = true
        acquireWakeLock()
        startTimerTick()

        // Update UI to Timer screen
        updateTimerDisplay()
    }

    /**
     * Main timer loop
     */
    private fun startTimerTick() {
        timerJob = viewModelScope.launch {
            while (isActive && _isTimerRunning.value) {
                if (!isPaused) {
                    timeRemaining -= 100L
                    updateTimerDisplay()

                    // Check if interval finished
                    if (timeRemaining <= 0) {
                        playAlarmSound()
                        switchInterval()
                    }
                }
                delay(100)
            }
        }
    }

    /**
     * Switch between work and rest, or move to next set
     */
    private fun switchInterval() {
        if (isWorkMode) {
            // Finished work, start rest
            isWorkMode = false
            timeRemaining = currentSettings.restDuration
        } else {
            // Finished rest
            currentSet++
            if (currentSet > currentSettings.numberOfSets) {
                // All sets completed
                finishTraining()
            } else {
                // Start next work interval
                isWorkMode = true
                timeRemaining = currentSettings.workDuration
            }
        }
        updateTimerDisplay()
    }

    /**
     * Pause the timer
     */
    fun pauseTimer() {
        isPaused = true
        _isTimerRunning.value = false
        releaseWakeLock()
    }

    /**
     * Resume the timer
     */
    fun resumeTimer() {
        if (!isPaused) return
        isPaused = false
        _isTimerRunning.value = true
        acquireWakeLock()
    }

    /**
     * Skip to next interval
     */
    fun skipSet() {
        playAlarmSound()
        switchInterval()
    }

    /**
     * Finish training early
     */
    fun finishTraining() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        releaseWakeLock()
        mediaPlayer?.release()
        mediaPlayer = null

        // Return to Setup screen
        _uiState.value = TimerUIState.Setup(
            settings = currentSettings,
            customAlarmUri = customAlarmUri
        )
    }

    /**
     * Update the Timer screen display
     */
    private fun updateTimerDisplay() {
        if (_isTimerRunning.value || isPaused) {
            val totalDuration = if (isWorkMode) {
                currentSettings.workDuration
            } else {
                currentSettings.restDuration
            }

            val progress = if (timeRemaining <= 0) {
                1f
            } else {
                1f - (timeRemaining.toFloat() / totalDuration)
            }

            _uiState.value = TimerUIState.Timer(
                timeRemaining = maxOf(0, timeRemaining),
                currentSet = currentSet,
                totalSets = currentSettings.numberOfSets,
                isWorkMode = isWorkMode,
                progress = min(1f, progress)
            )

            // Update notification with current timer state
            updateNotification()
        }
    }

    /**
     * Update the foreground notification with current timer state
     */
    private fun updateNotification() {
        try {
            val timeStr = formatTime(timeRemaining)
            val modeText = if (isWorkMode) "🔥 WORK" else "😮‍💨 REST"
            val setInfo = "Set $currentSet of ${currentSettings.numberOfSets}"
            val statusText = if (_isTimerRunning.value) "Running" else "Paused"

            val notification = buildNotification(timeStr, modeText, setInfo, statusText, _isTimerRunning.value)
            notificationManager.notify(TimerService.NOTIFICATION_ID, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Build the notification with current timer state
     */
    private fun buildNotification(
        timeStr: String,
        modeText: String,
        setInfo: String,
        statusText: String,
        isRunning: Boolean
    ): Notification {
        val builder = NotificationCompat.Builder(context, TimerService.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Interval Timer - $statusText")
            .setContentText("$modeText • $timeStr • $setInfo")
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$modeText\n$timeStr\n$setInfo")
            )

        // Add action buttons
        if (isRunning) {
            builder.addAction(
                android.R.drawable.ic_media_pause,
                "Pause",
                createActionIntent(TimerService.ACTION_PAUSE)
            )
        } else {
            builder.addAction(
                android.R.drawable.ic_media_play,
                "Resume",
                createActionIntent(TimerService.ACTION_RESUME)
            )
        }

        builder.addAction(
            android.R.drawable.ic_menu_more,
            "Skip",
            createActionIntent(TimerService.ACTION_SKIP)
        )

        builder.addAction(
            android.R.drawable.ic_menu_close_clear_cancel,
            "Finish",
            createActionIntent(TimerService.ACTION_FINISH)
        )

        return builder.build()
    }

    /**
     * Create a PendingIntent for notification action
     */
    private fun createActionIntent(action: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            this.action = action
        }
        return PendingIntent.getActivity(
            context,
            action.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Format milliseconds to MM:SS
     */
    private fun formatTime(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * Play alarm sound (buzzer or custom)
     */
    private fun playAlarmSound() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (currentSettings.soundType == "custom" && customAlarmUri != null) {
                    // Use custom sound
                    mediaPlayer?.release()
                    mediaPlayer = null

                    mediaPlayer = MediaPlayer().apply {
                        setDataSource(context, customAlarmUri!!)
                        prepare()
                        start()

                        // Auto-release after sound finishes
                        setOnCompletionListener {
                            it.release()
                            if (mediaPlayer == it) {
                                mediaPlayer = null
                            }
                        }
                    }
                } else {
                    // Use system buzzer tone via SoundGenerator
                    SoundGenerator.playBuzzerTone(context)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Fallback: use buzzer
                try {
                    SoundGenerator.playBuzzerTone(context)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }
    }

    /**
     * Acquire wake lock to keep screen on
     */
    private fun acquireWakeLock() {
        if (wakeLock == null) {
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "IntervalTimer:timerWakeLock"
            ).apply {
                acquire(10 * 60 * 1000)  // 10 minute timeout
            }
        }
    }

    /**
     * Release wake lock
     */
    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        wakeLock = null
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        mediaPlayer?.release()
        releaseWakeLock()
    }
}
