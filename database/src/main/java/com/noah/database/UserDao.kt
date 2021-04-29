package com.noah.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM user WHERE phone_num = :phone")
    fun getUser(phone: String): User

    @Query("SELECT * FROM user WHERE is_login = '1'")
    fun getIsLoginUser(): User

    @Insert
    fun insert(user: User)

    @Delete
    fun delete(user: User)

    @Update
    fun update(user: User?)
}
    