package com.noah.express_send.ui.base

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatActivity
import com.noah.express_send.R

abstract class BaseActivity : AppCompatActivity() {
    abstract fun getLayoutId(): Int
    abstract fun isSetSoftInputMode(): Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutId())
        // 防止底部弹出键盘，"顶起"某些底部view
        if (isSetSoftInputMode()) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        }
        initView()
        initData()
    }

    protected abstract fun initView()
    protected abstract fun initData()

    /**
     * 设置沉浸式界面
     * 根据Android版本的不同，作出相应设置
     */
    fun setImmersive() {
        if (Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false) // 相当于设置FULLSCREEN
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else if (Build.VERSION.SDK_INT >= 21) {
            val decorView = window.decorView
            val option = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            decorView.systemUiVisibility = option
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity 活动
     * @param color    颜色值
     */
    protected fun setStatusBar(activity: Activity, color: Int) {
        val window = activity.window
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //6.0 以上直接设置状态栏颜色
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏颜色
            window.statusBarColor = color
            // 去掉系统状态栏下的windowContentOverlay
            val v = window.findViewById<View>(android.R.id.content)
            if (v != null) {
                v.foreground = null
            }
            if (toGrey(color) > 225) { //判定该状态栏颜色条件下，是否要将状态栏图标切换成灰色
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //5.0 以上直接设置状态栏颜色
            //取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            //需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            //设置状态栏颜色
            window.statusBarColor = color
        }
    }

    companion object {
        /**
         * 把颜色转换成灰度值。
         * 代码来自 Flyme 示例代码
         */
        fun toGrey(@ColorInt color: Int): Int {
            val blue = Color.blue(color)
            val green = Color.green(color)
            val red = Color.red(color)
            return red * 38 + green * 75 + blue * 15 shr 7
        }
    }

    /**
     * @param start 起始活动
     * @param end   结束活动类
     */
    fun activityJump(start: Activity?, end: Class<*>?) {
        val intent = Intent(start, end)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.translate_left_in, R.anim.translate_right_out)
        finish()
    }

    /**
     * 键盘焦点监听，焦点消失键盘收起
     */
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus //这时能获取到焦点的View就是EditText
            if (isShouldHideKeyboard(v, ev)) {
                val imm = (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                imm.hideSoftInputFromWindow(v!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                v.clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘
    private fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v is EditText) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            //只要点击了EditText周围的空白处，就返回true
            //return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
            return !(event.y > top && event.y < bottom)
        }
        return false
    }

    /**
     * 利用反射获取状态栏高度
     * @return statusBar的height
     */
    fun getStatusBarHeight(activity: Activity): Int {
        val resources = activity.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return resources.getDimensionPixelSize(resourceId)
    }

    /*@Subscribe
    open fun onEventMainThread(event: LoginStateChangeEvent) {
        val reason = event.reason
        if (reason == LoginStateChangeEvent.Reason.user_logout) {
            Toast.makeText(this, "登录失效，重新登录", Toast.LENGTH_SHORT).show()
        } else if (reason == LoginStateChangeEvent.Reason.user_password_change) {
            Toast.makeText(this, "修改密码，重新登录", Toast.LENGTH_SHORT).show()
        }
        if (!isFinishing) {
            JMessageClient.logout()
        }
    }*/
}