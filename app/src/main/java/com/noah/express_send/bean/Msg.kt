package com.noah.express_send.bean

import android.graphics.Bitmap
import cn.jpush.im.android.api.model.Message

class Msg(val message: Message, val type: Int, val bitmap: Bitmap?) {
    companion object {
        const val TEXT_RECEIVED = 0
        const val TEXT_SENT = 1
        const val IMG_RECEIVED = 2
        const val IMG_SENT = 3
        const val ORDER_REQUEST_SENT = 4
        const val ORDER_REQUEST_RECEIVED = 5
        const val OTHER = 6
    }
}