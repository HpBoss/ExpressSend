package com.noah.internet.response

/**
 * @Auther: 何飘
 * @Date: 5/6/21 16:15
 * @Description:
 */
data class CommentAllInfoEntity(
    val id: Long,
    val avatarUrl: String,
    val nickname: String,
    val type: Int,
    val chipList: ArrayList<ResponseChipEntity>,
    val content: String,
    val createTime: String
)