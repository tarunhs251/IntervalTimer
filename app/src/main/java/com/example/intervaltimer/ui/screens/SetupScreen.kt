package com.example.intervaltimer.ui.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.intervaltimer.TimerSettings

@Composable
fun SetupScreen(
    settings: TimerSettings,
    onSettingsChanged: (TimerSettings) -> Unit,
    onStartTimer: () -> Unit,
    onPickCustomSound: () -> Unit,
    selectedSoundUri: Uri?
) {
    var workDuration by remember { mutableStateOf((settings.workDuration / 1000).toString()) }
    var restDuration by remember { mutableStateOf((settings.restDuration / 1000).toString()) }
    var numberOfSets by remember { mutableStateOf(settings.numberOfSets.toString()) }
    var soundType by remember { mutableStateOf(settings.soundType) }
    var useCustomAlarm by remember { mutableStateOf(settings.useCustomAlarm) }

    // Track when inputs change and update settings
    LaunchedEffect(workDuration, restDuration, numberOfSets, soundType, useCustomAlarm) {
        val newSettings = TimerSettings(
            workDuration = (workDuration.toLongOrNull() ?: 30L) * 1000,
            restDuration = (restDuration.toLongOrNull() ?: 10L) * 1000,
            numberOfSets = numberOfSets.toIntOrNull() ?: 5,
            useCustomAlarm = useCustomAlarm,
            soundType = soundType
        )
        onSettingsChanged(newSettings)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Title
        Text(
            text = "Interval Timer Setup",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Work Duration Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Work Duration",
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = workDuration,
                        onValueChange = { workDuration = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Seconds") },
                        singleLine = true
                    )

                    Text(
                        text = "sec",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                Text(
                    text = "${workDuration.toIntOrNull() ?: 30} seconds of activity",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Rest Duration Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Rest Duration",
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = restDuration,
                        onValueChange = { restDuration = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Seconds") },
                        singleLine = true
                    )

                    Text(
                        text = "sec",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                Text(
                    text = "${restDuration.toIntOrNull() ?: 10} seconds between sets",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Number of Sets Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Number of Sets",
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = numberOfSets,
                        onValueChange = { numberOfSets = it },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Sets") },
                        singleLine = true
                    )

                    Text(
                        text = "sets",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }

                Text(
                    text = "${numberOfSets.toIntOrNull() ?: 5} complete cycles",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Alarm Sound Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Alarm Sound",
                    style = MaterialTheme.typography.labelLarge
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = soundType == "buzzer",
                            onClick = { soundType = "buzzer" }
                        )
                        Text(
                            text = "System Buzzer",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = soundType == "custom",
                            onClick = { soundType = "custom" }
                        )
                        Text(
                            text = "Custom Audio File",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        )
                    }
                }

                if (soundType == "custom") {
                    Button(
                        onClick = onPickCustomSound,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = null,
                            modifier = Modifier
                                .size(18.dp)
                                .padding(end = 8.dp)
                        )
                        Text("Pick Sound")
                    }

                    if (selectedSoundUri != null) {
                        Text(
                            text = "✓ Sound selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Text(
                            text = "No sound selected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Summary
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Session Summary",
                    style = MaterialTheme.typography.labelLarge
                )

                val totalWorkTime = (workDuration.toLongOrNull() ?: 30) * (numberOfSets.toIntOrNull() ?: 5)
                val totalRestTime = (restDuration.toLongOrNull() ?: 10) * ((numberOfSets.toIntOrNull() ?: 5) - 1)
                val totalTime = totalWorkTime + totalRestTime

                Text(
                    text = "${numberOfSets.toIntOrNull() ?: 5} sets × (${workDuration.toIntOrNull() ?: 30}s work + ${restDuration.toIntOrNull() ?: 10}s rest)",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Total time: ${formatSeconds(totalTime)}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Start Button
        Button(
            onClick = onStartTimer,
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(56.dp),
            shape = MaterialTheme.shapes.large
        ) {
            Text(
                text = "Start Training",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun formatSeconds(totalSeconds: Long): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return if (minutes > 0) {
        "$minutes min $seconds sec"
    } else {
        "$seconds sec"
    }
}
