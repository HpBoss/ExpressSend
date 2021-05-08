package com.noah.express_send.ui.fragment

import android.content.Intent
import android.graphics.Bitmap
import androidx.lifecycle.ViewModelProvider
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.UserInfo
import com.noah.express_send.R
import com.noah.express_send.ui.activity.ChatActivity
import com.noah.express_send.ui.adapter.MessageAdapter
import com.noah.express_send.ui.adapter.io.IClickMessageItem
import com.noah.express_send.ui.base.BaseFragment
import com.noah.express_send.ui.view.CustomHeader
import com.noah.express_send.utils.NetWorkAvailableUtil
import com.noah.express_send.viewModle.MessageViewModel
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.fragment_message.refreshLayout

class MessageFragment : BaseFragment(), IClickMessageItem{
    private lateinit var adapter: MessageAdapter
    private lateinit var conversationList: MutableList<Conversation>
    private val messageViewModel by lazy {
        ViewModelProvider(this).get(MessageViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_message
    }

    override fun initView() {
        JMessageClient.registerEventReceiver(this)
        // 初始化Adapter
        val layoutManager = LinearLayoutManager(requireActivity())
        messageRecycleView.layoutManager = layoutManager
        adapter = MessageAdapter(requireContext(), this)
        messageRecycleView.adapter = adapter
        initRefreshLayout()
        refreshConversation()
    }

    private fun refreshConversation() {
        // 获取本地会话列表, 添加到adapter中
//        JMessageClient.deleteSingleConversation("19980492664")
        conversationList = JMessageClient.getConversationList() ?: return
        adapter.setAdapter(conversationList)
    }

    override fun initData() {}

    private fun initRefreshLayout() {
        refreshLayout.setRefreshHeader(CustomHeader(requireContext()))
        refreshLayout.setHeaderHeight(60F)

        // 下拉刷新
        refreshLayout.setOnRefreshListener {
            if (NetWorkAvailableUtil.isNetworkAvailable(requireActivity())) {
                refreshConversation()
                refreshLayout.finishRefresh()
            } else {
                refreshLayout.finishRefresh(false)
                Toast.makeText(context, R.string.network_invalid, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun setOnClickMessageItem(position: Int, bitmap: Bitmap?, targetId: String?, nickname: String?) {
        val intent = Intent(requireActivity(), ChatActivity::class.java)
        // 获取点击的会话item的phoneNum
        intent.putExtra("targetUser", targetId)
        intent.putExtra("receiverBitmap", bitmap)
        intent.putExtra("mode", 1)
        intent.putExtra("nickname", nickname)
        JMessageClient.getMyInfo().getAvatarBitmap(object : GetAvatarBitmapCallback() {
            override fun gotResult(p0: Int, p1: String?, p2: Bitmap?) {
                if (p0 == 0) {
                    intent.putExtra("myselfBitmap", p2)
                    startActivity(intent)
                    /*requireActivity().overridePendingTransition(R.anim.translate_right_in,
                        R.anim.translate_left_out)*/
                }
            }
        })
    }

    /**
     * 接收在线消息
     */
    fun onEventMainThread(event: MessageEvent) {
        val msg = event.message
        val userInfo = msg.targetInfo as UserInfo
        for (conversation in conversationList) {
            val userI = conversation.targetInfo as UserInfo
            if (userI.userName.equals(userInfo.userName)) {
                conversationList = JMessageClient.getConversationList() ?: return
                conversation.updateConversationExtra("new_message")
                conversationList.indexOf(conversation)
                adapter.setAdapter(conversationList)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        adapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        JMessageClient.unRegisterEventReceiver(this)
    }

}