package com.noah.express_send.repository

import com.noah.internet.RetrofitClient

/**
 * @Auther: 何飘
 * @Date: 4/3/21 16:31
 * @Description:
 */
class OrderRepository {
    suspend fun getOrderOfUserInfo(phoneNum: String?) =
        RetrofitClient.instance!!.service.getOrderOfUserInfo(phoneNum)
}