package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.OrderDetailsRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.response.CommentAllInfoEntity
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 5/5/21 01:24
 * @Description:
 */
class OrderDetailsViewModel(application: Application) : BaseViewModel(application) {
    private val orderDetailsRepository by lazy {
        OrderDetailsRepository()
    }


    var commentInfoWithChip = MutableLiveData<CommentAllInfoEntity>()
    fun getCommentByOid(oid: String?) {
        viewModelScope.launch {
            commentInfoWithChip.value = orderDetailsRepository.getCommentByOid(oid).data
        }
    }
}