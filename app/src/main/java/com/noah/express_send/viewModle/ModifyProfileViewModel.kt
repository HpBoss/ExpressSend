package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.ModifyProfileRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.Constant
import com.noah.internet.request.RequestUserEntity
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 3/27/21 23:05
 * @Description:
 */
class ModifyProfileViewModel(application: Application) : BaseViewModel(application) {
    private val modifyProfileRepository by lazy {
        ModifyProfileRepository()
    }
    private val _num = MutableLiveData<Int>().also {
        it.value = 30
    }

    private val success = MutableLiveData<Boolean>().also {
        it.value = false
    }

    val num: LiveData<Int> = _num
    var isUpdateSuccess = MutableLiveData<Boolean>()

    fun reduceOne() {
        if (_num.value == 0) return
        _num.value = _num.value?.minus(1)
    }

    fun reduceAny(startLen: Int) {
        if (_num.value!! < startLen) {
            _num.value = 0
        } else {
            _num.value = _num.value!! - startLen
        }
    }

    fun cleanUp() {
        _num.value = 30
    }

    fun addOne() {
        if (_num.value == 30) return
        _num.value = _num.value?.plus(1)
    }

    fun updateUserProfile(phoneNum: String?, requestUserEntity: RequestUserEntity) {
        viewModelScope.launch {
            isUpdateSuccess.value =
                modifyProfileRepository.updateUserProfile(phoneNum, requestUserEntity).resultCode == Constant.CODE_SUCCESS
        }
    }

}
