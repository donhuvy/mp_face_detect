package com.xtechrik.facedetect

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.face.Face
import com.google.android.gms.vision.face.FaceDetector
import com.tbruyelle.rxpermissions2.RxPermissions
import com.xtechrik.facedetect.custom.CameraSourcePreview
import com.xtechrik.facedetect.custom.GraphicFaceTrackerFactory
import com.xtechrik.facedetect.custom.GraphicOverlay
import com.xtechrik.facedetect.databinding.ActivityMainBinding
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var mCameraSource: CameraSource? = null
    private lateinit var mPreview: CameraSourcePreview
    private lateinit var mGraphicOverlay: GraphicOverlay

    private val RC_HANDLE_GMS = 9001
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        mPreview = binding.cameraSourcePreview
        mGraphicOverlay = binding.graphicOverlay
        checkGrandPermissions()
    }

    @SuppressLint("CheckResult")
    private fun checkGrandPermissions() {
        RxPermissions(this)
            .request(Manifest.permission.CAMERA)
            .subscribe { granted: Boolean ->
                if (granted) { // Always true pre-M
                    // I can control the camera now
                    createCameraSource()
                } else {
                    // Oups permission denied
                }
            }
    }

    private fun createCameraSource() {
        val context = applicationContext
        val detector = FaceDetector.Builder(context)
            .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
            .build()
        detector.setProcessor(MultiProcessor.Builder<Face>(GraphicFaceTrackerFactory(mGraphicOverlay)).build())
        if (!detector.isOperational) {
            Log.w("TAGs", "Face detector dependencis are not yet available")
        }
        mCameraSource = CameraSource.Builder(context, detector)
            //.setRequestedPreviewSize(640, 480)
            .setFacing(CameraSource.CAMERA_FACING_BACK)
            .setRequestedFps(30.0f)
            .build()
    }

    override fun onResume() {
        super.onResume()
        startCameraSource()
    }

    override fun onStop() {
        mPreview.stop()
        super.onStop()
    }

    override fun onDestroy() {
        if (mCameraSource != null) {
            mCameraSource!!.release()
        }
        super.onDestroy()
    }

    private fun startCameraSource() {
        val code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(applicationContext)
        if (code != ConnectionResult.SUCCESS) {
            val dlg = GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS)
            dlg.show()
        }
        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource)
            } catch (e: IOException) {
                Log.e("TAGs", "Unable to start camera source", e)
                mCameraSource!!.release()
                mCameraSource = null
            }
        }
    }
}