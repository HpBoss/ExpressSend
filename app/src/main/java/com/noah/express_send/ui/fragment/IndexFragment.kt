package com.noah.express_send.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.model.UserInfo
import com.google.android.material.chip.Chip
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
import com.noah.internet.request.RequestFilterOrder
import com.noah.internet.response.ResponseExpress
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.zhpan.bannerview.BannerViewPager
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.fragment_index.*
import kotlinx.android.synthetic.main.item_express_filter.*
import kotlinx.android.synthetic.main.layout_screen.*
import me.leefeng.promptlibrary.PromptButton
import me.leefeng.promptlibrary.PromptDialog
import java.util.*
import kotlin.collections.ArrayList


class IndexFragment : BaseFragment(), IOrderInfo, View.OnClickListener {
    private lateinit var mViewPager: BannerViewPager<Int>
    private lateinit var adapter: IndexFragmentAdapter
    private lateinit var promptDialog: PromptDialog
    private var getAllExpressList = ArrayList<ResponseExpress>()
    private lateinit var requestFilterOrder: RequestFilterOrder
    private var mixHeight: Int = 0
    private var maxHeight: Int = 0
    private var filterMode = 0
    private val indexViewModel by lazy {
        ViewModelProvider(this).get(IndexViewModel::class.java)
    }
    companion object{
        const val FILTER_MODE_EXPRESS = 0
        const val FILTER_MODE_MULTIPLE = 1
    }
    private var position: Int = 0
    private var curPage = 1

    override fun getLayoutId() = R.layout.fragment_index

    override fun initView() {
        setupViewPager()
        initRecycleView()
        initRefreshLayout()
        initClassification()
    }

    override fun initData() {
        setInVisibilityFilter()
        filterParent.visibility = View.GONE
        maxHeight = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            180f, resources.displayMetrics
        ).toInt()
        btnFloating.setOnClickListener(this)
        express_filter.setOnClickListener(this)
        mask.setOnClickListener(this)
        btn_reset.setOnClickListener(this)
        btn_complete.setOnClickListener(this)
        multiple_sort.setOnClickListener(this)
    }

    private fun setInVisibilityFilter() {
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_display_up)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        express_filter.setCompoundDrawables(null, null, drawable, null)
        foldAnimator(maxHeight, mixHeight)
        mask.visibility = View.GONE
    }

    private fun setVisibilityFilter() {
        chip_group.clearCheck()
        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_display_down)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        express_filter.setCompoundDrawables(null, null, drawable, null)
        foldAnimator(mixHeight, maxHeight)
        filterParent.visibility = View.VISIBLE
        mask.visibility = View.VISIBLE
    }

    private fun foldAnimator(startHeight: Int, endHeight: Int) {
        val va = ValueAnimator.ofInt(startHeight, endHeight)
        va.interpolator = FastOutLinearInInterpolator()
        va.addUpdateListener { valueAnimator ->
            filterContent.layoutParams.height = valueAnimator.animatedValue as Int
            filterContent.requestLayout()
        }
        va.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator, isReverse: Boolean) {
                if (startHeight > endHeight) filterParent.visibility = View.GONE
            }
        })
        va.duration = 300
        va.start()
    }

    private fun initClassification() {
        indexViewModel.dataList.observe(viewLifecycleOwner, {
            if (it.size == 0 && filterMode == FILTER_MODE_EXPRESS) {
                Toast.makeText(requireContext(), "无数据", Toast.LENGTH_SHORT).show()
            }
            adapter.setAdapter(it)
        })
        indexViewModel.isReceiveOrderSuccess.observe(viewLifecycleOwner, {
            // promptDialog.showSuccess("接单成功")
            // 向被接单用户发送消息,先带着phoneNum打开ChatActivity
            openChatRoom(false)
        })
        indexViewModel.expressList.observe(this, {
            if (it.size == 0) return@observe
            addChipToChipGroup(it)
            getAllExpressList.addAll(it)
        })
        indexViewModel.getAllExpressName()
    }

    private fun addChipToChipGroup(expressList: ArrayList<ResponseExpress>) {
        chip_group.removeAllViews()
        var count = 0
        for (express in expressList) {
            val chip = layoutInflater.inflate(R.layout.item_chip, chip_group, false) as Chip
            chip.text = express.expressName
            chip.id = count
            count += 1
            chip_group.addView(chip)
        }
    }

    private fun initRefreshLayout() {
        refreshLayout.setRefreshHeader(CustomHeader(requireContext()))
        refreshLayout.setHeaderHeight(60F)
        refreshLayout.setRefreshFooter(ClassicsFooter(requireContext()))

        // 下拉刷新
        refreshLayout.setOnRefreshListener {
            if (NetWorkAvailableUtil.isNetworkAvailable(requireActivity())) {
                val user = indexViewModel.queryIsLoginUser() ?: return@setOnRefreshListener
                // 刷新整个list数据
                curPage = 1
                adapter.clearAdapterList()
                if (filterMode == FILTER_MODE_EXPRESS) {
                    indexViewModel.getAllFilterOrder(requestFilterOrder)
                } else if (filterMode == FILTER_MODE_MULTIPLE) {
                    indexViewModel.getPageOrderNoCur(curPage, user.phoneNum)
                }
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
        // 底部上滑加载更多
        refreshLayout.setOnLoadMoreListener {
            if (NetWorkAvailableUtil.isNetworkAvailable(requireActivity())) {
                val user = indexViewModel.queryIsLoginUser() ?: return@setOnLoadMoreListener
                // 添加新数据到list中, 并通知到界面
                curPage++
                if (filterMode == FILTER_MODE_EXPRESS) {
                    requestFilterOrder.page = curPage
                    indexViewModel.getAllFilterOrder(requestFilterOrder)
                } else if (filterMode == FILTER_MODE_MULTIPLE) {
                    indexViewModel.getPageOrderNoCur(curPage, user.phoneNum)
                }
                refreshLayout.finishLoadMore() // 获取到正确数据就调用此段代码
            } else {
                refreshLayout.finishLoadMore(false)
                Toast.makeText(
                    context,
                    requireContext().resources.getString(R.string.network_invalid),
                    Toast.LENGTH_SHORT
                ).show()
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
                val user = indexViewModel.queryIsLoginUser() ?: return@PromptButton
                indexViewModel.receiveOrder(id.toString(), user.phoneNum)
            },
        )
    }

    override fun onClick(v: View?) {
        when (v) {
            btnFloating -> {
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
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.integral_not_enough),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            express_filter -> {
                if (filterParent.visibility == View.VISIBLE) {
                    setInVisibilityFilter()
                } else {
                    setVisibilityFilter()
                }
            }
            mask -> {
                setInVisibilityFilter()
            }
            btn_reset -> {
                chip_group.clearCheck()
            }
            btn_complete -> {
                val checkedChipIds = chip_group.checkedChipIds
                if (checkedChipIds.size == 0) return
                val expressEntities = ArrayList<ResponseExpress>()
                for (checkedChipId in checkedChipIds) {
                    expressEntities.add(getAllExpressList[checkedChipId])
                }
                curPage = 1
                requestFilterOrder = RequestFilterOrder(
                    curPage,
                    indexViewModel.queryIsLoginUser()?.phoneNum,
                    expressEntities
                )
                indexViewModel.getAllFilterOrder(requestFilterOrder)
                filterMode = FILTER_MODE_EXPRESS
                multiple_sort.setTextColor(Color.GRAY)
                express_filter.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                setInVisibilityFilter()
            }
            multiple_sort -> {
                if (filterMode == FILTER_MODE_MULTIPLE) return
                curPage = 1
                if (filterParent.visibility == View.VISIBLE) setInVisibilityFilter()
                indexViewModel.getPageOrderNoCur(
                    curPage,
                    indexViewModel.queryIsLoginUser()?.phoneNum
                )
                multiple_sort.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                express_filter.setTextColor(Color.GRAY)
            }
        }
    }

}