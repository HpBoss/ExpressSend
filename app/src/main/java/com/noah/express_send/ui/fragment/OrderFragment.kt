package com.noah.express_send.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.noah.database.User
import com.noah.express_send.R
import com.noah.express_send.ui.activity.AllOrderActivity
import com.noah.express_send.ui.view.CustomHeader
import com.noah.express_send.utils.NetWorkAvailableUtil
import com.noah.express_send.viewModle.OrderModelView
import com.noah.internet.response.ResponseOrderOfUser
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.fragment_order.view.*
import kotlinx.android.synthetic.main.item_order_info_message.*
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

class OrderFragment : Fragment(), View.OnClickListener {
    private lateinit var oid: String
    private var curUser: User? = null
    private val badges by lazy {
        ArrayList<Badge>()
    }

    private val orderModelView by lazy {
        ViewModelProvider(this).get(OrderModelView::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewAll.setOnClickListener(this)
        tv_ToBeSendOrder.setOnClickListener(this)
        tv_ToBeComment.setOnClickListener(this)
        tv_ToBeReceived.setOnClickListener(this)
        tv_ToBeReceivedOrder.setOnClickListener(this)
        initObserver()
        initRefreshLayout()
        initBadgeTextView()
    }

    private fun initObserver() {
        orderModelView.responseOrderEntity.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            refreshBadgeData(it)
            btn_changeOrderState.visibility = View.INVISIBLE
            btn_funcOperate.visibility = View.INVISIBLE
            if (it.bestNewOrderEntity == null) {
                hintPlaceHolder.visibility = View.VISIBLE
                bestNewOrder.visibility = View.GONE
            } else {
                hintPlaceHolder.visibility = View.GONE
                bestNewOrder.visibility = View.VISIBLE
                tv_orderState.text = it.bestNewOrderEntity!!.stateName
                tv_express.text = it.bestNewOrderEntity!!.express
                tv_address.text = it.bestNewOrderEntity!!.detailAddress
                tv_nickNameRight.text = it.bestNewOrderEntity!!.nickName
                tv_weights.text = it.bestNewOrderEntity!!.weight
                tv_typeName.text = it.bestNewOrderEntity!!.typeName
                tv_payIntegralNum.text =
                    getString(R.string.payIntegralNum, it.bestNewOrderEntity!!.payIntegralNum)
                dormitory.text = it.bestNewOrderEntity!!.dormitory
                oid = it.bestNewOrderEntity!!.oid.toString()
                tv_operateTime.visibility = View.VISIBLE
                tv_operateTime.text = it.bestNewOrderEntity!!.operateTime

                Glide.with(requireActivity()).load(it.bestNewOrderEntity!!.avatarUrl)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .preload()
                Glide.with(requireActivity()).load(it.bestNewOrderEntity!!.avatarUrl)
                    .placeholder(R.drawable.ic_place_holder)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(avatar)
                when(it.bestNewOrderEntity!!.stateName) {
                    getString(R.string.toBeReceive) ->{
                        if (curUser?.phoneNum == it.bestNewOrderEntity!!.phoneNum) {
                            tv_orderState.text = getString(R.string.sending)
                        } else {
                            tv_orderState.text = getString(R.string.toBeSend)
                        }
                    }
                }
            }
            // 刷新成功关闭refresh
            refreshLayout.finishRefresh()
        })
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
                Toast.makeText(context, requireContext().resources.getString(R.string.network_invalid), Toast.LENGTH_SHORT).show()
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
}