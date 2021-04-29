package com.noah.express_send.ui.adapter.io

import android.view.View
import android.widget.TextView
import com.noah.internet.response.BestNewOrderEntity

/**
 * @Auther: 何飘
 * @Date: 4/4/21 03:11
 * @Description:
 */
interface IOrderOperate {
    fun cancelOrder(oid: String?, position: Int)
    fun confirmOrder(oid: String?, position: Int)
    fun deliveryOrder(changeOrderState: TextView, disOrderState: TextView, oid: String?)
    fun commentOrder(orderInfo: BestNewOrderEntity)
    fun contactUser(oid: String?, phoneNum: String?, nickname: String?)
    fun modifyOrder(oid: String?, phoneNum: String?, orderInfo: BestNewOrderEntity)
}