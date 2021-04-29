package com.noah.express_send.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import com.scwang.smart.drawable.ProgressDrawable
import com.scwang.smart.refresh.classics.ArrowDrawable
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import com.scwang.smart.refresh.layout.util.SmartUtil

/**
 * @Auther: 何飘
 * @Date: 2/28/21 17:21
 * @Description:
 */
class CustomHeader: LinearLayout, RefreshHeader {
    private var mHeaderText: TextView
    private var mArrowView: ImageView
    private var mProgressView: ImageView
    private var mProgressDrawable: ProgressDrawable

    constructor(context: Context): this(context, null)
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs, 0) {
        gravity = Gravity.CENTER
        mHeaderText = TextView(context)
        mProgressDrawable = ProgressDrawable()
        mArrowView = ImageView(context)
        mProgressView = ImageView(context)
        mProgressView.setImageDrawable(mProgressDrawable)
        mArrowView.setImageDrawable(ArrowDrawable())
        addView(mProgressView, SmartUtil.dp2px(20F), SmartUtil.dp2px(20F))
        addView(mArrowView, SmartUtil.dp2px(20F), SmartUtil.dp2px(20F))
        addView(Space(context), SmartUtil.dp2px(20F), SmartUtil.dp2px(20F))
        addView(mHeaderText, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        minimumHeight = SmartUtil.dp2px(60F)
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState
    ) {
        when (newState) {
            RefreshState.None, RefreshState.PullDownToRefresh -> {
                mHeaderText.text = "下拉开始刷新"
                mArrowView.visibility = VISIBLE //显示下拉箭头
                mProgressView.visibility = GONE //隐藏动画
                mArrowView.animate().rotation(0f) //还原箭头方向
            }
            RefreshState.Refreshing -> {
                mHeaderText.text = "正在刷新"
                mProgressView.visibility = VISIBLE //显示加载动画
                mArrowView.visibility = GONE //隐藏箭头
            }
            RefreshState.ReleaseToRefresh -> {
                mHeaderText.text = "释放立即刷新"
                mArrowView.animate().rotation(180f) //显示箭头改为朝上
            }
        }
    }

    override fun getView(): View {
        return this
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        mProgressDrawable.start()
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        mProgressDrawable.stop()
        mProgressView.visibility = GONE
        if (success) {
            mHeaderText.text = "刷新完成"
        } else {
            mHeaderText.text = "刷新失败"
        }
        return 500
    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int
    ) {

    }

    override fun setPrimaryColors(vararg colors: Int) {

    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {

    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {

    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {

    }

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }
}