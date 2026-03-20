package com.birdtracks.farmbird.grf.domain.usecases

import android.util.Log
import com.birdtracks.farmbird.grf.data.repo.BirdTrackFarmRepository
import com.birdtracks.farmbird.grf.data.utils.BirdTrackFarmPushToken
import com.birdtracks.farmbird.grf.data.utils.BirdTrackFarmSystemService
import com.birdtracks.farmbird.grf.domain.model.BirdTrackFarmEntity
import com.birdtracks.farmbird.grf.domain.model.BirdTrackFarmParam
import com.birdtracks.farmbird.grf.presentation.app.BirdTrackFarmApplication

class BirdTrackFarmGetAllUseCase(
    private val birdTrackFarmRepository: BirdTrackFarmRepository,
    private val birdTrackFarmSystemService: BirdTrackFarmSystemService,
    private val birdTrackFarmPushToken: BirdTrackFarmPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : BirdTrackFarmEntity?{
        val params = BirdTrackFarmParam(
            birdTrackFarmLocale = birdTrackFarmSystemService.birdTrackFarmGetLocale(),
            birdTrackFarmPushToken = birdTrackFarmPushToken.birdTrackFarmGetToken(),
            birdTrackFarmAfId = birdTrackFarmSystemService.birdTrackFarmGetAppsflyerId()
        )
        Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Params for request: $params")
        return birdTrackFarmRepository.birdTrackFarmGetClient(params, conversion)
    }



}