package com.noah.internet

import com.noah.internet.request.*
import com.noah.internet.response.ResponseOrderOfUser
import com.noah.internet.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface RequestApi {
    @POST(Constant.LOGIN_API)
    suspend fun phoneNumLogin(
        @Body requestLoginUser: RequestLoginUser
    ): BackResultData<ResponsePhone>

    @POST(Constant.SIGN_OUT)
    suspend fun signOut(@Path("phoneNum") phoneNum: String?): BackResultData<*>

    @POST(Constant.UPDATE_PROFILE)
    suspend fun updateUserProfile(
        @Path("phoneNum") phoneNum: String?,
        @Body requestUser: RequestUser
    ): BackResultData<*>

    @GET(Constant.GET_AVATAR_URL)
    suspend fun requestAvatarUrl(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<String>

    @GET(Constant.GET_PROFILE)
    suspend fun requestUserProfile(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<ResponseProfile>

    @GET(Constant.GET_USER_PAGE_INFO)
    suspend fun getUserPageInfo(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<ResponseUserPage>

    @Multipart
    @POST(Constant.UPDATE_AVATAR_URL)
    suspend fun updateAvatarUrl(
        @Path("phoneNum") phoneNum: String?,
        @Part part: MultipartBody.Part
    ): BackResultData<String>

    @POST(Constant.RELEASE_ORDER)
    suspend fun releasePersonalOrder(
        @Body requestOrder: RequestOrder
    ): BackResultData<*>

    @GET(Constant.GET_ORDER_PAGE_NO_CUR)
    suspend fun getPageOrderNoCur(
        @Path("page") page: String?,
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<PageQuery<ResponseOrder>>

    @GET(Constant.REQUEST_USER_ORDER_INFO)
    suspend fun getOrderOfUserInfo(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<ResponseOrderOfUser>

    @GET(Constant.REQUEST_USER_ALL_ORDER_INFO)
    suspend fun getAllOrderOfUserInfo(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<List<BestNewOrder>>

    @PUT(Constant.CANCEL_ORDER)
    suspend fun cancelUserOrder(@Path("oid") oid: String?): BackResultData<*>

    @FormUrlEncoded
    @PUT(Constant.DELETE_ORDER)
    suspend fun deleteUserOrder(
        @Field("oid") oid: String?,
        @Field("isReceiveInvisible") isReceiveInvisible: Boolean
    ): BackResultData<*>

    @PUT(Constant.CONFIRM_ORDER)
    suspend fun confirmOrder(@Path("oid") oid: String?): BackResultData<*>

    @GET(Constant.DELIVERY_ORDER)
    suspend fun deliveryOrder(@Path("oid") oid: String?): BackResultData<*>

    @GET(Constant.QUERY_TO_BE_RECEIVE)
    suspend fun queryToBeReceive(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<List<BestNewOrder>>

    @GET(Constant.QUERY_RECEIVED)
    suspend fun queryReceived(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<List<BestNewOrder>>

    @GET(Constant.QUERY_TO_BE_FETCH)
    suspend fun queryToBeFetch(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<List<BestNewOrder>>

    @GET(Constant.QUERY_TO_BE_SEND)
    suspend fun queryToBeSend(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<List<BestNewOrder>>

    @GET(Constant.QUERY_COMPLETED)
    suspend fun queryCompleted(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<List<BestNewOrder>>

    @GET(Constant.QUERY_TO_BE_COMMENT)
    suspend fun queryToBeComment(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<List<BestNewOrder>>

    @POST(Constant.COMMENT_ORDER)
    suspend fun commentOrder(@Body requestComment: RequestComment): BackResultData<*>

    @POST(Constant.RECEIVE_ORDER)
    suspend fun receiveOrder(
        @Path("id") oid: String?,
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<*>

    @POST(Constant.APPLY_ORDER)
    suspend fun applyOrder(
        @Path("id") oid: String?
    ): BackResultData<*>

    @POST(Constant.REFUSE_ORDER)
    suspend fun refuseOrder(
        @Path("id") oid: String?
    ): BackResultData<*>

    @GET(Constant.GET_PERSONAL_ADDRESS_BOOK)
    suspend fun getPersonalAddressBook(
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<ArrayList<ResponseAddressBook>>

    @DELETE(Constant.DELETE_ADDRESS_BOOK)
    suspend fun deleteAddressBook(@Path("id") id: String?): BackResultData<*>

    @FormUrlEncoded
    @PUT(Constant.EDIT_ADDRESS_BOOK)
    suspend fun editAddressBook(
        @Path("id") id: String?,
        @Field("addressName") addressName: String
    ): BackResultData<*>

    @FormUrlEncoded
    @POST(Constant.CREATE_ADDRESS_BOOK)
    suspend fun createAddressBook(
        @Field("phoneNum") phoneNum: String?,
        @Field("addressName") addressName: String
    ): BackResultData<*>

    @GET(Constant.GET_ALL_COMMENT_CHIPS)
    suspend fun getAllCommentChips(): BackResultData<ArrayList<ResponseChip>>

    @GET(Constant.GET_COMMENT_BY_OID)
    suspend fun getCommentByOid(
        @Path("oid") oid: String?
    ): BackResultData<CommentAllInfo>

    @GET(Constant.GET_ALL_EXPRESS_NAME)
    suspend fun getAllExpressName(): BackResultData<ArrayList<ResponseExpress>>

    @POST(Constant.GET_ALL_FILTER_ORDER)
    suspend fun getALlFilterOrder(
        @Body responseFilterOrder: RequestFilterOrder
    ): BackResultData<PageQuery<ResponseOrder>>
}