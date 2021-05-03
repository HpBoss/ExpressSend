package com.noah.express_send.repository

import com.noah.internet.RetrofitClient

/**
 * @Auther: 何飘
 * @Date: 4/30/21 16:13
 * @Description:
 */
class EditAddressRepository {
    suspend fun editAddressBook(id: String) =
        RetrofitClient.instance!!.service.editAddressBook(id)
}