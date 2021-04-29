package com.noah.express_send.ui.activity

import android.graphics.Bitmap
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.enums.MessageDirect
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.api.BasicCallback
import com.noah.express_send.R
import com.noah.express_send.bean.Msg
import com.noah.express_send.ui.adapter.MsgAdapter
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.viewModle.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : BaseActivity(), View.OnClickListener {
    private val msgList = ArrayList<Msg>()
    private lateinit var adapter: MsgAdapter
    private var targetUser: String? = null
    private lateinit var conversation: Conversation
    private var receiverBitmap: Bitmap? = null
    private var myselfBitmap: Bitmap? = null
    private val chatViewModel by lazy {
        ViewModelProvider(this).get(ChatViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_chat
    }

    override fun isSetSoftInputMode() = false

    override fun initView() {
        // 使用这样的方式设置statusBar颜色，并且保证吊起键盘时，底部EditText也可以被顶起
        setStatusBar(this, ContextCompat.getColor(this, R.color.blue_364))
        JMessageClient.registerEventReceiver(this)
        targetUser = intent.getStringExtra("targetUser")
        val nickname = intent.getStringExtra("nickname")
        if (nickname == null || nickname.isEmpty()) {
            receiverNickName.text = resources.getString(R.string.no_nickname)
        } else {
            receiverNickName.text = nickname
        }
        val layoutManager = LinearLayoutManager(this)
        chatRecycleView.layoutManager = layoutManager
        if (!::adapter.isInitialized) {
            adapter = MsgAdapter(this)
        }
        chatRecycleView.adapter = adapter
        receiverBitmap = intent.getParcelableExtra("receiverBitmap")
        myselfBitmap = intent.getParcelableExtra("myselfBitmap")
        createLocalConversation()
        sendMessage.setOnClickListener(this)
        backToMain.setOnClickListener(this)
    }

    private fun createLocalConversation() {
        // JMessageClient.deleteSingleConversation(targetUser) //删除与targetUser的会话
        // conversation.getUnReadMsgCnt() //获取单个会话未读消息数
        // JMessageClient.getAllUnReadMsgCount() // 获取所有会话未读消息总数

        //conversation = JMessageClient.getSingleConversation(targetUser)
        // 当直接卸载应用，且未退出时，再次登录应用，无法进行下面的Conversation的创建
        conversation = Conversation.createSingleConversation(targetUser)
    }

    override fun initData() {
        getMessageHistory()
        // mode为0，则开启的Activity, 需要自动先向对方发送一个消息
        val mode = intent.getIntExtra("mode", 0)
        if (mode == 0) {
            sendMessage(this.getString(R.string.message_hints))
        }
    }

    override fun onResume() {
        super.onResume()
        JMessageClient.enterSingleConversation(targetUser)
    }

    override fun onPause() {
        super.onPause()
        JMessageClient.exitConversation()
    }

    private fun getMessageHistory() {
        if (conversation.allMessage != null) {
            for (bean in conversation.allMessage!!.toMutableList()) {
                if (bean.direct == MessageDirect.send) {
                    msgList.add(
                        Msg(
                            (bean.content as TextContent).text,
                            Msg.TYPE_SENT,
                            myselfBitmap
                        )
                    )
                } else {
                    msgList.add(
                        Msg(
                            (bean.content as TextContent).text,
                            Msg.TYPE_RECEIVED,
                            receiverBitmap
                        )
                    )
                }
            }
            adapter.addListMsg(msgList)
        }
        chatRecycleView.scrollToPosition(msgList.size - 1)
    }

    private fun sendMessage(msgStr: String) {
        val message = conversation.createSendMessage(TextContent(msgStr))
        message?.setOnSendCompleteCallback(object : BasicCallback() {
            override fun gotResult(p0: Int, p1: String?) {
                if (p0 != 0) return
                val msg = Msg(msgStr, Msg.TYPE_SENT, myselfBitmap)
                msgList.add(msg)
                adapter.addOneMsg(msg)
                chatRecycleView.scrollToPosition(msgList.size - 1)
            }
        })
        JMessageClient.sendMessage(message)
    }

    override fun onClick(v: View?) {
        when (v) {
            sendMessage -> {
                val content = editText.text.toString()
                if (content.isNotEmpty()) {
                    sendMessage(content)
                    editText.text.clear()
                }
            }
            backToMain -> {
                onBackPressed()
            }
        }
    }

    /**
     * 接收在线消息
     */
    fun onEventMainThread(event: MessageEvent) {
        val msg = Msg(
            (event.message.content as TextContent).text,
            Msg.TYPE_RECEIVED,
            receiverBitmap
        )
        msgList.add(msg)
        adapter.addOneMsg(msg)
        chatRecycleView.scrollToPosition(msgList.size - 1)
    }

    /**
     * 离线消息
     */

    override fun onDestroy() {
        super.onDestroy()
        JMessageClient.unRegisterEventReceiver(this)
    }
}