package com.noah.express_send.repository

import com.noah.internet.RetrofitClient
import com.noah.internet.request.RequestLoginUser

/**
 * @Auther: 何飘
 * @Date: 4/1/21 16:53
 * @Description:
 */
class SplashRepository {

    suspend fun getPhoneNum(loginToken: String) =
        RetrofitClient.instance!!.service.phoneNumLogin(RequestLoginUser(loginToken))

}