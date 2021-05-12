package com.noah.express_send.viewModle

import android.app.Application
import androidx.lifecycle.*
import com.noah.express_send.repository.IndexRepository
import com.noah.express_send.ui.base.BaseViewModel
import com.noah.internet.Constant
import com.noah.internet.request.RequestFilterOrder
import com.noah.internet.response.ResponseExpress
import com.noah.internet.response.ResponseOrder
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

    var dataList = MutableLiveData<ArrayList<ResponseOrder>>()
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
            val oldValue = dataList.value ?: ArrayList<ResponseOrder>()
            oldValue.addAll(data)
            dataList.value = oldValue
        }
    }

    fun getAllFilterOrder(requestFilterOrder: RequestFilterOrder) {
        viewModelScope.launch {
            if (requestFilterOrder.page == 1) dataList.value?.clear()
            if (requestFilterOrder.page > _totalPage) return@launch
            val pageOrder =
                indexRepository.getAllFilterOrder(requestFilterOrder)
            if (pageOrder.resultCode == 0) {
                dataList.value = ArrayList<ResponseOrder>()
                return@launch
            }
            val pageQuery = pageOrder.data
            _totalPage = pageQuery.totalPage!!
            val data = pageQuery.dataList
            val oldValue = dataList.value ?: ArrayList<ResponseOrder>()
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

    var expressList = MutableLiveData<ArrayList<ResponseExpress>>()
    fun getAllExpressName() {
        viewModelScope.launch {
            expressList.value = indexRepository.getAllExpressName().data
        }
    }
}
