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
}