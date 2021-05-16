package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.ChatRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.Constant
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 4/6/21 18:07
 * @Description:
 */
class ChatViewModel(application: Application) : BaseViewModel(application) {
    private val chatRepository by lazy {
        ChatRepository()
    }

    var isReceiveOrderSuccess = MutableLiveData<Boolean>()
    fun receiveOrder(id: String?, phoneNum: String?) {
        viewModelScope.launch {
            isReceiveOrderSuccess.value =
                chatRepository.receiveOrder(id, phoneNum).resultCode == Constant.CODE_SUCCESS
        }
    }

    var isRefuseOrderSuccess = MutableLiveData<Boolean>()
    fun refuseOrder(id: String?) {
        viewModelScope.launch {
            isRefuseOrderSuccess.value = chatRepository.refuseOrder(id).resultCode == Constant.CODE_SUCCESS
        }
    }
}