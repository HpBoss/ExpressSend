package com.noah.express_send.repository

import com.noah.internet.RetrofitClient
import com.noah.internet.request.RequestUserEntity
/**
 * @Auther: 何飘
 * @Date: 3/31/21 11:05
 * @Description:
 */
class ModifyProfileRepository {
    suspend fun updateUserProfile(phoneNum: String?, requestUserEntity: RequestUserEntity) =
        RetrofitClient.instance!!.service.updateUserProfile(phoneNum, requestUserEntity)
}

