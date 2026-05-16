package com.example.intervaltimer.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import kotlin.math.sin
import kotlin.math.PI

object SoundGenerator {
    /**
     * Generate and play a buzzer tone (880 Hz, 500ms)
     */
    fun playBuzzerTone(context: Context) {
        try {
            val sampleRate = 44100
            val frequency = 880  // 880 Hz tone
            val duration = 500   // 500 ms
            val numSamples = (duration * sampleRate / 1000)

            // Generate sine wave samples
            val samples = ShortArray(numSamples)
            for (i in 0 until numSamples) {
                val sample = (32767 * 0.3 * sin(2 * PI * frequency * i / sampleRate)).toInt()
                samples[i] = sample.toShort()
            }

            // Create AudioTrack for direct PCM playback
            val audioTrack = AudioTrack(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build(),
                AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build(),
                numSamples * 2,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )

            audioTrack.write(samples, 0, samples.size)
            audioTrack.play()

            // Cleanup after playback
            Thread {
                Thread.sleep(duration + 100L)
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
