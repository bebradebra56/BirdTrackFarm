package com.birdtracks.farmbird.grf.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.birdtracks.farmbird.grf.presentation.di.birdTrackFarmModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface BirdTrackFarmAppsFlyerState {
    data object BirdTrackFarmDefault : BirdTrackFarmAppsFlyerState
    data class BirdTrackFarmSuccess(val birdTrackFarmData: MutableMap<String, Any>?) :
        BirdTrackFarmAppsFlyerState

    data object BirdTrackFarmError : BirdTrackFarmAppsFlyerState
}

interface BirdTrackFarmAppsApi {
    @Headers("Content-Type: application/json")
    @GET(BIRD_TRACK_FARM_LIN)
    fun birdTrackFarmGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val BIRD_TRACK_FARM_APP_DEV = "84nFJCwLefTMMhLZQgzh63"
private const val BIRD_TRACK_FARM_LIN = "com.birdtracks.farmbird"

class BirdTrackFarmApplication : Application() {


    private var birdTrackFarmIsResumed = false
    ///////
    private var birdTrackFarmConversionTimeoutJob: Job? = null
    private var birdTrackFarmDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        birdTrackFarmSetDebufLogger(appsflyer)
        birdTrackFarmMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        birdTrackFarmExtractDeepMap(p0.deepLink)
                        Log.d(BIRD_TRACK_FARM_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(BIRD_TRACK_FARM_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(BIRD_TRACK_FARM_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            BIRD_TRACK_FARM_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    //////////
                    birdTrackFarmConversionTimeoutJob?.cancel()
                    Log.d(BIRD_TRACK_FARM_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = birdTrackFarmGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.birdTrackFarmGetClient(
                                    devkey = BIRD_TRACK_FARM_APP_DEV,
                                    deviceId = birdTrackFarmGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(BIRD_TRACK_FARM_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    birdTrackFarmResume(
                                        BirdTrackFarmAppsFlyerState.BirdTrackFarmError
                                    )
                                } else {
                                    birdTrackFarmResume(
                                        BirdTrackFarmAppsFlyerState.BirdTrackFarmSuccess(
                                            resp
                                        )
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(BIRD_TRACK_FARM_MAIN_TAG, "Error: ${d.message}")
                                birdTrackFarmResume(BirdTrackFarmAppsFlyerState.BirdTrackFarmError)
                            }
                        }
                    } else {
                        birdTrackFarmResume(
                            BirdTrackFarmAppsFlyerState.BirdTrackFarmSuccess(
                                p0
                            )
                        )
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    /////////
                    birdTrackFarmConversionTimeoutJob?.cancel()
                    Log.d(BIRD_TRACK_FARM_MAIN_TAG, "onConversionDataFail: $p0")
                    birdTrackFarmResume(BirdTrackFarmAppsFlyerState.BirdTrackFarmError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(BIRD_TRACK_FARM_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(BIRD_TRACK_FARM_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, BIRD_TRACK_FARM_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(BIRD_TRACK_FARM_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(BIRD_TRACK_FARM_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        ///////////
        birdTrackFarmStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@BirdTrackFarmApplication)
            modules(
                listOf(
                    birdTrackFarmModule
                )
            )
        }
    }

    private fun birdTrackFarmExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(BIRD_TRACK_FARM_MAIN_TAG, "Extracted DeepLink data: $map")
        birdTrackFarmDeepLinkData = map
    }
    /////////////////

    private fun birdTrackFarmStartConversionTimeout() {
        birdTrackFarmConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!birdTrackFarmIsResumed) {
                Log.d(BIRD_TRACK_FARM_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                birdTrackFarmResume(BirdTrackFarmAppsFlyerState.BirdTrackFarmError)
            }
        }
    }

    private fun birdTrackFarmResume(state: BirdTrackFarmAppsFlyerState) {
        ////////////
        birdTrackFarmConversionTimeoutJob?.cancel()
        if (state is BirdTrackFarmAppsFlyerState.BirdTrackFarmSuccess) {
            val convData = state.birdTrackFarmData ?: mutableMapOf()
            val deepData = birdTrackFarmDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!birdTrackFarmIsResumed) {
                birdTrackFarmIsResumed = true
                birdTrackFarmConversionFlow.value =
                    BirdTrackFarmAppsFlyerState.BirdTrackFarmSuccess(merged)
            }
        } else {
            if (!birdTrackFarmIsResumed) {
                birdTrackFarmIsResumed = true
                birdTrackFarmConversionFlow.value = state
            }
        }
    }

    private fun birdTrackFarmGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(BIRD_TRACK_FARM_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun birdTrackFarmSetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun birdTrackFarmMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun birdTrackFarmGetApi(url: String, client: OkHttpClient?): BirdTrackFarmAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var birdTrackFarmInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val birdTrackFarmConversionFlow: MutableStateFlow<BirdTrackFarmAppsFlyerState> = MutableStateFlow(
            BirdTrackFarmAppsFlyerState.BirdTrackFarmDefault
        )
        var BIRD_TRACK_FARM_FB_LI: String? = null
        const val BIRD_TRACK_FARM_MAIN_TAG = "BirdTrackFarmMainTag"
    }
}