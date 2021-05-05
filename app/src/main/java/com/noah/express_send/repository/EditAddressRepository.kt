package com.noah.express_send.repository

import com.noah.internet.RetrofitClient

/**
 * @Auther: 何飘
 * @Date: 4/30/21 16:13
 * @Description:
 */
class EditAddressRepository {
    suspend fun editAddressBook(id: String, addressName: String) =
        RetrofitClient.instance!!.service.editAddressBook(id, addressName)

    suspend fun createAddressBook(phoneNum: String?, addressName: String) =
        RetrofitClient.instance!!.service.createAddressBook(phoneNum, addressName)
}