package com.birdtracks.farmbird.grf.domain.model

import com.google.gson.annotations.SerializedName


data class BirdTrackFarmEntity (
    @SerializedName("ok")
    val birdTrackFarmOk: String,
    @SerializedName("url")
    val birdTrackFarmUrl: String,
    @SerializedName("expires")
    val birdTrackFarmExpires: Long,
)