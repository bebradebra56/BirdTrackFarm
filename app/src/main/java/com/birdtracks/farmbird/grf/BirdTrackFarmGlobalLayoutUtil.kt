package com.birdtracks.farmbird.grf

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.birdtracks.farmbird.grf.presentation.app.BirdTrackFarmApplication

class BirdTrackFarmGlobalLayoutUtil {

    private var birdTrackFarmMChildOfContent: View? = null
    private var birdTrackFarmUsableHeightPrevious = 0

    fun birdTrackFarmAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        birdTrackFarmMChildOfContent = content.getChildAt(0)

        birdTrackFarmMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val birdTrackFarmUsableHeightNow = birdTrackFarmComputeUsableHeight()
        if (birdTrackFarmUsableHeightNow != birdTrackFarmUsableHeightPrevious) {
            val birdTrackFarmUsableHeightSansKeyboard = birdTrackFarmMChildOfContent?.rootView?.height ?: 0
            val birdTrackFarmHeightDifference = birdTrackFarmUsableHeightSansKeyboard - birdTrackFarmUsableHeightNow

            if (birdTrackFarmHeightDifference > (birdTrackFarmUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(BirdTrackFarmApplication.birdTrackFarmInputMode)
            } else {
                activity.window.setSoftInputMode(BirdTrackFarmApplication.birdTrackFarmInputMode)
            }
//            mChildOfContent?.requestLayout()
            birdTrackFarmUsableHeightPrevious = birdTrackFarmUsableHeightNow
        }
    }

    private fun birdTrackFarmComputeUsableHeight(): Int {
        val r = Rect()
        birdTrackFarmMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}