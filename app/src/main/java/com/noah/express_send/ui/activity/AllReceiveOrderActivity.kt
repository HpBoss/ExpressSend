package com.noah.express_send.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.express_send.R
import com.noah.express_send.ui.adapter.HistoryCommentAdapter
import com.noah.express_send.ui.adapter.OrderPagerAdapter
import com.noah.express_send.ui.adapter.io.IOrderDetails
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.viewModle.OrderModelView
import com.noah.internet.response.BestNewOrderEntity
import kotlinx.android.synthetic.main.activity_all_receive_order.*
import kotlinx.android.synthetic.main.activity_user_home_page.*
import kotlinx.android.synthetic.main.item_action_bar.*
import kotlinx.android.synthetic.main.item_order_info_message.*
import kotlinx.android.synthetic.main.item_status_bar.*
import me.leefeng.promptlibrary.PromptDialog

class AllReceiveOrderActivity : BaseActivity(), IOrderDetails {
    private var receiveOrderList = ArrayList<BestNewOrderEntity>()
    private lateinit var adapter: OrderPagerAdapter
    private var position: Int = -1
    private val promptDialog by lazy {
        PromptDialog(this)
    }

    private val orderModelView by lazy {
        ViewModelProvider(this).get(OrderModelView::class.java)
    }

    override fun getLayoutId() = R.layout.activity_all_receive_order
    override fun isSetSoftInputMode() = true

    override fun initView() {
        immersionBar {
            statusBarView(status_bar_view)
            statusBarDarkFont(false)
        }
        actionBar_title.text = getString(R.string.receive_my_order)
        receiveOrderList =
            intent.getParcelableArrayListExtra<BestNewOrderEntity>("receiveOrderList")
                    as ArrayList<BestNewOrderEntity>
        initRecycleView()
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        recycleView_receiveOrder.layoutManager = layoutManager
        adapter = OrderPagerAdapter(this, this, R.layout.item_order_info_message)
        adapter.setAdapter(receiveOrderList)
        recycleView_receiveOrder.adapter = adapter
    }

    override fun initData() {
        orderModelView.isSuccessDeliveryOrder.observe(this, {
            if (!it) {
                promptDialog.showError("派送订单失败")
                return@observe
            }
            btn_changeOrderState.visibility = View.GONE
            tv_orderState.text = "已派单"
            promptDialog.showSuccess("派送订单成功")
        })
        orderModelView.isSuccessDeleteUserOrder.observe(this, {
            if (!it) {
                promptDialog.showError(getString(R.string.delete_failure))
                return@observe
            }
            promptDialog.showSuccess(getString(R.string.delete_success))
            adapter.removeAt(position)
        })
        back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun entryOrderDetails(bestNewOrderEntity: BestNewOrderEntity) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        intent.putExtra("bestNewOrderEntity", bestNewOrderEntity)
        startActivity(intent)
    }

    override fun startDeliverOrder(bestNewOrderEntity: BestNewOrderEntity) {
        promptDialog.showLoading("")
        orderModelView.deliveryOrder(bestNewOrderEntity.oid.toString())
    }

    override fun browseUserPageInfo(phoneNum: String?, nickname: String?) {
        val intent = Intent(this, UserHomePageActivity::class.java)
        intent.putExtra("phoneNum", phoneNum)
        intent.putExtra("nickname", nickname)
        startActivity(intent)
    }

    override fun deleteOrder(oid: String?, position: Int) {
        // 在order表中再增加一个字段，表示每一个订单是否对接收者可见（对于接单者来说算是假装删除了订单）
        this.position = position
        orderModelView.deleteUserOrder(oid, true)
    }

}