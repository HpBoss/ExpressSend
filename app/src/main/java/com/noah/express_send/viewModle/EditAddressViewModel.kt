package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.EditAddressRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.Constant
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 4/30/21 16:14
 * @Description:
 */
class EditAddressViewModel(application: Application) : BaseViewModel(application) {
    private val editProfileViewModel by lazy { 
        EditAddressRepository()
    }

    var isSuccessEditAddressBook = MutableLiveData<Boolean>()
    fun editAddressBook(id: String) {
        viewModelScope.launch {
            isSuccessEditAddressBook.value =
                editProfileViewModel.editAddressBook(id).resultCode == Constant.CODE_SUCCESS
        }
    }
}