package com.noah.express_send.ui.activity

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.express_send.R
import com.noah.express_send.ui.base.BaseActivity
import com.noah.internet.response.BestNewOrderEntity
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.item_action_bar.*
import kotlinx.android.synthetic.main.item_order_info_message.*
import kotlinx.android.synthetic.main.item_status_bar.*
import java.util.*
import kotlin.collections.HashSet

class OrderDetailsActivity : BaseActivity() {
    override fun getLayoutId() = R.layout.activity_order_details

    override fun isSetSoftInputMode() = true

    override fun initView() {
        immersionBar {
            statusBarView(status_bar_view)
            statusBarDarkFont(false)
        }
        actionBar_title.text = getString(R.string.order_details)
        back.setOnClickListener { onBackPressed() }
        btn_funcOperate.visibility = View.INVISIBLE
        btn_changeOrderState.visibility = View.INVISIBLE
    }

    override fun initData() {
        val bestNewOrderEntity = intent.getParcelableExtra<BestNewOrderEntity>("bestNewOrderEntity")
        initPointProgressView(bestNewOrderEntity?.stateName)

        tv_express.text = bestNewOrderEntity?.express
        tv_weights.text = bestNewOrderEntity?.weight
        tv_nickNameRight.text = bestNewOrderEntity?.nickName
        tv_payIntegralNum.text = bestNewOrderEntity?.payIntegralNum.toString()
        dormitory.text = bestNewOrderEntity?.dormitory
        tv_address.text = bestNewOrderEntity?.addressName
        tv_operateTime.text = bestNewOrderEntity?.operateTime
        tv_orderState.visibility = View.INVISIBLE

        Glide.with(this).load(bestNewOrderEntity?.avatarUrl)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .preload()
        Glide.with(this).load(bestNewOrderEntity?.avatarUrl)
            .placeholder(R.drawable.ic_place_holder)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .into(avatar)
    }

    private fun initPointProgressView(stateName: String?) {
        val list = mutableListOf("已接单", "已派单", "已收货", "已完成")
        var set: HashSet<Int>? = null
        when(stateName) {
            "待派送" ->{set = hashSetOf(0)}
            "待收货" ->{set = hashSetOf(0, 1)}
            "待评价" ->{set = hashSetOf(0, 1, 2)}
            "已完成" ->{set = hashSetOf(0, 1, 2, 3)}
        }
        pointProgressView.show(list, set)
    }


}