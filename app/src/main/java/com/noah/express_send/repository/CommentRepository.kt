package com.noah.express_send.repository

import com.noah.internet.RetrofitClient
import com.noah.internet.request.RequestCommentEntity

/**
 * @Auther: 何飘
 * @Date: 5/5/21 23:18
 * @Description:
 */
class CommentRepository {
    suspend fun getAllCommentChips() =
        RetrofitClient.instance!!.service.getAllCommentChips()

    suspend fun commentOrder(requestCommentEntity: RequestCommentEntity) =
        RetrofitClient.instance!!.service.commentOrder(requestCommentEntity)
}