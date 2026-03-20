package com.birdtracks.farmbird.grf.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class BirdTrackFarmDataStore : ViewModel(){
    val birdTrackFarmViList: MutableList<BirdTrackFarmVi> = mutableListOf()
    var birdTrackFarmIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var birdTrackFarmContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var birdTrackFarmView: BirdTrackFarmVi

}