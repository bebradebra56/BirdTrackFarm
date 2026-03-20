package com.birdtracks.farmbird.grf.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.birdtracks.farmbird.grf.presentation.app.BirdTrackFarmApplication

class BirdTrackFarmPushHandler {
    fun birdTrackFarmHandlePush(extras: Bundle?) {
        Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = birdTrackFarmBundleToMap(extras)
            Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    BirdTrackFarmApplication.BIRD_TRACK_FARM_FB_LI = map["url"]
                    Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Push data no!")
        }
    }

    private fun birdTrackFarmBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}