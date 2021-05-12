package com.noah.express_send.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.model.UserInfo
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.express_send.R
import com.noah.express_send.ui.adapter.HistoryCommentAdapter
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.viewModle.UserHomeViewModel
import com.noah.internet.response.CommentAllInfo
import kotlinx.android.synthetic.main.activity_user_home_page.*
import kotlinx.android.synthetic.main.item_action_bar.*
import kotlinx.android.synthetic.main.item_status_bar.*

class UserHomePageActivity : BaseActivity() {
    private lateinit var adapter: HistoryCommentAdapter
    private var commentList = ArrayList<CommentAllInfo>()
    private val userHomeViewModel by lazy {
        ViewModelProvider(this).get(UserHomeViewModel::class.java)
    }

    override fun getLayoutId() = R.layout.activity_user_home_page

    override fun isSetSoftInputMode() = true

    override fun initView() {
        immersionBar {
            statusBarView(status_bar_view)
            statusBarDarkFont(false)
        }
        initRecycleView()
    }

    override fun initData() {
        val nickname = intent.getStringExtra("nickname")
        val phoneNum = intent.getStringExtra("phoneNum")
        if (userHomeViewModel.queryIsLoginUser()?.phoneNum != phoneNum) {
            button.visibility = View.VISIBLE
            button.text = getString(R.string.contact)
        }
        actionBar_title.text = getString(R.string.personal_page)
        tv_nickname.text = nickname
        tv_phoneNumber.text = phoneNum

        userHomeViewModel.pageInfo.observe(this, {
            tv_dormitory.text = it.dormitory
            tv_roomNumber.text = it.roomNumber
            tv_receiverNum.text = it.receiveNum.toString()
            tv_schoolName.text = it.schoolName

            adapter.setAdapter(it.commentList)
            if (it.commentList.size > 0) {
                placeholder_no_find.visibility = View.GONE
            } else {
                placeholder_no_find.visibility = View.VISIBLE
            }

            Glide.with(this).load(it.avatarUrl)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .preload(60, 60)
            Glide.with(this)
                .load(it.avatarUrl).placeholder(R.drawable.ic_place_holder)
                .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(tv_userAvatar)
        })
        userHomeViewModel.getUserPageInfo(phoneNum)

        back.setOnClickListener {
            onBackPressed()
        }
        // 打开聊天窗口
        button.setOnClickListener {
            contactUser(phoneNum, nickname)
        }
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        recycleView_historyComment.layoutManager = layoutManager
        adapter = HistoryCommentAdapter( this)
        recycleView_historyComment.adapter = adapter
        recycleView_historyComment.isNestedScrollingEnabled = false
    }

    private fun contactUser(phoneNum: String?, nickname: String?) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("targetUser", phoneNum)
        intent.putExtra("mode", 1)
        intent.putExtra("nickname", nickname)
        JMessageClient.getUserInfo(phoneNum, object : GetUserInfoCallback() {
            override fun gotResult(p0: Int, p1: String?, p2: UserInfo?) {
                if (p0 == 0) {
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

}