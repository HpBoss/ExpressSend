package com.noah.express_send.ui.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.widget.RelativeLayout
import com.noah.express_send.R
import kotlinx.android.synthetic.main.activity_main.*


/**
 * @Auther: 何飘
 * @Date: 2/24/21 20:32
 * @Description:
 */
class RoundRelativeLayout : RelativeLayout {
    constructor(mContext: Context) : this(mContext, null)
    constructor(mContext: Context, attrs: AttributeSet?) : this(mContext, attrs, 0)
    constructor(mContext: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(mContext, attrs, defStyleAttr)
    val path = Path()
    private val rectF = RectF()
    /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/
    private val rids = floatArrayOf(20.0f, 20.0f, 20.0f, 20.0f, 0.0f, 0.0f, 0.0f, 0.0f)

    override fun onDraw(canvas: Canvas) {
        val w = width
        val h = height
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
        rectF.set(0F, 0F, w.toFloat(), h.toFloat())
        path.addRoundRect(rectF, rids, Path.Direction.CW)
        canvas.clipPath(path)
        super.onDraw(canvas)
    }

}