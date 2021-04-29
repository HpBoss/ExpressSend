package com.noah.express_send.viewModle

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.SplashRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.response.ResponseProfile
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 2/8/21 14:44
 * @Description:
 */
class SplashViewModel(application: Application) : BaseViewModel(application) {
    private val splashRepository by lazy {
        SplashRepository()
    }

    val phoneNum = MutableLiveData<String>()

    fun getPhoneNum(loginToken: String, mContext: Context) {
        viewModelScope.launch {
            phoneNum.value = splashRepository.getPhoneNum(loginToken).getResultData()?.phoneNum
            Log.d("AutoLoginView", phoneNum.value.toString())
        }
    }
}