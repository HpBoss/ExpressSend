package com.noah.internet

import com.noah.internet.request.RequestLoginUser
import com.noah.internet.request.RequestOrderEntity
import com.noah.internet.response.ResponseOrderOfUser
import com.noah.internet.request.RequestUserEntity
import com.noah.internet.response.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface RequestApi {
    @POST(Constant.LOGIN_API)
    suspend fun phoneNumLogin(@Body requestLoginUser: RequestLoginUser): BackResultData<ResponsePhone>

    @POST(Constant.SIGN_OUT)
    suspend fun signOut(@Path("phoneNum") phoneNum: String?): BackResultData<*>

    @POST(Constant.UPDATE_PROFILE)
    suspend fun updateUserProfile(
        @Path("phoneNum") phoneNum: String?,
        @Body requestUserEntity: RequestUserEntity
    ): BackResultData<*>

    @GET(Constant.GET_AVATAR_URL)
    suspend fun requestAvatarUrl(@Path("phoneNum") phoneNum: String?): BackResultData<String>

    @GET(Constant.GET_PROFILE)
    suspend fun requestUserProfile(@Path("phoneNum") phoneNum: String?): BackResultData<ResponseProfile>

    @Multipart
    @POST(Constant.UPDATE_AVATAR_URL)
    suspend fun updateAvatarUrl(
        @Path("phoneNum") phoneNum: String?,
        @Part part: MultipartBody.Part
    ): BackResultData<String>

    @POST(Constant.RELEASE_ORDER)
    suspend fun releasePersonalOrder(@Body requestOrderEntity: RequestOrderEntity): BackResultData<*>

    @GET(Constant.GET_ORDER_PAGE_NO_CUR)
    suspend fun getPageOrderNoCur(
        @Path("page") page: String?,
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<PageQuery<ResponseOrderEntity>>

    @GET(Constant.REQUEST_USER_ORDER_INFO)
    suspend fun getOrderOfUserInfo(@Path("phoneNum") phoneNum: String?): BackResultData<ResponseOrderOfUser>

    @GET(Constant.REQUEST_USER_ALL_ORDER_INFO)
    suspend fun getAllOrderOfUserInfo(@Path("phoneNum") phoneNum: String?): BackResultData<List<BestNewOrderEntity>>

    @PUT(Constant.CANCEL_ORDER)
    suspend fun cancelUserOrder(@Path("oid") oid: String?): BackResultData<*>

    @PUT(Constant.CONFIRM_ORDER)
    suspend fun confirmOrder(@Path("oid") oid: String?): BackResultData<*>

    @GET(Constant.DELIVERY_ORDER)
    suspend fun deliveryOrder(@Path("oid") oid: String?): BackResultData<*>

    @GET(Constant.QUERY_TO_BE_RECEIVE)
    suspend fun queryToBeReceive(@Path("phoneNum") phoneNum: String?): BackResultData<List<BestNewOrderEntity>>

    @GET(Constant.QUERY_RECEIVED)
    suspend fun queryReceived(@Path("phoneNum") phoneNum: String?): BackResultData<List<BestNewOrderEntity>>

    @GET(Constant.QUERY_TO_BE_FETCH)
    suspend fun queryToBeFetch(@Path("phoneNum") phoneNum: String?): BackResultData<List<BestNewOrderEntity>>

    @GET(Constant.QUERY_TO_BE_SEND)
    suspend fun queryToBeSend(@Path("phoneNum") phoneNum: String?): BackResultData<List<BestNewOrderEntity>>

    @GET(Constant.QUERY_COMPLETED)
    suspend fun queryCompleted(@Path("phoneNum") phoneNum: String?): BackResultData<List<BestNewOrderEntity>>

    @GET(Constant.QUERY_TO_BE_COMMENT)
    suspend fun queryToBeComment(@Path("phoneNum") phoneNum: String?): BackResultData<List<BestNewOrderEntity>>

    @GET(Constant.COMMENT_ORDER)
    suspend fun commentOrder(@Path("id") id: String?): BackResultData<*>

    @POST(Constant.RECEIVE_ORDER)
    suspend fun receiveOrder(
        @Path("id") oid: String?,
        @Path("phoneNum") phoneNum: String?
    ): BackResultData<*>
}