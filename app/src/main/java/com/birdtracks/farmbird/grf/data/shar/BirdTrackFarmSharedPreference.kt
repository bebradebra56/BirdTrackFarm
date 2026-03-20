package com.birdtracks.farmbird.grf.data.shar

import android.content.Context
import androidx.core.content.edit

class BirdTrackFarmSharedPreference(context: Context) {
    private val birdTrackFarmPrefs = context.getSharedPreferences("birdTrackFarmSharedPrefsAb", Context.MODE_PRIVATE)

    var birdTrackFarmSavedUrl: String
        get() = birdTrackFarmPrefs.getString(BIRD_TRACK_FARM_SAVED_URL, "") ?: ""
        set(value) = birdTrackFarmPrefs.edit { putString(BIRD_TRACK_FARM_SAVED_URL, value) }

    var birdTrackFarmExpired : Long
        get() = birdTrackFarmPrefs.getLong(BIRD_TRACK_FARM_EXPIRED, 0L)
        set(value) = birdTrackFarmPrefs.edit { putLong(BIRD_TRACK_FARM_EXPIRED, value) }

    var birdTrackFarmAppState: Int
        get() = birdTrackFarmPrefs.getInt(BIRD_TRACK_FARM_APPLICATION_STATE, 0)
        set(value) = birdTrackFarmPrefs.edit { putInt(BIRD_TRACK_FARM_APPLICATION_STATE, value) }

    var birdTrackFarmNotificationRequest: Long
        get() = birdTrackFarmPrefs.getLong(BIRD_TRACK_FARM_NOTIFICAITON_REQUEST, 0L)
        set(value) = birdTrackFarmPrefs.edit { putLong(BIRD_TRACK_FARM_NOTIFICAITON_REQUEST, value) }


    var birdTrackFarmNotificationState:Int
        get() = birdTrackFarmPrefs.getInt(BIRD_TRACK_FARM_NOTIFICATION_STATE, 0)
        set(value) = birdTrackFarmPrefs.edit { putInt(BIRD_TRACK_FARM_NOTIFICATION_STATE, value) }

    companion object {
        private const val BIRD_TRACK_FARM_NOTIFICATION_STATE = "birdTrackFarmNotificationState"
        private const val BIRD_TRACK_FARM_SAVED_URL = "birdTrackFarmSavedUrl"
        private const val BIRD_TRACK_FARM_EXPIRED = "birdTrackFarmExpired"
        private const val BIRD_TRACK_FARM_APPLICATION_STATE = "birdTrackFarmApplicationState"
        private const val BIRD_TRACK_FARM_NOTIFICAITON_REQUEST = "birdTrackFarmNotificationRequest"
    }
}