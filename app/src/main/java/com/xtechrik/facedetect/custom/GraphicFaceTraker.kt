package com.xtechrik.facedetect.custom

import android.util.Log
import com.google.android.gms.vision.Detector.Detections
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face

class GraphicFaceTraker(private val mOverlay: GraphicOverlay) : Tracker<Face>() {

    private var mFaceGraphic: FaceGraphic = FaceGraphic(mOverlay)

    override fun onNewItem(i: Int, face: Face?) {
        Log.d("TAGs", "onNewItem")
        mFaceGraphic.setId(i)
    }

    override fun onUpdate(detections: Detections<Face?>, face: Face?) {
        Log.d("TAGs", "onUpdate")
        /// FIXME: Call api
        mOverlay.add(mFaceGraphic)
        mFaceGraphic.updateFace(face!!)
    }

    override fun onMissing(detections: Detections<Face?>) {
        Log.d("TAGs", "onMissing")
        mOverlay.remove(mFaceGraphic)
    }

    override fun onDone() {
        Log.d("TAGs", "onDone")
        mOverlay.remove(mFaceGraphic)
    }

}