package com.noah.express_send.viewModle

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.noah.express_send.repository.IndexRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.Constant
import com.noah.internet.response.ResponseOrderEntity
import com.noah.internet.response.ResponseProfile
import kotlinx.coroutines.launch

/**
 * @Auther: 何飘
 * @Date: 3/29/21 18:17
 * @Description:
 */
class IndexViewModel(application: Application) : BaseViewModel(application) {
    private val indexRepository by lazy {
        IndexRepository()
    }

    var dataList = MutableLiveData<ArrayList<ResponseOrderEntity>>()
    private var _totalPage = 1
    fun getPageOrderNoCur(page: Int, phoneNum: String?) {
        viewModelScope.launch {
            if (page == 1) dataList.value?.clear()
            if (page > _totalPage) return@launch
            val pageOrder = indexRepository.getPageOrderNoCur(page.toString(), phoneNum)
            if (pageOrder.resultCode == 0) return@launch
            val pageQuery = pageOrder.data
            _totalPage = pageQuery.totalPage!!
            val data = pageQuery.dataList
            val oldValue = dataList.value ?: ArrayList<ResponseOrderEntity>()
            oldValue.addAll(data)
            dataList.value = oldValue
        }
    }

    var isReceiveOrderSuccess = MutableLiveData<Boolean>()
    fun receiveOrder(id: String?, phoneNum: String?) {
        viewModelScope.launch {
            isReceiveOrderSuccess.value =
                indexRepository.receiveOrder(id, phoneNum).resultCode == Constant.CODE_SUCCESS
        }
    }
}
