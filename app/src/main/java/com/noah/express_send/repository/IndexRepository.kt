package com.noah.express_send.repository

import com.noah.internet.RetrofitClient
import com.noah.internet.request.RequestLoginUser

/**
 * @Auther: 何飘
 * @Date: 3/29/21 18:17
 * @Description:
 */
class IndexRepository {

    suspend fun getPageOrderNoCur(page: String?, phoneNum: String?) =
        RetrofitClient.instance!!.service.getPageOrderNoCur(page, phoneNum)

    suspend fun receiveOrder(id: String?, phoneNum: String?) =
        RetrofitClient.instance!!.service.receiveOrder(id, phoneNum)
}