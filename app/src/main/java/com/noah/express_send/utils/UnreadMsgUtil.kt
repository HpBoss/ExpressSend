package com.noah.express_send.utils

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView


/**
 * @Auther: 何飘
 * @Date: 3/11/21 20:27
 * @Description:
 */
class UnreadMsgUtil {
    companion object{
        fun show(numView: TextView?, num: Int) {
            if (numView == null) {
                return
            }
            val lp = numView.layoutParams
            val dm = numView.resources.displayMetrics
            numView.visibility = View.VISIBLE
            if (num in 1..9) { //圆
                lp.width = (15 * dm.density).toInt()
                numView.text = num.toString()
            } else if (num in 10..99) { //圆角矩形,圆角是高度的一半,设置默认padding
                lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT
                numView.setPadding((6 * dm.density).toInt(), 0, (6 * dm.density).toInt(), 0)
                numView.text = num.toString()
            } else { //数字超过两位,显示99+
                lp.width = RelativeLayout.LayoutParams.WRAP_CONTENT
                numView.setPadding((6 * dm.density).toInt(), 0, (6 * dm.density).toInt(), 0)
                numView.text = "99+"
            }
            numView.layoutParams = lp
        }
    }
}