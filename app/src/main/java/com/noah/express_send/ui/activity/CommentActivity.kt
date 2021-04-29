package com.noah.express_send.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.express_send.R
import com.noah.express_send.ui.base.BaseActivity
import com.noah.internet.response.BestNewOrderEntity

class CommentActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_comment
    }

    override fun isSetSoftInputMode() = true

    override fun initView() {
        immersionBar {
            statusBarColor(R.color.blue_364)
            fitsSystemWindows(true)
        }
        val orderInfo = intent.getParcelableExtra<BestNewOrderEntity>("orderInfo")
        
    }

    override fun initData() {

    }

}