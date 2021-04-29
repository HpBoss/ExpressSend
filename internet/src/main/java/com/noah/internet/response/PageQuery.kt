package com.noah.internet.response

/**
 * @Auther: 何飘
 * @Date: 4/2/21 02:04
 * @Description:
 */

data class PageQuery<T>(
    val curPage: Int? = 0,
    val totalPage: Int? = 0,
    val totalSize: Int? = 0,
    val dataList: ArrayList<T>
)