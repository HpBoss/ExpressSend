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
    fun cancelOrder(oid: String?, position: Int, isCancelOrder: Boolean)
    fun confirmOrder(oid: String?, position: Int)
    fun commentOrder(orderInfo: BestNewOrderEntity)
    fun modifyOrder(oid: String?, phoneNum: String?, orderInfo: BestNewOrderEntity)
    fun browseUserPageInfo(phoneNum: String?, nickname: String?)
}