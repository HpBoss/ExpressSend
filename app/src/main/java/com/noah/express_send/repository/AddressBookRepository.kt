package com.noah.express_send.repository

import com.noah.internet.RetrofitClient

/**
 * @Auther: 何飘
 * @Date: 4/30/21 14:46
 * @Description:
 */
class AddressBookRepository {
    suspend fun getPersonalAddressBook(phoneNum: String?) =
        RetrofitClient.instance!!.service.getPersonalAddressBook(phoneNum)

    suspend fun deleteAddressBook(id: String) =
        RetrofitClient.instance!!.service.deleteAddressBook(id)
}