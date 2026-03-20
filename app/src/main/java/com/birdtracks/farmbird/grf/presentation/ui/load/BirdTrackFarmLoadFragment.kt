package com.birdtracks.farmbird.grf.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.birdtracks.farmbird.MainActivity
import com.birdtracks.farmbird.R
import com.birdtracks.farmbird.databinding.FragmentLoadBirdTrackFarmBinding
import com.birdtracks.farmbird.grf.data.shar.BirdTrackFarmSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class BirdTrackFarmLoadFragment : Fragment(R.layout.fragment_load_bird_track_farm) {
    private lateinit var birdTrackFarmLoadBinding: FragmentLoadBirdTrackFarmBinding

    private val birdTrackFarmLoadViewModel by viewModel<BirdTrackFarmLoadViewModel>()

    private val birdTrackFarmSharedPreference by inject<BirdTrackFarmSharedPreference>()

    private var birdTrackFarmUrl = ""

    private val birdTrackFarmRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        birdTrackFarmSharedPreference.birdTrackFarmNotificationState = 2
        birdTrackFarmNavigateToSuccess(birdTrackFarmUrl)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        birdTrackFarmLoadBinding = FragmentLoadBirdTrackFarmBinding.bind(view)

        birdTrackFarmLoadBinding.birdTrackFarmGrandButton.setOnClickListener {
            val birdTrackFarmPermission = Manifest.permission.POST_NOTIFICATIONS
            birdTrackFarmRequestNotificationPermission.launch(birdTrackFarmPermission)
        }

        birdTrackFarmLoadBinding.birdTrackFarmSkipButton.setOnClickListener {
            birdTrackFarmSharedPreference.birdTrackFarmNotificationState = 1
            birdTrackFarmSharedPreference.birdTrackFarmNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            birdTrackFarmNavigateToSuccess(birdTrackFarmUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                birdTrackFarmLoadViewModel.birdTrackFarmHomeScreenState.collect {
                    when (it) {
                        is BirdTrackFarmLoadViewModel.BirdTrackFarmHomeScreenState.BirdTrackFarmLoading -> {

                        }

                        is BirdTrackFarmLoadViewModel.BirdTrackFarmHomeScreenState.BirdTrackFarmError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is BirdTrackFarmLoadViewModel.BirdTrackFarmHomeScreenState.BirdTrackFarmSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val birdTrackFarmNotificationState = birdTrackFarmSharedPreference.birdTrackFarmNotificationState
                                when (birdTrackFarmNotificationState) {
                                    0 -> {
                                        birdTrackFarmLoadBinding.birdTrackFarmNotiGroup.visibility = View.VISIBLE
                                        birdTrackFarmLoadBinding.birdTrackFarmLoadingGroup.visibility = View.GONE
                                        birdTrackFarmUrl = it.data
                                    }
                                    1 -> {
                                        if (System.currentTimeMillis() / 1000 > birdTrackFarmSharedPreference.birdTrackFarmNotificationRequest) {
                                            birdTrackFarmLoadBinding.birdTrackFarmNotiGroup.visibility = View.VISIBLE
                                            birdTrackFarmLoadBinding.birdTrackFarmLoadingGroup.visibility = View.GONE
                                            birdTrackFarmUrl = it.data
                                        } else {
                                            birdTrackFarmNavigateToSuccess(it.data)
                                        }
                                    }
                                    2 -> {
                                        birdTrackFarmNavigateToSuccess(it.data)
                                    }
                                }
                            } else {
                                birdTrackFarmNavigateToSuccess(it.data)
                            }
                        }

                        BirdTrackFarmLoadViewModel.BirdTrackFarmHomeScreenState.BirdTrackFarmNotInternet -> {
                            birdTrackFarmLoadBinding.birdTrackFarmStateGroup.visibility = View.VISIBLE
                            birdTrackFarmLoadBinding.birdTrackFarmLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun birdTrackFarmNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_birdTrackFarmLoadFragment_to_birdTrackFarmV,
            bundleOf(BIRD_TRACK_FARM_D to data)
        )
    }

    companion object {
        const val BIRD_TRACK_FARM_D = "birdTrackFarmData"
    }
}