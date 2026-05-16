package com.example.intervaltimer.data

import android.content.Context
import android.content.SharedPreferences
import com.example.intervaltimer.TimerSettings

class PreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    /**
     * Save timer settings to SharedPreferences
     */
    fun saveSettings(settings: TimerSettings) {
        sharedPreferences.edit().apply {
            putLong(KEY_WORK_DURATION, settings.workDuration)
            putLong(KEY_REST_DURATION, settings.restDuration)
            putInt(KEY_NUMBER_OF_SETS, settings.numberOfSets)
            putBoolean(KEY_USE_CUSTOM_ALARM, settings.useCustomAlarm)
            putString(KEY_SOUND_TYPE, settings.soundType)
            apply()
        }
    }

    /**
     * Load timer settings from SharedPreferences
     */
    fun getSettings(): TimerSettings {
        return TimerSettings(
            workDuration = sharedPreferences.getLong(
                KEY_WORK_DURATION,
                DEFAULT_WORK_DURATION
            ),
            restDuration = sharedPreferences.getLong(
                KEY_REST_DURATION,
                DEFAULT_REST_DURATION
            ),
            numberOfSets = sharedPreferences.getInt(
                KEY_NUMBER_OF_SETS,
                DEFAULT_NUMBER_OF_SETS
            ),
            useCustomAlarm = sharedPreferences.getBoolean(
                KEY_USE_CUSTOM_ALARM,
                false
            ),
            soundType = sharedPreferences.getString(
                KEY_SOUND_TYPE,
                "buzzer"
            ) ?: "buzzer"
        )
    }

    /**
     * Save custom alarm sound URI
     */
    fun saveCustomAlarmUri(uri: String) {
        sharedPreferences.edit().apply {
            putString(KEY_CUSTOM_ALARM_URI, uri)
            apply()
        }
    }

    /**
     * Get custom alarm sound URI
     */
    fun getCustomAlarmUri(): String? {
        return sharedPreferences.getString(KEY_CUSTOM_ALARM_URI, null)
    }

    /**
     * Clear all saved settings
     */
    fun clearSettings() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "interval_timer_prefs"

        private const val KEY_WORK_DURATION = "work_duration"
        private const val KEY_REST_DURATION = "rest_duration"
        private const val KEY_NUMBER_OF_SETS = "number_of_sets"
        private const val KEY_USE_CUSTOM_ALARM = "use_custom_alarm"
        private const val KEY_SOUND_TYPE = "sound_type"
        private const val KEY_CUSTOM_ALARM_URI = "custom_alarm_uri"

        private const val DEFAULT_WORK_DURATION = 30000L  // 30 seconds
        private const val DEFAULT_REST_DURATION = 10000L  // 10 seconds
        private const val DEFAULT_NUMBER_OF_SETS = 5
    }
}
