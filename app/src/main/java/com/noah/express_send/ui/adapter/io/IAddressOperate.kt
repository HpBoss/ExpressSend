package com.noah.express_send.ui.adapter.io

import com.noah.internet.response.ResponseAddressBook

/**
 * @Auther: 何飘
 * @Date: 4/29/21 17:21
 * @Description:
 */
interface IAddressOperate {
    fun deleteAddress(position: Int, id: Int)
    fun editAddress(position: Int, responseAddressBook: ResponseAddressBook)
}