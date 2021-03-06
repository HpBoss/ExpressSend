package com.noah.internet

/**
 * @Auther: 何飘
 * @Date: 2/6/21 15:24
 * @Description:
 */
object Constant {
    // 172.20.10.4
    // 192.168.31.222
    const val CODE_SUCCESS = 200
    const val CODE_FAILURE = 0
    // 别搞我服务器，各位大哥
    const val BASE_URL = "http://47.102.206.167:9090/"
    const val LOGIN_API = "sign/signIn"
    const val SIGN_OUT = "sign/signOut/{phoneNum}"
    const val UPDATE_PROFILE = "profile/modify/{phoneNum}"
    const val GET_PROFILE = "profile/getProfile/{phoneNum}"
    const val GET_AVATAR_URL = "profile/getAvatarUrl/{phoneNum}"
    const val UPDATE_AVATAR_URL = "profile/updateAvatarUrl/{phoneNum}"
    const val GET_USER_PAGE_INFO = "profile/getUserPageInfo/{phoneNum}"

    const val RELEASE_ORDER = "order/release"
    const val GET_ORDER_PAGE_NO_CUR = "order/getAllOrderNoCur/{page}/{phoneNum}"

    const val REQUEST_USER_ORDER_INFO = "order/getUserOrder/{phoneNum}"

    const val CANCEL_ORDER = "order/cancelUserOrder/{oid}"
    const val DELETE_ORDER = "order/deleteUserOrder"
    const val CONFIRM_ORDER = "order/confirmReceive/{oid}"
    const val REQUEST_USER_ALL_ORDER_INFO = "order/getUserAllOrder/{phoneNum}"

    const val DELIVERY_ORDER = "order/deliveryOrder/{oid}"
    const val RECEIVE_ORDER = "order/receiveOrder/{phoneNum}/{id}"
    const val APPLY_ORDER = "order/applyReceiveOrder/{id}"
    const val REFUSE_ORDER = "order/refuseReceiveOrder/{id}"
    const val QUERY_TO_BE_RECEIVE = "order/queryToBeReceive/{phoneNum}"

    const val QUERY_TO_BE_SEND = "order/queryToBeSend/{phoneNum}"
    const val QUERY_COMPLETED = "order/queryCompleted/{phoneNum}"
    const val QUERY_RECEIVED = "order/queryReceived/{phoneNum}"
    const val QUERY_TO_BE_FETCH = "order/queryToBeFetch/{phoneNum}"
    const val QUERY_TO_BE_COMMENT = "order/queryToBeComment/{phoneNum}"
    const val GET_PERSONAL_ADDRESS_BOOK = "address/getPersonalAddressBook/{phoneNum}"

    const val DELETE_ADDRESS_BOOK = "address/deleteAddressBook/{id}"
    const val EDIT_ADDRESS_BOOK = "address/editAddressBook/{id}"
    const val CREATE_ADDRESS_BOOK = "address/createAddressBook"

    const val COMMENT_ORDER = "comment/commentOrder"
    const val GET_ALL_COMMENT_CHIPS = "comment/getAllCommentChips"
    const val GET_COMMENT_BY_OID = "comment/getCommentByOid/{oid}"

    const val GET_ALL_EXPRESS_NAME = "filter/getAllExpressName"
    const val GET_ALL_FILTER_ORDER = "filter/getALlFilterOrder"
}
