package com.noah.express_send.bean

/**
 * @Auther: 何飘
 * @Date: 4/5/21 20:33
 * @Description:
 */
data class NowMessageInfo(
    val avatarUrl: String,
    val sender: String? = "",
    val nickName: String,
    val content: String
)