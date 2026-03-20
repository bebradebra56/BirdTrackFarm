package com.birdtracks.farmbird.grf

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.core.view.WindowCompat

class BirdTrackFarmSystemBarsController(private val activity: Activity) {

    private val birdTrackFarmWindow = activity.window
    private val birdTrackFarmDecorView = birdTrackFarmWindow.decorView

    fun birdTrackFarmSetupSystemBars() {
        val isLandscape = activity.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            birdTrackFarmSetupForApi30Plus(isLandscape)
        } else {
            birdTrackFarmSetupForApi24To29(isLandscape)
        }
    }

    private fun birdTrackFarmSetupForApi30Plus(isLandscape: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowCompat.setDecorFitsSystemWindows(birdTrackFarmWindow, false)

            val insetsController = birdTrackFarmWindow.insetsController ?: return

            insetsController.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (isLandscape) {
                insetsController.hide(
                    WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars()
                )
            } else {
                insetsController.hide(WindowInsets.Type.navigationBars())
                insetsController.show(WindowInsets.Type.statusBars())
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun birdTrackFarmSetupForApi24To29(isLandscape: Boolean) {
        var flags = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        if (isLandscape) {
            flags = flags or (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        } else {
            birdTrackFarmWindow.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        birdTrackFarmDecorView.systemUiVisibility = flags

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            birdTrackFarmWindow.navigationBarColor = android.graphics.Color.TRANSPARENT
            // Светлые иконки навигации (если нужно)
            birdTrackFarmDecorView.systemUiVisibility = flags and
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
        }
    }


}

fun Activity.birdTrackFarmSetupSystemBars() {
    BirdTrackFarmSystemBarsController(this).birdTrackFarmSetupSystemBars()
}
