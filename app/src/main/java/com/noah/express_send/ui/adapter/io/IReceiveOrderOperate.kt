package com.noah.express_send.ui.adapter.io

/**
 * @Auther: 何飘
 * @Date: 5/14/21 09:53
 * @Description:
 */
interface IReceiveOrderOperate {
    fun refuseRequest(oid: String?)
    fun agreeRequest(oid: String?)
    fun clickAvatar()
}