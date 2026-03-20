package com.birdtracks.farmbird.grf.data.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.appsflyer.AppsFlyerLib
import com.birdtracks.farmbird.grf.presentation.app.BirdTrackFarmApplication
import java.util.Locale

class BirdTrackFarmSystemService(private val context: Context) {

    fun birdTrackFarmGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(context) ?: ""
        Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    fun birdTrackFarmGetLocale() : String {
        return  Locale.getDefault().language
    }

    fun birdTrackFarmIsOnline(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return true
            }
        }
        return false
    }

}