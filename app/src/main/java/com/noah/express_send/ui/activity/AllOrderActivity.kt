package com.noah.express_send.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.model.UserInfo
import com.google.android.material.tabs.TabLayout
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.database.User
import com.noah.express_send.R
import com.noah.express_send.ui.adapter.AllOrderAdapter
import com.noah.express_send.ui.adapter.io.IOrderOperate
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.ui.view.CustomHeader
import com.noah.express_send.utils.NetWorkAvailableUtil
import com.noah.express_send.viewModle.AllOrderViewModel
import com.noah.internet.response.BestNewOrderEntity
import com.scwang.smart.refresh.footer.ClassicsFooter
import kotlinx.android.synthetic.main.activity_all_order.*
import kotlinx.android.synthetic.main.activity_all_order.refreshLayout
import kotlinx.android.synthetic.main.item_status_bar.*
import me.leefeng.promptlibrary.PromptDialog

class AllOrderActivity : BaseActivity(), IOrderOperate {
    private lateinit var adapter: AllOrderAdapter
    private var curUser: User? = null
    private var position: Int = -1
    private val promptDialog by lazy {
        PromptDialog(this)
    }
    private val orderInfoList by lazy {
        ArrayList<BestNewOrderEntity>()
    }
    private val allOrderViewModel by lazy {
        ViewModelProvider(this).get(AllOrderViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_all_order
    }
    override fun isSetSoftInputMode() = true

    override fun initView() {
        immersionBar {
            statusBarView(status_bar_view)
            statusBarDarkFont(false)
        }
        orderBack.setOnClickListener {
            onBackPressed()
        }
        val intent = intent
        val page = intent.getIntExtra("page", 0)
        tableLayout.getTabAt(page)?.select()
        loadPageData(page)
        allOrderViewModel.allOrderInfo.observe(this, {
            initOrderInfo(it)
            adapter.notifyDataSetChanged()
        })
        allOrderViewModel.orderInfoToBeReceive.observe(this, {
            initOrderInfo(it)
            adapter.notifyDataSetChanged()
        })
        allOrderViewModel.orderInfoComment.observe(this, {
            initOrderInfo(it)
            adapter.notifyDataSetChanged()
        })
        allOrderViewModel.orderInfoFetch.observe(this, {
            initOrderInfo(it)
            adapter.notifyDataSetChanged()
        })
        allOrderViewModel.orderInfoReceived.observe(this, {
            initOrderInfo(it)
            adapter.notifyDataSetChanged()
        })
        allOrderViewModel.orderInfoSend.observe(this, {
            initOrderInfo(it)
            adapter.notifyDataSetChanged()
        })
        allOrderViewModel.isCancelOrderSuccess.observe(this, {
            if (!it) {
                promptDialog.showError("取消订单失败")
                return@observe
            }
            adapter.removeAt(position)
            promptDialog.showSuccess("取消订单成功")
        })
        allOrderViewModel.isConfirmOrderSuccess.observe(this, {
            if (!it) {
                promptDialog.showError("确认收货失败")
                return@observe
            }
            val user = allOrderViewModel.queryIsLoginUser()
            if (user != null) {
                user.integralNum = user.integralNum?.minus(1)
                allOrderViewModel.updateUser(user)
            }
            adapter.removeAt(position)
            promptDialog.showSuccess("确认收货成功")
        })
        allOrderViewModel.isSuccessDeleteUserOrder.observe(this, {
            if (!it) {
                promptDialog.showError(getString(R.string.delete_failure))
                return@observe
            }
            adapter.removeAt(position)
            promptDialog.showSuccess(getString(R.string.delete_success))
        })
    }

    override fun initData() {
        initRefreshLayout()
        initRecycleView()
        listenTabLayout()
    }

    override fun onStart() {
        super.onStart()
        loadPageData(tableLayout.selectedTabPosition)
    }

    private fun listenTabLayout() {
        tableLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                recycleView.scrollToPosition(0)
                loadPageData(tab!!.position)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun initRefreshLayout() {
        refreshLayout.setRefreshHeader(CustomHeader(this))
        refreshLayout.setHeaderHeight(60F)

        // 下拉刷新
        refreshLayout.setOnRefreshListener {
            if (NetWorkAvailableUtil.isNetworkAvailable(this)) {
                // 刷新整个list数据
                loadPageData(tableLayout.selectedTabPosition)
                refreshLayout.finishRefresh()
            } else {
                refreshLayout.finishRefresh(false)
                Toast.makeText(this, resources.getString(R.string.network_invalid), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        recycleView.layoutManager = layoutManager
        adapter = AllOrderAdapter(orderInfoList, this, this)
        recycleView.adapter = adapter
    }

    private fun initOrderInfo(dataList: List<BestNewOrderEntity>) {
        orderInfoList.clear()
        for (index in dataList.indices) {
            orderInfoList.add(dataList[index])
        }
    }

    private fun loadPageData(page: Int) {
        if (curUser == null) curUser = allOrderViewModel.queryIsLoginUser() ?: return
        when(page) {
            0 -> {allOrderViewModel.getAllOrderOfUserInfo(curUser?.phoneNum)}
            1 -> {allOrderViewModel.queryToBeReceive(curUser?.phoneNum)}
            2 -> {allOrderViewModel.queryToBeSend(curUser?.phoneNum)}
            3 -> {allOrderViewModel.queryToBeFetch(curUser?.phoneNum)}
            4 -> {allOrderViewModel.queryToBeComment(curUser?.phoneNum)}
        }
    }

    override fun cancelOrder(oid: String?, position: Int) {
        promptDialog.showLoading("")
        this.position = position
        allOrderViewModel.cancelUserOrder(oid)
    }

    override fun deleteOrder(oid: String?, position: Int) {
        promptDialog.showLoading("")
        this.position = position
        allOrderViewModel.deleteUserOrder(oid, false)
    }

    override fun confirmOrder(oid: String?, position: Int) {
        promptDialog.showLoading("")
        this.position = position
        allOrderViewModel.confirmOrder(oid)
    }

    override fun commentOrder(orderInfo: BestNewOrderEntity) {
        val intent = Intent(this, CommentActivity::class.java)
        val extras = Bundle()
        extras.putParcelable("orderInfo", orderInfo)
        intent.putExtras(extras)
        startActivity(intent)
    }

    override fun modifyOrder(oid: String?, phoneNum: String?, orderInfo: BestNewOrderEntity) {
        val intent = Intent(this, ReleaseActivity::class.java)
        val extras = Bundle()
        extras.putParcelable("orderInfo", orderInfo)
        intent.putExtra("mode", 1)
        intent.putExtras(extras)
        startActivity(intent)
    }

    override fun browseUserPageInfo(phoneNum: String?, nickname: String?) {
        val intent = Intent(this, UserHomePageActivity::class.java)
        intent.putExtra("phoneNum", phoneNum)
        intent.putExtra("nickname", nickname)
        startActivity(intent)
    }
}