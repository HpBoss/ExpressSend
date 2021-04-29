package com.noah.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) var uid: Int?,
    @ColumnInfo(name = "phone_num") var phoneNum: String?,
    @ColumnInfo(name = "is_login") var isLogin: Int? = 0,
    @ColumnInfo(name = "avatar_path") var avatarPath: String? = null,
    @ColumnInfo(name = "nick_name") var nickName: String? = null,
    @ColumnInfo(name = "school_name") var schoolName: String? = null,
    @ColumnInfo(name = "integralNum") var integralNum: Int? = 0,
    @ColumnInfo(name = "email") var email: String? = null,
    @ColumnInfo(name = "dormitory") var dormitory: String? = null,
    @ColumnInfo(name = "room_number") var roomNumber: String? = null
)