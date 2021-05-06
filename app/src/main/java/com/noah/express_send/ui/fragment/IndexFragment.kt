package com.noah.express_send.ui.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.model.UserInfo
import com.noah.express_send.R
import com.noah.express_send.ui.activity.ChatActivity
import com.noah.express_send.ui.activity.ReleaseActivity
import com.noah.express_send.ui.adapter.IndexFragmentAdapter
import com.noah.express_send.ui.adapter.SimpleAdapter
import com.noah.express_send.ui.adapter.io.IOrderInfo
import com.noah.express_send.ui.base.BaseFragment
import com.noah.express_send.ui.view.CustomHeader
import com.noah.express_send.utils.NetWorkAvailableUtil
import com.noah.express_send.viewModle.IndexViewModel
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.zhpan.bannerview.BannerViewPager
import kotlinx.android.synthetic.main.fragment_index.*
import kotlinx.android.synthetic.main.layout_screen.*
import me.leefeng.promptlibrary.PromptButton
import me.leefeng.promptlibrary.PromptDialog
import java.util.*

class IndexFragment : BaseFragment(), IOrderInfo{
    private lateinit var mViewPager: BannerViewPager<Int>
    private lateinit var adapter: IndexFragmentAdapter
    private lateinit var promptDialog: PromptDialog

    private val indexViewModel by lazy {
        ViewModelProvider(this).get(IndexViewModel::class.java)
    }

    private var position: Int = 0
    private var curPage = 1

    override fun getLayoutId(): Int {
        return R.layout.fragment_index
    }

    override fun initView() {
        setupViewPager()
        initRecycleView()
        initRefreshLayout()
        initClassification()
        btnFloating.setOnClickListener {
            // 判断当前用户积分是否大于0
            val user = indexViewModel.queryIsLoginUser()
            if (user?.integralNum!! > 0) {
                val bundle = Bundle()
                bundle.putInt("value", 0)
                val intent = Intent(activity, ReleaseActivity::class.java)
                startActivity(intent.putExtras(bundle))
                /*requireActivity().overridePendingTransition(
                    R.anim.translate_right_in,
                    R.anim.translate_left_out
                )*/
            } else {
                Toast.makeText(requireContext(), "当前积分不足", Toast.LENGTH_SHORT).show()
            }
        }

        loginSuccess()
    }

    override fun initData() {}

    private fun loginSuccess() {
        loginHints.visibility = View.GONE
        btnFloating.visibility = View.VISIBLE
    }

    fun notLoginIn() {
        btnFloating.visibility = View.GONE
        loginHints.visibility = View.VISIBLE
    }

    private fun initClassification() {
        indexViewModel.dataList.observe(viewLifecycleOwner, {
            adapter.setAdapter(it)
        })
        indexViewModel.isReceiveOrderSuccess.observe(viewLifecycleOwner, {
            // promptDialog.showSuccess("接单成功")
            // 向被接单用户发送消息,先带着phoneNum打开ChatActivity
            openChatRoom(false)
        })
    }

    private fun initRefreshLayout() {
        refreshLayout.setRefreshHeader(CustomHeader(requireContext()))
        refreshLayout.setHeaderHeight(60F)
        refreshLayout.setRefreshFooter(ClassicsFooter(requireContext()))

        // 下拉刷新
        refreshLayout.setOnRefreshListener {
            if (NetWorkAvailableUtil.isNetworkAvailable(requireActivity())) {
                val user = indexViewModel.queryIsLoginUser()?: return@setOnRefreshListener
                // 刷新整个list数据
                curPage = 1
                adapter.clearAdapterList()
                indexViewModel.getPageOrderNoCur(curPage, user.phoneNum)
                refreshLayout.finishRefresh()
            } else {
                refreshLayout.finishRefresh(false)
                Toast.makeText(context,
                    requireContext().resources.getString(R.string.network_invalid), Toast.LENGTH_SHORT).show()
            }
        }
        // 底部上滑加载更多
        refreshLayout.setOnLoadMoreListener {
            if (NetWorkAvailableUtil.isNetworkAvailable(requireActivity())) {
                val user = indexViewModel.queryIsLoginUser()?: return@setOnLoadMoreListener
                // 添加新数据到list中, 并通知到界面
                curPage++
                indexViewModel.getPageOrderNoCur(curPage, user.phoneNum)
                refreshLayout.finishLoadMore() // 获取到正确数据就调用此段代码
            } else {
                refreshLayout.finishLoadMore(false)
                Toast.makeText(context,
                    requireContext().resources.getString(R.string.network_invalid), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initRecycleView() {
        val layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recycleView.layoutManager = layoutManager
        adapter = IndexFragmentAdapter(requireContext(), this)
        recycleView.adapter = adapter
        recycleView.isNestedScrollingEnabled = false // 解决滑动卡顿
        val user = indexViewModel.queryIsLoginUser()
        if (user != null) indexViewModel.getPageOrderNoCur(curPage, user.phoneNum)
    }

    private fun setupViewPager() {
        val mPictureList: MutableList<Int> = ArrayList()
        for (i in 0..4) {
            val drawable = resources.getIdentifier("advertise$i", "drawable", activity?.packageName)
            mPictureList.add(drawable)
        }
        mViewPager = view?.findViewById(R.id.bannerView) as BannerViewPager<Int>
        mViewPager.apply {
            adapter = SimpleAdapter()
            setLifecycleRegistry(lifecycle)
            setInterval(5000)
        }.create(mPictureList)
    }

    private fun openChatRoom(isOnlyChat: Boolean) {
        var mode = 1
        val orderPhoneNum = adapter.getOrderPhoneNum(position)
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        if (!isOnlyChat) {
            adapter.removeAt(position)
            mode = 0
        }
        intent.putExtra("targetUser", orderPhoneNum)
        intent.putExtra("mode", mode)
        JMessageClient.getUserInfo(orderPhoneNum, object : GetUserInfoCallback() {
            override fun gotResult(p0: Int, p1: String?, p2: UserInfo?) {
                if (p0 == 0) {
                    intent.putExtra("nickname", p2?.nickname)
                    p2?.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                        override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                            if (p0 == 0) intent.putExtra("receiverBitmap", p2)
                            JMessageClient.getMyInfo().getAvatarBitmap(object :
                                GetAvatarBitmapCallback() {
                                override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                                    if (p0 == 0) intent.putExtra("myselfBitmap", p2)
                                    startActivity(intent)
                                }
                            })
                        }
                    })
                }
            }
        })
    }

    override fun setOnClickItemMore(position: Int, id: Long) {
        this.position = position
        val cancel = PromptButton(resources.getString(R.string.cancel), null)
        cancel.textColor = Color.parseColor("#0076ff")
        promptDialog = PromptDialog(requireActivity())
        promptDialog.showAlertSheet(
            "", true, cancel,
            PromptButton(
                resources.getString(R.string.contact)
            ) {
                openChatRoom(true)
            },
            PromptButton(resources.getString(R.string.receive_order)) {
                val user = indexViewModel.queryIsLoginUser()?: return@PromptButton
                indexViewModel.receiveOrder(id.toString(), user.phoneNum)
            },
        )
    }

}