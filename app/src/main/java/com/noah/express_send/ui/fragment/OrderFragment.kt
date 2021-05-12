package com.noah.express_send.ui.fragment

import android.content.Intent
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.noah.database.User
import com.noah.express_send.R
import com.noah.express_send.ui.activity.AllOrderActivity
import com.noah.express_send.ui.activity.AllReceiveOrderActivity
import com.noah.express_send.ui.activity.OrderDetailsActivity
import com.noah.express_send.ui.activity.UserHomePageActivity
import com.noah.express_send.ui.adapter.OrderPagerAdapter
import com.noah.express_send.ui.adapter.io.IOrderDetails
import com.noah.express_send.ui.base.BaseFragment
import com.noah.express_send.ui.view.CustomHeader
import com.noah.express_send.utils.NetWorkAvailableUtil
import com.noah.express_send.viewModle.OrderModelView
import com.noah.internet.response.BestNewOrder
import com.noah.internet.response.ResponseOrderOfUser
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.fragment_order.view.*
import kotlinx.android.synthetic.main.item_order_info_message.*
import kotlinx.android.synthetic.main.item_order_info_message.btn_changeOrderState
import kotlinx.android.synthetic.main.item_order_info_message.tv_orderState
import kotlinx.android.synthetic.main.item_order_info_messages.*
import me.leefeng.promptlibrary.PromptDialog
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView


class OrderFragment : BaseFragment(), View.OnClickListener, IOrderDetails {
    private var mCurrentItem = 0
    private var position: Int = -1
    private var curUser: User? = null
    private var receiveOrderList = ArrayList<BestNewOrder>()
    private var filterReceiveOrderList = ArrayList<BestNewOrder>()
    private val badges by lazy {
        ArrayList<Badge>()
    }

    private val promptDialog by lazy {
        PromptDialog(requireActivity())
    }

    private val orderModelView by lazy {
        ViewModelProvider(this).get(OrderModelView::class.java)
    }

    override fun getLayoutId() = R.layout.fragment_order

    override fun initView() {
        viewAll.setOnClickListener(this)
        tv_ToBeSendOrder.setOnClickListener(this)
        tv_ToBeComment.setOnClickListener(this)
        tv_ToBeReceived.setOnClickListener(this)
        tv_ToBeReceivedOrder.setOnClickListener(this)
        initObserver()
        initRefreshLayout()
        initBadgeTextView()
        // 去除viewPager2边缘的阴影（在xml中设置overScrollMode为never无效）
        val child: View = viewPager.getChildAt(0)
        (child as? RecyclerView)?.overScrollMode = View.OVER_SCROLL_NEVER
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
        tv_allReceiveOrder.setOnClickListener {
            val intent = Intent(requireActivity(), AllReceiveOrderActivity::class.java)
            intent.putExtra("receiveOrderList", receiveOrderList)
            startActivity(intent)
        }
        circleAvatar.setOnClickListener {
            browseUserPageInfo(curUser?.phoneNum, curUser?.nickName)
        }
    }

    private fun createIndicator(size: Int) {
        indicate.removeAllViews()
        var view: View
        var count: Int = size
        if (size > 4) {
            count = 4
        }
        for (i in 0 until count) {
            //创建底部指示器(小圆点)
            view = View(requireContext())
            view.setBackgroundResource(R.drawable.indicator)
            view.isEnabled = false
            //设置宽高
            val layoutParams = LinearLayout.LayoutParams(15, 15)
            //设置间隔
            if (i != 0) {
                layoutParams.leftMargin = 30
            }
            //添加到LinearLayout
            indicate.addView(view, layoutParams)
        }
    }

    private fun initObserver() {
        orderModelView.responseOrderEntity.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            refreshBadgeData(it)
            filterOrderClassify(it.bestNewOrders)
            createIndicator(filterReceiveOrderList.size)
            indicate.getChildAt(mCurrentItem).isEnabled = true
            hintPlaceHolder.visibility = View.GONE
            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    indicate.getChildAt(mCurrentItem).isEnabled = false
                    indicate.getChildAt(position).isEnabled = true
                    mCurrentItem = position
                }
            })
            val adapter = OrderPagerAdapter(requireContext(), this, R.layout.item_order_info_messages)
            adapter.setAdapter(filterReceiveOrderList)
            viewPager.adapter = adapter
            receiveOrderList.clear()
            receiveOrderList.addAll(it.bestNewOrders)
            // 刷新成功关闭refresh
            refreshLayout.finishRefresh()
        })
    }

    private fun filterOrderClassify(bestNewOrders: ArrayList<BestNewOrder>) {
        filterReceiveOrderList.clear()
        for (bestNewOrderEntity in bestNewOrders) {
            if (bestNewOrderEntity.stateName == "待派送" || bestNewOrderEntity.stateName == "待收货") {
                filterReceiveOrderList.add(bestNewOrderEntity)
            }
        }
    }

    private fun refreshBadgeData(responseOrderOfUser: ResponseOrderOfUser) {
        badges[0].badgeNumber = responseOrderOfUser.toBeReceiverNum
        badges[1].badgeNumber = responseOrderOfUser.toBeSendNum
        badges[2].badgeNumber = responseOrderOfUser.toBeFetchNum
        badges[3].badgeNumber = responseOrderOfUser.toBeCommentNum
    }

    private fun initBadgeTextView() {
        badges.add(
            QBadgeView(requireActivity()).bindTarget(tv_ToBeReceivedOrder) // 待接单
                .setGravityOffset(30F, 0F, true).setBadgePadding(2F, true)
                .setBadgeTextSize(9F, true)
        )
        badges.add(
            QBadgeView(requireActivity()).bindTarget(tv_ToBeSendOrder) // 待派单
                .setGravityOffset(28F, 0F, true).setBadgePadding(2F, true)
                .setBadgeTextSize(9F, true)
        )
        badges.add(
            QBadgeView(requireActivity()).bindTarget(tv_ToBeReceived) // 待收货
                .setGravityOffset(28F, 0F, true).setBadgePadding(2F, true)
                .setBadgeTextSize(9F, true)
        )
        badges.add(
            QBadgeView(requireActivity()).bindTarget(tv_ToBeComment) // 待评价
                .setGravityOffset(29F, 0F, true).setBadgePadding(2F, true)
                .setBadgeTextSize(9F, true)
        )
    }

    override fun onStart() {
        super.onStart()
        // activity跳转回来时，页面可以自动刷新
        refreshAllPageInfo()
    }

    private fun refreshAllPageInfo() {
        curUser = orderModelView.queryIsLoginUser()
        if (curUser != null) {
            // 获取个人信息（加载头像、积分）
            getUserInfo(curUser!!)
            // 开始请求实时最新用户相关的订单信息
            orderModelView.getOrderOfUserInfo(curUser?.phoneNum)
        }
    }

    private fun getUserInfo(curUser: User) {
        if (curUser.avatarPath != null) {
            Glide.with(requireActivity())
                .load(curUser.avatarPath)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .preload(60, 60)
            Glide.with(requireActivity())
                .load(curUser.avatarPath)
                .placeholder(R.drawable.ic_place_holder)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(circleAvatar)
        }
        if (curUser.nickName == null) {
            tv_nickName.text = requireActivity().resources.getString(R.string.no_nickname)
        } else {
            tv_nickName.text = curUser.nickName
        }
        disIntegral.text = curUser.integralNum.toString()
    }

    private fun initRefreshLayout() {
        val customHeader = CustomHeader(requireContext())
        // ContextCompat.getColor(requireContext(), R.color.light_grey) 获取资源颜色
        refreshLayout.setRefreshHeader(customHeader)
        refreshLayout.setHeaderHeight(60F)

        // 下拉刷新
        refreshLayout.setOnRefreshListener {
            if (NetWorkAvailableUtil.isNetworkAvailable(requireActivity())) {
                // 刷新整个list数据
                refreshAllPageInfo()
                refreshLayout.finishRefresh()
            } else {
                refreshLayout.finishRefresh(false)
                Toast.makeText(
                    context,
                    requireContext().resources.getString(R.string.network_invalid),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onClick(v: View?) {
        val intent = Intent(activity, AllOrderActivity::class.java)
        when (v?.id) {
            R.id.viewAll -> intent.putExtra("page", 0)
            R.id.tv_ToBeReceivedOrder -> intent.putExtra("page", 1)
            R.id.tv_ToBeSendOrder -> intent.putExtra("page", 2)
            R.id.tv_ToBeReceived -> intent.putExtra("page", 3)
            R.id.tv_ToBeComment -> intent.putExtra("page", 4)
        }

        startActivity(intent)
        /*requireActivity().overridePendingTransition(
            R.anim.translate_right_in,
            R.anim.translate_left_out
        )*/
    }

    override fun entryOrderDetails(bestNewOrder: BestNewOrder) {
        val intent = Intent(requireActivity(), OrderDetailsActivity::class.java)
        intent.putExtra("bestNewOrderEntity", bestNewOrder)
        startActivity(intent)
    }

    override fun startDeliverOrder(bestNewOrder: BestNewOrder) {
        promptDialog.showLoading("")
        orderModelView.deliveryOrder(bestNewOrder.oid.toString())
    }

    override fun browseUserPageInfo(phoneNum: String?, nickname: String?) {
        val intent = Intent(requireActivity(), UserHomePageActivity::class.java)
        intent.putExtra("phoneNum", phoneNum)
        intent.putExtra("nickname", nickname)
        startActivity(intent)
    }

    override fun deleteOrder(oid: String?, position: Int) {
        this.position = position
        orderModelView.deleteUserOrder(oid, true)
    }
}