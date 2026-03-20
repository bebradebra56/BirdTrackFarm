package com.birdtracks.farmbird.grf.domain.model

import com.google.gson.annotations.SerializedName


private const val BIRD_TRACK_FARM_A = "com.birdtracks.farmbird"
private const val BIRD_TRACK_FARM_B = "birdtrackfarm-2f34c"
data class BirdTrackFarmParam (
    @SerializedName("af_id")
    val birdTrackFarmAfId: String,
    @SerializedName("bundle_id")
    val birdTrackFarmBundleId: String = BIRD_TRACK_FARM_A,
    @SerializedName("os")
    val birdTrackFarmOs: String = "Android",
    @SerializedName("store_id")
    val birdTrackFarmStoreId: String = BIRD_TRACK_FARM_A,
    @SerializedName("locale")
    val birdTrackFarmLocale: String,
    @SerializedName("push_token")
    val birdTrackFarmPushToken: String,
    @SerializedName("firebase_project_id")
    val birdTrackFarmFirebaseProjectId: String = BIRD_TRACK_FARM_B,

    )