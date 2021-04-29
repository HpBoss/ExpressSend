package com.noah.express_send.bean

import android.graphics.Bitmap

class Msg(val content: String, val type: Int, val bitmap: Bitmap?) {
    companion object {
        const val TYPE_RECEIVED = 0
        const val TYPE_SENT = 1
    }
}