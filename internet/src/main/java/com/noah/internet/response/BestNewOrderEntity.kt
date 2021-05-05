package com.noah.internet.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * @Auther: 何飘
 * @Date: 4/3/21 16:29
 * @Description:
 */
@Parcelize
data class BestNewOrderEntity(
    val oid: Long? = null,
    val avatarUrl: String? = null,
    val nickName: String? = null,
    val dormitory: String? = null,
    val express: String? = null,
    val typeName: String? = null,
    val weight: String? = null,
    val addressName: String? = null,
    val stateName: String? = null,
    val phoneNum: String? = null,
    val payIntegralNum: Int? = null,
    val operateTime: String? = null
): Parcelable