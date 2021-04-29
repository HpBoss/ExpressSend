package com.noah.internet

/**
 * @Auther: 何飘
 * @Date: 2/6/21 15:24
 * @Description:
 */
object Constant {
    const val CODE_SUCCESS = 200
    const val BASE_URL = "http://192.168.31.222:9090/"
    const val LOGIN_API = "sign/signIn"
    const val SIGN_OUT = "sign/signOut/{phoneNum}"
    const val UPDATE_PROFILE = "profile/modify/{phoneNum}"
    const val GET_PROFILE = "profile/getProfile/{phoneNum}"
    const val GET_AVATAR_URL = "profile/getAvatarUrl/{phoneNum}"
    const val UPDATE_AVATAR_URL = "profile/updateAvatarUrl/{phoneNum}"

    const val RELEASE_ORDER = "order/release"
    const val GET_ORDER_PAGE_NO_CUR = "order/getAllOrderNoCur/{page}/{phoneNum}"

    const val REQUEST_USER_ORDER_INFO = "order/getUserOrder/{phoneNum}"

    const val CANCEL_ORDER = "order/cancelUserOrder/{oid}"
    const val CONFIRM_ORDER = "order/confirmReceive/{oid}"
    const val REQUEST_USER_ALL_ORDER_INFO = "order/getUserAllOrder/{phoneNum}"

    const val DELIVERY_ORDER = "order/deliveryOrder/{oid}"
    const val RECEIVE_ORDER = "order/receiveOrder/{phoneNum}/{id}"
    const val COMMENT_ORDER = "order/commentOrder/{phoneNum}/{id}"

    const val QUERY_TO_BE_RECEIVE = "order/queryToBeReceive/{phoneNum}"
    const val QUERY_TO_BE_SEND = "order/queryToBeSend/{phoneNum}"
    const val QUERY_COMPLETED = "order/queryCompleted/{phoneNum}"
    const val QUERY_RECEIVED = "order/queryReceived/{phoneNum}"
    const val QUERY_TO_BE_FETCH = "order/queryToBeFetch/{phoneNum}"
    const val QUERY_TO_BE_COMMENT = "order/queryToBeComment/{phoneNum}"
}
