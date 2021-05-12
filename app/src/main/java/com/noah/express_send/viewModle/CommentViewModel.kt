package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.CommentRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.Constant
import com.noah.internet.request.RequestComment
import com.noah.internet.response.ResponseChip
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 5/5/21 23:19
 * @Description:
 */
class CommentViewModel(application: Application) : BaseViewModel(application) {
    private val commentRepository by lazy {
        CommentRepository()
    }

    var chipsList = MutableLiveData<ArrayList<ResponseChip>>()
    fun getAllCommentChips() {
        viewModelScope.launch {
            chipsList.value = commentRepository.getAllCommentChips().data
        }
    }

    val isSuccessCommentOrder = MutableLiveData<Boolean>()
    fun commentOrder(requestComment: RequestComment) {
        viewModelScope.launch {
            isSuccessCommentOrder.value = commentRepository.commentOrder(requestComment).resultCode == Constant.CODE_SUCCESS
        }
    }
}