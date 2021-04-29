package com.noah.express_send.ui.activity

import android.Manifest
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import com.noah.database.User
import com.noah.express_send.R
import com.noah.express_send.ui.adapter.io.IReturnLoginToken
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.ui.view.AutoLoginView
import com.noah.express_send.utils.MD5Utils
import com.noah.express_send.viewModle.SplashViewModel
import me.leefeng.promptlibrary.PromptDialog


class SplashActivity : BaseActivity(), IReturnLoginToken {
    // 设置启动页到SplashActivity（APP启动最先启动的活动）中，但是观察发现，
    // 点击APP之后，会先出现一个白屏，然后才是启动页，这样的效果肯定是不太好的。
    private lateinit var autoLoginView: AutoLoginView
    private val splashViewModel by lazy {
        ViewModelProvider(this).get(SplashViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_splash
    }

    override fun isSetSoftInputMode() = true

    override fun initView() {
        setImmersive()
        autoLoginView = AutoLoginView(this)
        autoLoginView.preLogin()
    }

    override fun initData() {
        requestPermissions()
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_PRECISE_PHONE_STATE,
                ), 100
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            try {
                Handler(Looper.getMainLooper()).postDelayed({
                    if (splashViewModel.queryIsLoginUser() == null) { // 如果已经登录就没必要再初始化AutoLoginView
                        autoLoginView.loginAuth(false, this)
                    } else {
                        activityJump(this, MainActivity::class.java)
                    }
                }, 2000)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun setLoginToken(loginToken: String) {
        splashViewModel.getPhoneNum(loginToken, this)
        val promptDialog = PromptDialog(this)
        promptDialog.showLoading("")
        splashViewModel.phoneNum.observe(this, Observer {
            // 登录成功，添加一个user到Room数据库中，并这个user的isLogin为true
            val user = splashViewModel.queryUser(it)
            if (user == null) {
                splashViewModel.insertUser(User(0, it, 1))
            } else {
                user.isLogin = 1
                splashViewModel.updateUser(user)
            }
            autoLoginView.finishLoginLoad()
            JMessageClient.login(it, MD5Utils.string2MD5(it), object :
                BasicCallback() {
                override fun gotResult(p0: Int, p1: String?) {
                    if (p0 == 0) {
                        promptDialog.dismiss()
                        activityJump(this@SplashActivity, MainActivity::class.java)
                    } else {
                        promptDialog.showError("登录失败！")
                        finish()
                    }
                }
            })
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        autoLoginView.delPreLoginCache()
    }
}