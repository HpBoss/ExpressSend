package com.noah.express_send.viewModle

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.MainRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.Constant
import com.noah.internet.response.ResponseProfile
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 2/8/21 14:43
 * @Description:
 */
class MainViewModel(application: Application) : BaseViewModel(application) {
    private val mainRepository by lazy {
        MainRepository()
    }

    var isSignOutSuccess = MutableLiveData<Boolean>()
    fun signOut(phoneNum: String?) {
        viewModelScope.launch {
            isSignOutSuccess.value = mainRepository.signOut(phoneNum).resultCode == Constant.CODE_SUCCESS
        }
    }

    var avatarUrl = MutableLiveData<String>()
    fun requestAvatarUrl(phoneNum: String?) {
        viewModelScope.launch {
            avatarUrl.value = mainRepository.requestAvatarUrl(phoneNum).data
        }
    }

    var profiles = MutableLiveData<ResponseProfile>()

    fun requestUserProfile(phoneNum: String?) {
        viewModelScope.launch {
            profiles.value = mainRepository.requestUserProfile(phoneNum).data
        }
    }

    val phoneNum = MutableLiveData<String>()

    fun getPhoneNum(loginToken: String, mContext: Context) {
        viewModelScope.launch {
            phoneNum.value = mainRepository.getPhoneNum(loginToken).getResultData()?.phoneNum
            Log.d("AutoLoginView", phoneNum.value.toString())
        }
    }
}