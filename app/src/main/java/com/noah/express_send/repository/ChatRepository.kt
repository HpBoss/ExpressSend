package com.noah.express_send.repository

import com.noah.internet.RetrofitClient

/**
 * @Auther: 何飘
 * @Date: 4/6/21 18:08
 * @Description:
 */
class ChatRepository {
    suspend fun receiveOrder(id: String?, phoneNum: String?) =
        RetrofitClient.instance!!.service.receiveOrder(id, phoneNum)

    suspend fun refuseOrder(id: String?) =
        RetrofitClient.instance!!.service.refuseOrder((id))
}