package com.birdtracks.farmbird.grf.presentation.di

import com.birdtracks.farmbird.grf.data.repo.BirdTrackFarmRepository
import com.birdtracks.farmbird.grf.data.shar.BirdTrackFarmSharedPreference
import com.birdtracks.farmbird.grf.data.utils.BirdTrackFarmPushToken
import com.birdtracks.farmbird.grf.data.utils.BirdTrackFarmSystemService
import com.birdtracks.farmbird.grf.domain.usecases.BirdTrackFarmGetAllUseCase
import com.birdtracks.farmbird.grf.presentation.pushhandler.BirdTrackFarmPushHandler
import com.birdtracks.farmbird.grf.presentation.ui.load.BirdTrackFarmLoadViewModel
import com.birdtracks.farmbird.grf.presentation.ui.view.BirdTrackFarmViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val birdTrackFarmModule = module {
    factory {
        BirdTrackFarmPushHandler()
    }
    single {
        BirdTrackFarmRepository()
    }
    single {
        BirdTrackFarmSharedPreference(get())
    }
    factory {
        BirdTrackFarmPushToken()
    }
    factory {
        BirdTrackFarmSystemService(get())
    }
    factory {
        BirdTrackFarmGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        BirdTrackFarmViFun(get())
    }
    viewModel {
        BirdTrackFarmLoadViewModel(get(), get(), get())
    }
}