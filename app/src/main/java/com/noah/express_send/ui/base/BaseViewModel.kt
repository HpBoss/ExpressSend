package com.noah.express_send.ui.base

import android.app.Application
import androidx.lifecycle.*
import com.noah.database.User
import com.noah.express_send.repository.BaseRepository
import com.noah.internet.Constant
import kotlinx.coroutines.launch

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val baseRepository by lazy {
        BaseRepository(application)
    }

    fun insertUser(user: User) {
        baseRepository.insertUser(user)
    }

    fun queryUser(phone: String) : User? {
        return baseRepository.queryUser(phone)
    }

    fun updateUser(user: User?) {
        baseRepository.updateUser(user)
    }

    fun queryIsLoginUser(): User? {
        return baseRepository.queryIsLoginUser()
    }

    fun deleteUser(user: User) {
        baseRepository.deleteUser(user)
    }

    val isCancelOrderSuccess = MutableLiveData<Boolean>()
    val isConfirmOrderSuccess = MutableLiveData<Boolean>()

    fun cancelUserOrder(oid: String?) {
        viewModelScope.launch {
            isCancelOrderSuccess.value = baseRepository.cancelUserOrder(oid).resultCode == Constant.CODE_SUCCESS
        }
    }

    fun confirmOrder(oid: String?) {
        viewModelScope.launch {
            isConfirmOrderSuccess.value = baseRepository.confirmOrder(oid).resultCode == Constant.CODE_SUCCESS
        }
    }
}