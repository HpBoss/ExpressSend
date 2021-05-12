package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.ReleaseRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.Constant
import com.noah.internet.request.RequestOrder
import kotlinx.coroutines.launch

class ReleaseViewModel(application: Application) : BaseViewModel(application) {
    // TODO: Implement the ViewModel
    private val releaseRepository by lazy {
        ReleaseRepository()
    }

    val isReleaseSuccess = MutableLiveData<Boolean>()

    fun releasePersonalOrder(requestOrder: RequestOrder) {
        viewModelScope.launch {
            isReleaseSuccess.value =
                releaseRepository.releasePersonalOrder(requestOrder).resultCode == Constant.CODE_SUCCESS
        }
    }
}