package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.UserHomeRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.response.ResponseUserPage
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 5/6/21 14:41
 * @Description:
 */
class UserHomeViewModel(application: Application) :BaseViewModel(application) {
    private val userHomeRepository by lazy {
        UserHomeRepository()
    }

    val pageInfo = MutableLiveData<ResponseUserPage>()
    fun getUserPageInfo(phoneNum: String?) {
        viewModelScope.launch {
            pageInfo.value = userHomeRepository.getUserPageInfo(phoneNum).data
        }
    }
}