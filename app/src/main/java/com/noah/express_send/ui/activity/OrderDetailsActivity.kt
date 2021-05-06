package com.noah.express_send.ui.activity

import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.express_send.R
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.viewModle.OrderDetailsViewModel
import com.noah.internet.response.BestNewOrderEntity
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_order_details.*
import kotlinx.android.synthetic.main.item_action_bar.*
import kotlinx.android.synthetic.main.item_order_info_message.*
import kotlinx.android.synthetic.main.item_order_info_message.tv_address
import kotlinx.android.synthetic.main.item_order_info_message.tv_express
import kotlinx.android.synthetic.main.item_order_info_message.tv_payIntegralNum
import kotlinx.android.synthetic.main.item_order_info_message.tv_typeName
import kotlinx.android.synthetic.main.item_single_comment_info.*
import kotlinx.android.synthetic.main.item_single_comment_info.chipGroup
import kotlinx.android.synthetic.main.item_status_bar.*
import kotlin.collections.HashSet

class OrderDetailsActivity : BaseActivity() {
    private val orderDetailsViewModel by lazy {
        ViewModelProvider(this).get(OrderDetailsViewModel::class.java)
    }

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

        orderDetailsViewModel.commentInfoWithChip.observe(this, {
            tv_nickname.text = it.nickname
            tv_commentContent.text = it.content
            tv_createTime.text = it.createTime
            if (it.type == 1) {
                symbol_high.visibility = View.VISIBLE
            }

            if (it.chipList.size > 0) {
                chipGroup.removeAllViews()
                for (chipEntity in it.chipList) {
                    val chip =
                        layoutInflater.inflate(R.layout.item_chip_small, chipGroup, false) as Chip
                    chip.text = chipEntity.chipName
                    chipGroup.addView(chip)
                }
                chipGroup.visibility = View.VISIBLE
            } else {
                chipGroup.visibility = View.GONE
            }

            Glide.with(this).load(it.avatarUrl)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .preload(40, 40)
            Glide.with(this)
                .load(it.avatarUrl).placeholder(R.drawable.ic_place_holder)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(iv_avatarComment)
        })
    }

    override fun initData() {
        val bestNewOrderEntity = intent.getParcelableExtra<BestNewOrderEntity>("bestNewOrderEntity")
        initPointProgressView(bestNewOrderEntity?.stateName, bestNewOrderEntity?.oid)

        tv_express.text = bestNewOrderEntity?.express
        tv_weights.text = bestNewOrderEntity?.weight
        tv_nickNameRight.text = bestNewOrderEntity?.nickName
        tv_payIntegralNum.text = bestNewOrderEntity?.payIntegralNum.toString()
        dormitory.text = bestNewOrderEntity?.dormitory
        tv_address.text = bestNewOrderEntity?.addressName
        tv_operateTime.text = bestNewOrderEntity?.operateTime
        tv_typeName.text = bestNewOrderEntity?.typeName
        tv_orderState.visibility = View.INVISIBLE

        Glide.with(this).load(bestNewOrderEntity?.avatarUrl)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .preload()
        Glide.with(this).load(bestNewOrderEntity?.avatarUrl)
            .placeholder(R.drawable.ic_place_holder)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .into(avatar)
    }

    private fun initPointProgressView(stateName: String?, oid: Long?) {
        val list = mutableListOf("已接单", "已派单", "已收货", "已完成")
        var set: HashSet<Int>? = null
        when (stateName) {
            "待派送" -> { set = hashSetOf(0) }
            "待收货" -> { set = hashSetOf(0, 1) }
            "已完成" -> { set = hashSetOf(0, 1, 2, 3) }
            "待评价" -> {
                set = hashSetOf(0, 1, 2)
                commentPlaceHolderHint.visibility = View.GONE
                itemSingleComment.visibility = View.VISIBLE
                orderDetailsViewModel.getCommentByOid(oid.toString())
            }
        }
        pointProgressView.show(list, set)
    }


}