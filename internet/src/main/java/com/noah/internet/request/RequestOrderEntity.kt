package com.noah.internet.request

/**
 * @Auther: 何飘
 * @Date: 4/2/21 00:51
 * @Description:
 */
data class RequestOrderEntity(
    val phoneNum: String? = null, val express: String,
    val typeName: String, val weight: String, val payIntegralNum: Int, val detailAddress: String
)