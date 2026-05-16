package com.example.intervaltimer

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.intervaltimer.data.PreferencesManager
import com.example.intervaltimer.service.TimerService
import com.example.intervaltimer.ui.screens.SetupScreen
import com.example.intervaltimer.ui.screens.TimerScreen
import com.example.intervaltimer.ui.theme.IntervalTimerTheme
import com.example.intervaltimer.utils.NotificationUtils
import com.example.intervaltimer.viewmodel.TimerViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: TimerViewModel by viewModels()
    private val preferencesManager by lazy { PreferencesManager(this) }

    // Request notification permission for Android 13+
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted, can post notifications
        }
    }

    // File picker for custom alarm sound
    private val soundPickerLauncher = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { selectedUri ->
            viewModel.setCustomAlarmUri(selectedUri)
        }
    }

    // Broadcast receiver for notification control actions
    private val notificationControlReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                TimerService.ACTION_PAUSE -> viewModel.pauseTimer()
                TimerService.ACTION_RESUME -> viewModel.resumeTimer()
                TimerService.ACTION_SKIP -> viewModel.skipSet()
                TimerService.ACTION_FINISH -> {
                    viewModel.finishTraining()
                    stopTimerService()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Create notification channel
        NotificationUtils.createNotificationChannel(this)

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        // Register broadcast receiver for notification control actions
        val intentFilter = IntentFilter().apply {
            addAction(TimerService.ACTION_PAUSE)
            addAction(TimerService.ACTION_RESUME)
            addAction(TimerService.ACTION_SKIP)
            addAction(TimerService.ACTION_FINISH)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(notificationControlReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(notificationControlReceiver, intentFilter)
        }

        // Load saved settings
        lifecycleScope.launch {
            viewModel.loadSavedSettings(preferencesManager)
        }

        setContent {
            IntervalTimerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        viewModel = viewModel,
                        onPickSound = { soundPickerLauncher.launch(arrayOf("audio/*")) },
                        onStartService = { startTimerService() },
                        onStopService = { stopTimerService() },
                        preferencesManager = preferencesManager
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(notificationControlReceiver)
    }

    private fun startTimerService() {
        val intent = Intent(this, TimerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun stopTimerService() {
        val intent = Intent(this, TimerService::class.java)
        stopService(intent)
    }
}

@Composable
fun MainScreen(
    viewModel: TimerViewModel,
    onPickSound: () -> Unit,
    onStartService: () -> Unit,
    onStopService: () -> Unit,
    preferencesManager: PreferencesManager
) {
    val uiState by viewModel.uiState.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()

    // Listen for changes and save settings when needed
    LaunchedEffect(uiState) {
        if (uiState is TimerUIState.Setup) {
            preferencesManager.saveSettings(
                (uiState as TimerUIState.Setup).settings
            )
        }
    }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            onStartService()
        } else if (!isTimerRunning && uiState is TimerUIState.Timer) {
            // Service continues in background with notification
        }
    }

    when (uiState) {
        is TimerUIState.Setup -> {
            val setupState = uiState as TimerUIState.Setup
            SetupScreen(
                settings = setupState.settings,
                onSettingsChanged = { newSettings ->
                    viewModel.updateSettings(newSettings)
                },
                onStartTimer = {
                    viewModel.startTimer()
                },
                onPickCustomSound = onPickSound,
                selectedSoundUri = setupState.customAlarmUri
            )
        }

        is TimerUIState.Timer -> {
            val timerState = uiState as TimerUIState.Timer
            TimerScreen(
                timeRemaining = timerState.timeRemaining,
                currentSet = timerState.currentSet,
                totalSets = timerState.totalSets,
                isWorkMode = timerState.isWorkMode,
                progress = timerState.progress,
                onPause = {
                    viewModel.pauseTimer()
                },
                onResume = {
                    viewModel.resumeTimer()
                },
                onSkipSet = {
                    viewModel.skipSet()
                },
                onFinish = {
                    viewModel.finishTraining()
                    onStopService()
                },
                isRunning = isTimerRunning
            )
        }
    }
}

sealed class TimerUIState {
    data class Setup(
        val settings: TimerSettings,
        val customAlarmUri: android.net.Uri? = null
    ) : TimerUIState()

    data class Timer(
        val timeRemaining: Long,
        val currentSet: Int,
        val totalSets: Int,
        val isWorkMode: Boolean,
        val progress: Float
    ) : TimerUIState()
}

data class TimerSettings(
    val workDuration: Long = 30000L,  // milliseconds
    val restDuration: Long = 10000L,
    val numberOfSets: Int = 5,
    val useCustomAlarm: Boolean = false,
    val soundType: String = "buzzer"  // "buzzer" or "custom"
)
