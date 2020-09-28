package com.riis.autelevoiidemo

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.autel.common.CallbackWithNoParam
import com.autel.common.error.AutelError
import com.autel.sdk.Autel
import com.autel.sdk.AutelSdkConfig
import com.autel.sdk.ProductConnectListener
import com.autel.sdk.product.BaseProduct

class MainActivity : AppCompatActivity() {
    private lateinit var currentProduct: BaseProduct

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}