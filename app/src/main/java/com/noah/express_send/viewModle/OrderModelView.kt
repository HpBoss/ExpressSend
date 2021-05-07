package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.OrderRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.Constant
import com.noah.internet.response.ResponseOrderOfUser
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 4/2/21 17:43
 * @Description:
 */
class OrderModelView(application: Application) : BaseViewModel(application) {
    private val orderRepository by lazy {
        OrderRepository()
    }

    val responseOrderEntity = MutableLiveData<ResponseOrderOfUser>()
    fun getOrderOfUserInfo(phoneNum: String?) {
        viewModelScope.launch {
            responseOrderEntity.value = orderRepository.getOrderOfUserInfo(phoneNum).data
        }
    }

    val isSuccessDeliveryOrder = MutableLiveData<Boolean>()
    fun deliveryOrder(oid: String?) {
        viewModelScope.launch {
            isSuccessDeliveryOrder.value =
                orderRepository.deliveryOrder(oid).resultCode == Constant.CODE_SUCCESS
        }
    }

    val isSuccessDeleteUserOrder = MutableLiveData<Boolean>()
    fun deleteUserOrder(oid: String?, isReceiveInvisible: Boolean) {
        viewModelScope.launch {
            isSuccessDeleteUserOrder.value = orderRepository.deleteUserOrder(
                oid,
                isReceiveInvisible
            ).resultCode == Constant.CODE_SUCCESS
        }
    }
}