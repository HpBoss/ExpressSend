package com.noah.internet.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Auther: 何飘
 * @Date: 4/30/21 15:33
 * @Description:
 */
@Parcelize
data class ResponseAddressBook(
    val id: Int,
    val uid: Int,
    val schoolName: String,
    val detailName: String
) : Parcelable