package com.noah.internet.response

import com.noah.internet.response.BestNewOrderEntity

/**
 * @Auther: 何飘
 * @Date: 4/2/21 18:05
 * @Description:
 */
data class ResponseOrderOfUser(
    val receivedNum: Int,
    val toBeReceiverNum: Int,
    val toBeSendNum: Int,
    val toBeFetchNum: Int,
    val toBeCommentNum: Int,
    val bestNewOrderEntity: BestNewOrderEntity? = null
)