package com.noah.internet.request

/**
 * @Auther: 何飘
 * @Date: 4/2/21 00:51
 * @Description:
 */
data class RequestOrder(
    val id: Long? = null, val phoneNum: String? = null, val expressName: String, val schoolName: String?,
    val typeName: String, val weight: String, val payIntegralNum: Int, val addressName: String
)