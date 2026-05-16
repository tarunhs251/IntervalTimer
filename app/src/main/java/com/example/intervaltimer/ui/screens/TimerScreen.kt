package com.example.intervaltimer.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TimerScreen(
    timeRemaining: Long,
    currentSet: Int,
    totalSets: Int,
    isWorkMode: Boolean,
    progress: Float,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onSkipSet: () -> Unit,
    onFinish: () -> Unit,
    isRunning: Boolean
) {
    val modeColor = animateColorAsState(
        targetValue = if (isWorkMode) {
            Color(0xFFE53935)  // Red for work
        } else {
            Color(0xFF43A047)  // Green for rest
        },
        label = "modeColor"
    )

    val backgroundColor = animateColorAsState(
        targetValue = if (isWorkMode) {
            Color(0xFFFCE4EC)  // Light red background
        } else {
            Color(0xFFF1F8E9)  // Light green background
        },
        label = "backgroundColor"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor.value)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
    ) {
        // Mode Indicator
        Card(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(60.dp),
            colors = CardDefaults.cardColors(
                containerColor = modeColor.value
            ),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isWorkMode) "🔥 WORK 🔥" else "😮‍💨 REST 😮‍💨",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    color = Color.White
                )
            }
        }

        // Set Counter
        Text(
            text = "Set $currentSet of $totalSets",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Main Timer Display
        Card(
            modifier = Modifier
                .size(260.dp)
                .padding(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = modeColor.value.copy(alpha = 0.15f)
            ),
            border = CardDefaults.outlinedCardBorder().copy(
                width = 3.dp
            ),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Progress ring (circular)
                CircularProgressIndicator(
                    progress = progress,
                    modifier = Modifier.size(260.dp),
                    color = modeColor.value,
                    strokeWidth = 8.dp,
                    trackColor = Color.LightGray.copy(alpha = 0.3f)
                )

                // Timer text
                Text(
                    text = formatTime(timeRemaining),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 80.sp
                    ),
                    color = modeColor.value
                )
            }
        }

        // Linear progress bar below timer
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = modeColor.value,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Controls Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pause/Resume Button
            FloatingActionButton(
                onClick = {
                    if (isRunning) onPause() else onResume()
                },
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Resume",
                    modifier = Modifier.size(28.dp)
                )
            }

            // Skip Set Button
            FloatingActionButton(
                onClick = onSkipSet,
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Skip Set",
                    modifier = Modifier.size(28.dp)
                )
            }

            // Finish Button
            FloatingActionButton(
                onClick = onFinish,
                modifier = Modifier.size(56.dp),
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = Color.White,
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Finish Training",
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Button Labels
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
        ) {
            Text(
                text = if (isRunning) "Pause" else "Resume",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(56.dp),
                maxLines = 1
            )

            Text(
                text = "Skip",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(56.dp),
                maxLines = 1
            )

            Text(
                text = "Finish",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.width(56.dp),
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status Card
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(horizontal = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Session Status",
                    style = MaterialTheme.typography.labelLarge
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Sets Done",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "${currentSet - 1}/$totalSets",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Status",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = if (isRunning) "Running" else "Paused",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Current",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = if (isWorkMode) "Work" else "Rest",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Format milliseconds to MM:SS display
 */
private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
