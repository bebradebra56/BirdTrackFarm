package com.birdtracks.farmbird.grf.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.birdtracks.farmbird.grf.data.shar.BirdTrackFarmSharedPreference
import com.birdtracks.farmbird.grf.data.utils.BirdTrackFarmSystemService
import com.birdtracks.farmbird.grf.domain.usecases.BirdTrackFarmGetAllUseCase
import com.birdtracks.farmbird.grf.presentation.app.BirdTrackFarmAppsFlyerState
import com.birdtracks.farmbird.grf.presentation.app.BirdTrackFarmApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BirdTrackFarmLoadViewModel(
    private val birdTrackFarmGetAllUseCase: BirdTrackFarmGetAllUseCase,
    private val birdTrackFarmSharedPreference: BirdTrackFarmSharedPreference,
    private val birdTrackFarmSystemService: BirdTrackFarmSystemService
) : ViewModel() {

    private val _birdTrackFarmHomeScreenState: MutableStateFlow<BirdTrackFarmHomeScreenState> =
        MutableStateFlow(BirdTrackFarmHomeScreenState.BirdTrackFarmLoading)
    val birdTrackFarmHomeScreenState = _birdTrackFarmHomeScreenState.asStateFlow()

    private var birdTrackFarmGetApps = false


    init {
        viewModelScope.launch {
            when (birdTrackFarmSharedPreference.birdTrackFarmAppState) {
                0 -> {
                    if (birdTrackFarmSystemService.birdTrackFarmIsOnline()) {
                        BirdTrackFarmApplication.birdTrackFarmConversionFlow.collect {
                            when(it) {
                                BirdTrackFarmAppsFlyerState.BirdTrackFarmDefault -> {}
                                BirdTrackFarmAppsFlyerState.BirdTrackFarmError -> {
                                    birdTrackFarmSharedPreference.birdTrackFarmAppState = 2
                                    _birdTrackFarmHomeScreenState.value =
                                        BirdTrackFarmHomeScreenState.BirdTrackFarmError
                                    birdTrackFarmGetApps = true
                                }
                                is BirdTrackFarmAppsFlyerState.BirdTrackFarmSuccess -> {
                                    if (!birdTrackFarmGetApps) {
                                        birdTrackFarmGetData(it.birdTrackFarmData)
                                        birdTrackFarmGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _birdTrackFarmHomeScreenState.value =
                            BirdTrackFarmHomeScreenState.BirdTrackFarmNotInternet
                    }
                }
                1 -> {
                    if (birdTrackFarmSystemService.birdTrackFarmIsOnline()) {
                        if (BirdTrackFarmApplication.BIRD_TRACK_FARM_FB_LI != null) {
                            _birdTrackFarmHomeScreenState.value =
                                BirdTrackFarmHomeScreenState.BirdTrackFarmSuccess(
                                    BirdTrackFarmApplication.BIRD_TRACK_FARM_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > birdTrackFarmSharedPreference.birdTrackFarmExpired) {
                            Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Current time more then expired, repeat request")
                            BirdTrackFarmApplication.birdTrackFarmConversionFlow.collect {
                                when(it) {
                                    BirdTrackFarmAppsFlyerState.BirdTrackFarmDefault -> {}
                                    BirdTrackFarmAppsFlyerState.BirdTrackFarmError -> {
                                        _birdTrackFarmHomeScreenState.value =
                                            BirdTrackFarmHomeScreenState.BirdTrackFarmSuccess(
                                                birdTrackFarmSharedPreference.birdTrackFarmSavedUrl
                                            )
                                        birdTrackFarmGetApps = true
                                    }
                                    is BirdTrackFarmAppsFlyerState.BirdTrackFarmSuccess -> {
                                        if (!birdTrackFarmGetApps) {
                                            birdTrackFarmGetData(it.birdTrackFarmData)
                                            birdTrackFarmGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Current time less then expired, use saved url")
                            _birdTrackFarmHomeScreenState.value =
                                BirdTrackFarmHomeScreenState.BirdTrackFarmSuccess(
                                    birdTrackFarmSharedPreference.birdTrackFarmSavedUrl
                                )
                        }
                    } else {
                        _birdTrackFarmHomeScreenState.value =
                            BirdTrackFarmHomeScreenState.BirdTrackFarmNotInternet
                    }
                }
                2 -> {
                    _birdTrackFarmHomeScreenState.value =
                        BirdTrackFarmHomeScreenState.BirdTrackFarmError
                }
            }
        }
    }


    private suspend fun birdTrackFarmGetData(conversation: MutableMap<String, Any>?) {
        val birdTrackFarmData = birdTrackFarmGetAllUseCase.invoke(conversation)
        if (birdTrackFarmSharedPreference.birdTrackFarmAppState == 0) {
            if (birdTrackFarmData == null) {
                birdTrackFarmSharedPreference.birdTrackFarmAppState = 2
                _birdTrackFarmHomeScreenState.value =
                    BirdTrackFarmHomeScreenState.BirdTrackFarmError
            } else {
                birdTrackFarmSharedPreference.birdTrackFarmAppState = 1
                birdTrackFarmSharedPreference.apply {
                    birdTrackFarmExpired = birdTrackFarmData.birdTrackFarmExpires
                    birdTrackFarmSavedUrl = birdTrackFarmData.birdTrackFarmUrl
                }
                _birdTrackFarmHomeScreenState.value =
                    BirdTrackFarmHomeScreenState.BirdTrackFarmSuccess(birdTrackFarmData.birdTrackFarmUrl)
            }
        } else  {
            if (birdTrackFarmData == null) {
                _birdTrackFarmHomeScreenState.value =
                    BirdTrackFarmHomeScreenState.BirdTrackFarmSuccess(
                        birdTrackFarmSharedPreference.birdTrackFarmSavedUrl
                    )
            } else {
                birdTrackFarmSharedPreference.apply {
                    birdTrackFarmExpired = birdTrackFarmData.birdTrackFarmExpires
                    birdTrackFarmSavedUrl = birdTrackFarmData.birdTrackFarmUrl
                }
                _birdTrackFarmHomeScreenState.value =
                    BirdTrackFarmHomeScreenState.BirdTrackFarmSuccess(birdTrackFarmData.birdTrackFarmUrl)
            }
        }
    }


    sealed class BirdTrackFarmHomeScreenState {
        data object BirdTrackFarmLoading : BirdTrackFarmHomeScreenState()
        data object BirdTrackFarmError : BirdTrackFarmHomeScreenState()
        data class BirdTrackFarmSuccess(val data: String) : BirdTrackFarmHomeScreenState()
        data object BirdTrackFarmNotInternet: BirdTrackFarmHomeScreenState()
    }
}