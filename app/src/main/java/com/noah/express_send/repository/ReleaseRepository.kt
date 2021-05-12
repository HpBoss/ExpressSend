package com.noah.express_send.repository

import com.noah.internet.RetrofitClient
import com.noah.internet.request.RequestOrder

/**
 * @Auther: 何飘
 * @Date: 4/2/21 01:03
 * @Description:
 */
class ReleaseRepository {
    suspend fun releasePersonalOrder(requestOrder: RequestOrder) =
        RetrofitClient.instance!!.service.releasePersonalOrder(requestOrder)
}