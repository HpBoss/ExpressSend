package com.noah.express_send.utils

import android.content.res.Resources

/**
 * @Auther: 何飘
 * @Date: 3/11/21 20:25
 * @Description:
 */
class SizeUtils {
    companion object{
        fun dp2px(dpValue: Float): Int {
            val scale: Float = Resources.getSystem().displayMetrics.density
            return (dpValue * scale + 0.5f).toInt()
        }
    }
}