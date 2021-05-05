package com.noah.internet.response

/**
 * @Auther: 何飘
 * @Date: 4/2/21 01:57
 * @Description:
 */
data class ResponseOrderEntity(
    val id: Long, val avatarUrl: String, val nickName: String, val dormitory: String, val phoneNum: String,
    val express: String, val typeName: String, val weight: String, val addressName: String, val payIntegralNum: Int
)