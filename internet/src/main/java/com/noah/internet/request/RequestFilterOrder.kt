package com.noah.internet.request

import com.noah.internet.response.ResponseExpress

/**
 * @Auther: 何飘
 * @Date: 5/12/21 16:17
 * @Description:
 */
data class RequestFilterOrder(
    var page: Int,
    val phoneNum: String?,
    val expressEntities: ArrayList<ResponseExpress>
)