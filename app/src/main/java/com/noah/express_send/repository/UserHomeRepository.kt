package com.noah.express_send.repository

import com.noah.internet.RetrofitClient

/**
 * @Auther: 何飘
 * @Date: 5/6/21 14:42
 * @Description:
 */
class UserHomeRepository {
    suspend fun getUserPageInfo(phoneNum: String?) =
        RetrofitClient.instance!!.service.getUserPageInfo(phoneNum)
}