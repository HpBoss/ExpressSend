package com.noah.express_send.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.noah.express_send.R
import com.noah.internet.response.BestNewOrderEntity

/**
 * @Auther: 何飘
 * @Date: 5/4/21 20:53
 * @Description:
 */
class OrderInfoView(
    private val bestNewOrderEntity: BestNewOrderEntity,
    private val mContext: Context
) {
    private lateinit var avatar: ImageView
    private lateinit var username: TextView
    private lateinit var orderState: TextView
    private lateinit var express: TextView
    private lateinit var weight: TextView
    private lateinit var classify: TextView
    private lateinit var integralNum: TextView
    private lateinit var dormitory: TextView
    private lateinit var location: TextView

    private lateinit var btnChangeOrderState: TextView
    private lateinit var btnFuncOperate: TextView
    private lateinit var horizontalDividing: View
    private lateinit var hintPlaceHolder: TextView
    private lateinit var view: View

    fun initView() {
        view = LayoutInflater.from(mContext).inflate(R.layout.item_order_info_message, null)
        avatar = view.findViewById(R.id.avatar)
        username = view.findViewById(R.id.tv_nickNameRight)
        orderState = view.findViewById(R.id.tv_orderState)
        express = view.findViewById(R.id.tv_express)
        weight = view.findViewById(R.id.tv_weights)
        classify = view.findViewById(R.id.tv_typeName)
        integralNum = view.findViewById(R.id.tv_payIntegralNum)
        dormitory = view.findViewById(R.id.dormitory)
        location = view.findViewById(R.id.tv_address)

        btnChangeOrderState = view.findViewById(R.id.btn_changeOrderState)
        btnFuncOperate = view.findViewById(R.id.btn_funcOperate)
        horizontalDividing = view.findViewById(R.id.horizontalDividing)
    }

    fun loadData() {
        btnChangeOrderState.visibility = View.GONE
        btnFuncOperate.visibility = View.GONE
        horizontalDividing.visibility = View.VISIBLE

        orderState.text = bestNewOrderEntity.stateName
        express.text = bestNewOrderEntity.express
        location.text = bestNewOrderEntity.addressName
        username.text = bestNewOrderEntity.nickName
        weight.text = bestNewOrderEntity.weight
        classify.text = bestNewOrderEntity.typeName
        integralNum.text =
            mContext.getString(R.string.payIntegralNum, bestNewOrderEntity.payIntegralNum)
        dormitory.text = bestNewOrderEntity.dormitory

        Glide.with(mContext).load(bestNewOrderEntity.avatarUrl)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .preload()
        Glide.with(mContext).load(bestNewOrderEntity.avatarUrl)
            .placeholder(R.drawable.ic_place_holder)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
            .into(avatar)
        when (bestNewOrderEntity.stateName) {
            "待派送" -> {
                orderState.text = "已接单"
                btnChangeOrderState.text = "开始派送"
                btnChangeOrderState.visibility = View.VISIBLE
                horizontalDividing.visibility = View.GONE
            }
            "待收货" -> {
                orderState.text = "已派送"
            }
            "待评价" -> {
                orderState.text = "已收货"
            }
            "已完成" -> {
                orderState.text = "已完成"
            }
        }
    }

    fun getView() = view
}