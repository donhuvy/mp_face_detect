package com.xtechrik.facedetect.custom

import com.google.android.gms.vision.MultiProcessor
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face

class GraphicFaceTrackerFactory(var mGraphicOverlay: GraphicOverlay) : MultiProcessor.Factory<Face> {

    override fun create(face: Face): Tracker<Face> {
        return GraphicFaceTraker(mGraphicOverlay)
    }
}