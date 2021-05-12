package com.noah.internet.response

/**
 * @Auther: 何飘
 * @Date: 5/6/21 16:11
 * @Description:
 */
data class ResponseUserPage(
    val avatarUrl: String,
    val nickname: String,
    val dormitory: String,
    val roomNumber: String,
    val phoneNum: String,
    val receiveNum: Int,
    val schoolName: String,
    val commentList: ArrayList<CommentAllInfo>
)