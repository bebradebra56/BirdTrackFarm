package com.birdtracks.farmbird.grf.data.utils

import android.util.Log
import com.birdtracks.farmbird.grf.presentation.app.BirdTrackFarmApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception

class BirdTrackFarmPushToken {

    suspend fun birdTrackFarmGetToken(
        birdTrackFarmMaxAttempts: Int = 3,
        birdTrackFarmDelayMs: Long = 1500
    ): String {

        repeat(birdTrackFarmMaxAttempts - 1) {
            try {
                val birdTrackFarmToken = FirebaseMessaging.getInstance().token.await()
                return birdTrackFarmToken
            } catch (e: Exception) {
                Log.e(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(birdTrackFarmDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}