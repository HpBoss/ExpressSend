package com.noah.express_send.repository

import com.noah.internet.RetrofitClient

/**
 * @Auther: 何飘
 * @Date: 5/5/21 01:25
 * @Description:
 */
class OrderDetailsRepository {
    suspend fun getCommentByOid(oid: String?) =
        RetrofitClient.instance!!.service.getCommentByOid(oid)
}