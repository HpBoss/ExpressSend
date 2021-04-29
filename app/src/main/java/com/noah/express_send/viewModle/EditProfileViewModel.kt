package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.EditProfileRepository
import com.noah.express_send.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

/**
 * @Auther: 何飘
 * @Date: 3/31/21 10:35
 * @Description:
 */
class EditProfileViewModel(application: Application) : BaseViewModel(application) {
    private val editProfileRepository by lazy {
        EditProfileRepository()
    }

    var avatarUrl = MutableLiveData<String>()

    fun updateAvatarUrl(phoneNum: String?, part: MultipartBody.Part) {
        viewModelScope.launch {
            avatarUrl.value = editProfileRepository.updateAvatarUrl(phoneNum, part).data
        }
    }

}