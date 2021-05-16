package com.noah.express_send.repository

import com.noah.internet.RetrofitClient
import com.noah.internet.request.RequestFilterOrder

/**
 * @Auther: 何飘
 * @Date: 3/29/21 18:17
 * @Description:
 */
class IndexRepository {

    suspend fun getPageOrderNoCur(page: String?, phoneNum: String?) =
        RetrofitClient.instance!!.service.getPageOrderNoCur(page, phoneNum)

    suspend fun getAllExpressName() =
        RetrofitClient.instance!!.service.getAllExpressName()

    suspend fun getAllFilterOrder(requestFilterOrder: RequestFilterOrder) =
        RetrofitClient.instance!!.service.getALlFilterOrder(requestFilterOrder)

    suspend fun applyOrder(oid: String?) =
        RetrofitClient.instance!!.service.applyOrder(oid)
}