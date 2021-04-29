package com.noah.express_send.ui

import android.app.Application
import android.util.Log
import cn.jiguang.verifysdk.api.JVerificationInterface
import cn.jpush.im.android.api.JMessageClient
import com.jhworks.library.ImageSelector.setImageEngine
import com.noah.express_send.utils.GlideEngine

/**
 * @Auther: 何飘
 * @Date: 3/28/21 16:34
 * @Description:
 */
class MyApplication: Application() {
    companion object {
        private const val TAG = "JIGUANG-Example"
    }
    override fun onCreate() {
        super.onCreate()
        JMessageClient.setDebugMode(true)
        JMessageClient.init(this)
        JVerificationInterface.setDebugMode(true)
        val start = System.currentTimeMillis()
        JVerificationInterface.init(
            this
        ) { code: Int, result: String ->
            Log.d(
                TAG, "[init] code = " + code + " result = " + result +
                        " consists = " + (System.currentTimeMillis() - start)
            )
        }
        setImageEngine(GlideEngine())
    }
}