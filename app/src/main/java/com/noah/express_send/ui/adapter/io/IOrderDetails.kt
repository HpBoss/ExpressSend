package com.noah.express_send.ui.adapter.io

import com.noah.internet.response.BestNewOrderEntity
import com.noah.internet.response.ResponseAddressBook

/**
 * @Auther: 何飘
 * @Date: 4/29/21 17:21
 * @Description:
 */
interface IOrderDetails {
    fun entryOrderDetails(bestNewOrderEntity: BestNewOrderEntity)
    fun startDeliverOrder(bestNewOrderEntity: BestNewOrderEntity)
    fun browseUserPageInfo(phoneNum: String?, nickname: String?)
    fun deleteOrder(oid: String?, position: Int)
}