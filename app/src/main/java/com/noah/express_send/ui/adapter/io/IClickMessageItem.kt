package com.noah.express_send.ui.adapter.io

import android.graphics.Bitmap

/**
 * @Auther: 何飘
 * @Date: 4/5/21 20:37
 * @Description:
 */
interface IClickMessageItem {
    fun setOnClickMessageItem(position: Int, bitmap: Bitmap?, targetId: String?, nickname: String?)
}