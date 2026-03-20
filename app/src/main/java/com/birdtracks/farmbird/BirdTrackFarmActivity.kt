package com.birdtracks.farmbird

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.birdtracks.farmbird.grf.BirdTrackFarmGlobalLayoutUtil
import com.birdtracks.farmbird.grf.birdTrackFarmSetupSystemBars
import com.birdtracks.farmbird.grf.presentation.app.BirdTrackFarmApplication
import com.birdtracks.farmbird.grf.presentation.pushhandler.BirdTrackFarmPushHandler
import org.koin.android.ext.android.inject

class BirdTrackFarmActivity : AppCompatActivity() {

    private val birdTrackFarmPushHandler by inject<BirdTrackFarmPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        birdTrackFarmSetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_bird_track_farm)
        val birdTrackFarmRootView = findViewById<View>(android.R.id.content)
        BirdTrackFarmGlobalLayoutUtil().birdTrackFarmAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(birdTrackFarmRootView) { birdTrackFarmView, birdTrackFarmInsets ->
            val birdTrackFarmSystemBars = birdTrackFarmInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val birdTrackFarmDisplayCutout = birdTrackFarmInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val birdTrackFarmIme = birdTrackFarmInsets.getInsets(WindowInsetsCompat.Type.ime())


            val birdTrackFarmTopPadding = maxOf(birdTrackFarmSystemBars.top, birdTrackFarmDisplayCutout.top)
            val birdTrackFarmLeftPadding = maxOf(birdTrackFarmSystemBars.left, birdTrackFarmDisplayCutout.left)
            val birdTrackFarmRightPadding = maxOf(birdTrackFarmSystemBars.right, birdTrackFarmDisplayCutout.right)
            window.setSoftInputMode(BirdTrackFarmApplication.birdTrackFarmInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "ADJUST PUN")
                val birdTrackFarmBottomInset = maxOf(birdTrackFarmSystemBars.bottom, birdTrackFarmDisplayCutout.bottom)

                birdTrackFarmView.setPadding(birdTrackFarmLeftPadding, birdTrackFarmTopPadding, birdTrackFarmRightPadding, 0)

                birdTrackFarmView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = birdTrackFarmBottomInset
                }
            } else {
                Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "ADJUST RESIZE")

                val birdTrackFarmBottomInset = maxOf(birdTrackFarmSystemBars.bottom, birdTrackFarmDisplayCutout.bottom, birdTrackFarmIme.bottom)

                birdTrackFarmView.setPadding(birdTrackFarmLeftPadding, birdTrackFarmTopPadding, birdTrackFarmRightPadding, 0)

                birdTrackFarmView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = birdTrackFarmBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Activity onCreate()")
        birdTrackFarmPushHandler.birdTrackFarmHandlePush(intent.extras)
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            birdTrackFarmSetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        birdTrackFarmSetupSystemBars()
    }
}