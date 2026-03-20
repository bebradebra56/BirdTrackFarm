package com.birdtracks.farmbird.grf.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.birdtracks.farmbird.grf.presentation.app.BirdTrackFarmApplication
import com.birdtracks.farmbird.grf.presentation.ui.load.BirdTrackFarmLoadFragment
import org.koin.android.ext.android.inject

class BirdTrackFarmV : Fragment(){

    private lateinit var birdTrackFarmPhoto: Uri
    private var birdTrackFarmFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val birdTrackFarmTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        birdTrackFarmFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        birdTrackFarmFilePathFromChrome = null
    }

    private val birdTrackFarmTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            birdTrackFarmFilePathFromChrome?.onReceiveValue(arrayOf(birdTrackFarmPhoto))
            birdTrackFarmFilePathFromChrome = null
        } else {
            birdTrackFarmFilePathFromChrome?.onReceiveValue(null)
            birdTrackFarmFilePathFromChrome = null
        }
    }

    private val birdTrackFarmDataStore by activityViewModels<BirdTrackFarmDataStore>()


    private val birdTrackFarmViFun by inject<BirdTrackFarmViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (birdTrackFarmDataStore.birdTrackFarmView.canGoBack()) {
                        birdTrackFarmDataStore.birdTrackFarmView.goBack()
                        Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "WebView can go back")
                    } else if (birdTrackFarmDataStore.birdTrackFarmViList.size > 1) {
                        Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "WebView can`t go back")
                        birdTrackFarmDataStore.birdTrackFarmViList.removeAt(birdTrackFarmDataStore.birdTrackFarmViList.lastIndex)
                        Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "WebView list size ${birdTrackFarmDataStore.birdTrackFarmViList.size}")
                        birdTrackFarmDataStore.birdTrackFarmView.destroy()
                        val previousWebView = birdTrackFarmDataStore.birdTrackFarmViList.last()
                        birdTrackFarmAttachWebViewToContainer(previousWebView)
                        birdTrackFarmDataStore.birdTrackFarmView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (birdTrackFarmDataStore.birdTrackFarmIsFirstCreate) {
            birdTrackFarmDataStore.birdTrackFarmIsFirstCreate = false
            birdTrackFarmDataStore.birdTrackFarmContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return birdTrackFarmDataStore.birdTrackFarmContainerView
        } else {
            return birdTrackFarmDataStore.birdTrackFarmContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "onViewCreated")
        if (birdTrackFarmDataStore.birdTrackFarmViList.isEmpty()) {
            birdTrackFarmDataStore.birdTrackFarmView = BirdTrackFarmVi(requireContext(), object :
                BirdTrackFarmCallBack {
                override fun birdTrackFarmHandleCreateWebWindowRequest(birdTrackFarmVi: BirdTrackFarmVi) {
                    birdTrackFarmDataStore.birdTrackFarmViList.add(birdTrackFarmVi)
                    Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "WebView list size = ${birdTrackFarmDataStore.birdTrackFarmViList.size}")
                    Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "CreateWebWindowRequest")
                    birdTrackFarmDataStore.birdTrackFarmView = birdTrackFarmVi
                    birdTrackFarmVi.birdTrackFarmSetFileChooserHandler { callback ->
                        birdTrackFarmHandleFileChooser(callback)
                    }
                    birdTrackFarmAttachWebViewToContainer(birdTrackFarmVi)
                }

            }, birdTrackFarmWindow = requireActivity().window).apply {
                birdTrackFarmSetFileChooserHandler { callback ->
                    birdTrackFarmHandleFileChooser(callback)
                }
            }
            birdTrackFarmDataStore.birdTrackFarmView.birdTrackFarmFLoad(arguments?.getString(
                BirdTrackFarmLoadFragment.BIRD_TRACK_FARM_D) ?: "")
//            ejvview.fLoad("www.google.com")
            birdTrackFarmDataStore.birdTrackFarmViList.add(birdTrackFarmDataStore.birdTrackFarmView)
            birdTrackFarmAttachWebViewToContainer(birdTrackFarmDataStore.birdTrackFarmView)
        } else {
            birdTrackFarmDataStore.birdTrackFarmViList.forEach { webView ->
                webView.birdTrackFarmSetFileChooserHandler { callback ->
                    birdTrackFarmHandleFileChooser(callback)
                }
            }
            birdTrackFarmDataStore.birdTrackFarmView = birdTrackFarmDataStore.birdTrackFarmViList.last()

            birdTrackFarmAttachWebViewToContainer(birdTrackFarmDataStore.birdTrackFarmView)
        }
        Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "WebView list size = ${birdTrackFarmDataStore.birdTrackFarmViList.size}")
    }

    private fun birdTrackFarmHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        birdTrackFarmFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Launching file picker")
                    birdTrackFarmTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "Launching camera")
                    birdTrackFarmPhoto = birdTrackFarmViFun.birdTrackFarmSavePhoto()
                    birdTrackFarmTakePhoto.launch(birdTrackFarmPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(BirdTrackFarmApplication.BIRD_TRACK_FARM_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                birdTrackFarmFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun birdTrackFarmAttachWebViewToContainer(w: BirdTrackFarmVi) {
        birdTrackFarmDataStore.birdTrackFarmContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            birdTrackFarmDataStore.birdTrackFarmContainerView.removeAllViews()
            birdTrackFarmDataStore.birdTrackFarmContainerView.addView(w)
        }
    }


}