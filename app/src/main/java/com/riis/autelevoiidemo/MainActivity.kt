package com.riis.autelevoiidemo

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import com.autel.common.CallbackWithNoParam
import com.autel.common.error.AutelError
import com.autel.sdk.Autel
import com.autel.sdk.AutelSdkConfig
import com.autel.sdk.ProductConnectListener
import com.autel.sdk.product.BaseProduct
import com.autel.sdk.widget.AutelCodecView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.riis.autelevoiidemo.model.BoundingBox
import com.riis.autelevoiidemo.model.ImageClassifier
import com.riis.autelevoiidemo.view.OverlayView
import org.jetbrains.anko.doAsync


class MainActivity : AppCompatActivity() {
    private lateinit var currentProduct: BaseProduct
    private lateinit var classifier: ImageClassifier
    private lateinit var stream_view_overlay: OverlayView
    private lateinit var codec_view: AutelCodecView
    lateinit var mainHandler: Handler

    private val updateCaptureLoop = object: Runnable {
        override fun run() {
            captureBitmapLoop()
            mainHandler.postDelayed(this, 200)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainHandler = Handler(Looper.getMainLooper())
        classifier = ImageClassifier(application.assets)
        stream_view_overlay = findViewById(R.id.stream_view_overlay)
        codec_view = findViewById(R.id.codec_view)


        initSDK()
        initProduct()

    }

    private fun initSDK(){
        val config = AutelSdkConfig.AutelSdkConfigBuilder()
            .setAppKey("<SDK license should be input>")
            .setPostOnUi(true)
            .create()


        Autel.init(applicationContext, config, object: CallbackWithNoParam {
            override fun onSuccess() {
                Log.i("autelInit", "connected")
            }

            override fun onFailure(p0: AutelError?) {
                Log.i("autelInit", "${p0?.description}")
            }

        })
    }

    private fun initProduct() {
        Autel.setProductConnectListener(object: ProductConnectListener {
            override fun productConnected(p0: BaseProduct?) {
                Log.i("productListener", "connected")
                if (p0 != null) {
                    Toast.makeText(applicationContext, p0.type.toString(), Toast.LENGTH_LONG).show()
                    currentProduct = p0


                }
            }

            override fun productDisconnected() {
                Log.i("productListener", "disconnected")
            }

        })
    }

    private fun captureBitmapLoop(){
        if(codec_view.isAvailable){
            codec_view.bitmap?.let {
                processBitmap(it)
            }

        }

    }

    private fun processBitmap(bitmap: Bitmap) {
        doAsync {
            if(!classifier.processing){
                val results = classifier.classifyImage(bitmap)
                //bitmap.recycle()
                runOnUiThread {
                    Log.i("appInfo", results.title)
                    drawBoxes(results.boxes, results.title)
                }

            }
        }

    }

    private fun drawBoxes(boxes: List<BoundingBox>, title: String) {
        stream_view_overlay.boxes = boxes
        stream_view_overlay.title = title
        stream_view_overlay.invalidate()

    }

    override fun onResume() {
        super.onResume()
        mainHandler.post(updateCaptureLoop)
    }

    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateCaptureLoop)
    }
}