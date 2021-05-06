package com.noah.internet.request

/**
 * @Auther: 何飘
 * @Date: 5/6/21 00:25
 * @Description:
 */
data class RequestCommentEntity(
    val phoneNum: String?,
    val oid: Int,
    val type: Int,
    val chipList: ArrayList<Int>,
    val content: String
)