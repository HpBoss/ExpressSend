package com.noah.express_send.repository

import android.content.Context
import com.noah.database.AppDatabase
import com.noah.database.User
import com.noah.internet.RetrofitClient

/**
 * @Auther: 何飘
 * @Date: 3/30/21 19:00
 * @Description:
 */
class BaseRepository(context: Context) {
    private val appDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    private val userDao by lazy {
        appDatabase?.userDao()
    }

    // User表增删改查
    fun insertUser(user: User) {
        userDao?.insert(user)
    }

    fun queryUser(phone: String) : User? {
        return userDao?.getUser(phone)
    }

    fun updateUser(user: User?) {
        userDao?.update(user)
    }

    fun queryIsLoginUser(): User?{
        return userDao?.getIsLoginUser()
    }

    fun deleteUser(user: User) {
        userDao?.delete(user)
    }

    suspend fun cancelUserOrder(oid: String?) =
        RetrofitClient.instance!!.service.cancelUserOrder(oid)

    suspend fun confirmOrder(oid: String?) =
        RetrofitClient.instance!!.service.confirmOrder(oid)

}