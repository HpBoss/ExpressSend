package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.AllOrderRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.Constant
import com.noah.internet.response.BestNewOrderEntity
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 4/4/21 01:06
 * @Description:
 */
class AllOrderViewModel(application: Application) : BaseViewModel(application) {
    private val allOrderRepository by lazy {
        AllOrderRepository()
    }

    val allOrderInfo = MutableLiveData<List<BestNewOrderEntity>>()

    fun getAllOrderOfUserInfo(phoneNum: String?) {
        viewModelScope.launch {
            allOrderInfo.value = allOrderRepository.getAllOrderOfUserInfo(phoneNum).data
        }
    }

    val orderInfoToBeReceive = MutableLiveData<List<BestNewOrderEntity>>()
    fun queryToBeReceive(phoneNum: String?) {
        viewModelScope.launch {
            orderInfoToBeReceive.value = allOrderRepository.queryToBeReceive(phoneNum).data
        }
    }

    val orderInfoCompleted = MutableLiveData<List<BestNewOrderEntity>>()
    fun queryCompleted(phoneNum: String?) {
        viewModelScope.launch {
            orderInfoCompleted.value = allOrderRepository.queryCompleted(phoneNum).data
        }
    }

    val orderInfoReceived = MutableLiveData<List<BestNewOrderEntity>>()
    fun queryReceived(phoneNum: String?) {
        viewModelScope.launch {
            orderInfoReceived.value = allOrderRepository.queryReceived(phoneNum).data
        }
    }

    val orderInfoFetch = MutableLiveData<List<BestNewOrderEntity>>()
    fun queryToBeFetch(phoneNum: String?) {
        viewModelScope.launch {
            orderInfoFetch.value = allOrderRepository.queryToBeFetch(phoneNum).data
        }
    }

    val orderInfoSend = MutableLiveData<List<BestNewOrderEntity>>()
    fun queryToBeSend(phoneNum: String?) {
        viewModelScope.launch {
            orderInfoSend.value = allOrderRepository.queryToBeSend(phoneNum).data
        }
    }

    val orderInfoComment = MutableLiveData<List<BestNewOrderEntity>>()
    fun queryToBeComment(phoneNum: String?) {
        viewModelScope.launch {
            orderInfoComment.value = allOrderRepository.queryToBeComment(phoneNum).data
        }
    }
}