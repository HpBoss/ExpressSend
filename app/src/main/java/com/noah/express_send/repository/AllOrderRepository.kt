package com.noah.express_send.repository

import com.noah.internet.RetrofitClient

/**
 * @Auther: 何飘
 * @Date: 4/4/21 01:06
 * @Description:
 */
class AllOrderRepository {
    suspend fun getAllOrderOfUserInfo(phoneNum: String?) =
        RetrofitClient.instance!!.service.getAllOrderOfUserInfo(phoneNum)

    suspend fun queryToBeReceive(phoneNum: String?) =
        RetrofitClient.instance!!.service.queryToBeReceive(phoneNum)

    suspend fun queryCompleted(phoneNum: String?) =
        RetrofitClient.instance!!.service.queryCompleted(phoneNum)

    suspend fun queryReceived(phoneNum: String?) =
        RetrofitClient.instance!!.service.queryReceived(phoneNum)

    suspend fun queryToBeFetch(phoneNum: String?) =
        RetrofitClient.instance!!.service.queryToBeFetch(phoneNum)

    suspend fun queryToBeSend(phoneNum: String?) =
        RetrofitClient.instance!!.service.queryToBeSend(phoneNum)

    suspend fun queryToBeComment(phoneNum: String?) =
        RetrofitClient.instance!!.service.queryToBeComment(phoneNum)
}