package com.noah.express_send.repository

import com.noah.internet.RetrofitClient
import okhttp3.MultipartBody

/**
 * @Auther: 何飘
 * @Date: 3/31/21 13:35
 * @Description:
 */
class EditProfileRepository {
    suspend fun updateAvatarUrl(phoneNum: String?, part: MultipartBody.Part) =
        RetrofitClient.instance!!.service.updateAvatarUrl(phoneNum, part)
}