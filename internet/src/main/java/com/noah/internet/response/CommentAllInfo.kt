package com.noah.internet.response

/**
 * @Auther: 何飘
 * @Date: 5/6/21 16:15
 * @Description:
 */
data class CommentAllInfo(
    val id: Long,
    val avatarUrl: String,
    val nickname: String,
    val type: Int,
    val chipList: ArrayList<ResponseChip>,
    val content: String,
    val createTime: String
)