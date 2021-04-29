package com.noah.express_send.repository

import com.noah.internet.RetrofitClient
import com.noah.internet.request.RequestLoginUser

/**
 * @Auther: 何飘
 * @Date: 3/31/21 14:49
 * @Description:
 */
class MainRepository {
    suspend fun signOut(phoneNum: String?) =
        RetrofitClient.instance!!.service.signOut(phoneNum)

    suspend fun requestAvatarUrl(phoneNum: String?) =
        RetrofitClient.instance!!.service.requestAvatarUrl(phoneNum)

    suspend fun requestUserProfile(phoneNum: String?) =
        RetrofitClient.instance!!.service.requestUserProfile(phoneNum)

    suspend fun getPhoneNum(loginToken: String) =
        RetrofitClient.instance!!.service.phoneNumLogin(RequestLoginUser(loginToken))
}