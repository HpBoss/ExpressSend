package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.noah.express_send.repository.AddressBookRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.Constant
import com.noah.internet.response.ResponseAddressBook
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 4/30/21 14:46
 * @Description:
 */
class AddressBookViewModel(application: Application) : BaseViewModel(application) {
    private val addressBookRepository by lazy {
        AddressBookRepository()
    }

    var addressBookList = MutableLiveData<ArrayList<ResponseAddressBook>>()
    fun getPersonalAddressBook(phoneNum: String?) {
        viewModelScope.launch {
            addressBookList.value = addressBookRepository.getPersonalAddressBook(phoneNum).data
        }
    }

    var isSuccessDeleteAddressBook = MutableLiveData<Boolean>()
    fun deleteAddressBook(id: String) {
        viewModelScope.launch {
            isSuccessDeleteAddressBook.value =
                addressBookRepository.deleteAddressBook(id).resultCode == Constant.CODE_SUCCESS
        }
    }
}