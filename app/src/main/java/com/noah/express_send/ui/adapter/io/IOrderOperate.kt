package com.noah.express_send.ui.adapter.io

import com.noah.internet.response.BestNewOrder

/**
 * @Auther: 何飘
 * @Date: 4/4/21 03:11
 * @Description:
 */
interface IOrderOperate {
    fun cancelOrder(oid: String?, position: Int)
    fun deleteOrder(oid: String?, position: Int)
    fun confirmOrder(oid: String?, position: Int)
    fun commentOrder(orderInfo: BestNewOrder)
    fun modifyOrder(oid: String?, phoneNum: String?, orderInfo: BestNewOrder)
    fun browseUserPageInfo(phoneNum: String?, nickname: String?)
}