package com.noah.express_send.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.content.CustomContent
import cn.jpush.im.android.api.content.ImageContent
import cn.jpush.im.android.api.content.TextContent
import cn.jpush.im.android.api.enums.ContentType
import cn.jpush.im.android.api.enums.MessageDirect
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.Message
import cn.jpush.im.api.BasicCallback
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.noah.express_send.R
import com.noah.express_send.bean.Msg
import com.noah.express_send.ui.adapter.MsgAdapter
import com.noah.express_send.ui.adapter.io.IReceiveOrderOperate
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.utils.VariableName
import com.noah.express_send.viewModle.ChatViewModel
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.item_action_bar.*
import me.leefeng.promptlibrary.PromptDialog
import java.io.File


class ChatActivity : BaseActivity(), View.OnClickListener, IReceiveOrderOperate {
    private val msgList = ArrayList<Msg>()
    private lateinit var adapter: MsgAdapter
    private var targetUser: String? = null
    private lateinit var conversation: Conversation
    private var receiverBitmap: Bitmap? = null
    private var myselfBitmap: Bitmap? = null
    private var oid: String? = ""
    private var nickname: String? = ""
    private val chatViewModel by lazy {
        ViewModelProvider(this).get(ChatViewModel::class.java)
    }

    private val promptDialog by lazy {
        PromptDialog(this)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_chat
    }

    override fun isSetSoftInputMode() = false

    override fun initView() {
        // ???????????????????????????statusBar?????????????????????????????????????????????EditText??????????????????
        setStatusBar(this, ContextCompat.getColor(this, R.color.blue_364))
        JMessageClient.registerEventReceiver(this)
        targetUser = intent.getStringExtra("targetUser")
        nickname = intent.getStringExtra("nickname")
        oid = intent.getStringExtra("oid")
        if (nickname == null || nickname.equals("")) {
            actionBar_title.text = resources.getString(R.string.no_nickname)
        } else {
            actionBar_title.text = nickname
        }
        val layoutManager = LinearLayoutManager(this)
        chatRecycleView.layoutManager = layoutManager
        if (!::adapter.isInitialized) {
            adapter = MsgAdapter(this, this, this)
        }
        chatRecycleView.adapter = adapter
        receiverBitmap = intent.getParcelableExtra("receiverBitmap")
        myselfBitmap = intent.getParcelableExtra("myselfBitmap")
        createLocalConversation()
        sendMessage.setOnClickListener(this)
        back.setOnClickListener(this)
        iv_album.setOnClickListener(this)
        editText.setOnClickListener(this)
    }

    private fun createLocalConversation() {
        // JMessageClient.deleteSingleConversation(targetUser) //?????????targetUser?????????
        // conversation.getUnReadMsgCnt() //?????????????????????????????????
        // JMessageClient.getAllUnReadMsgCount() // ????????????????????????????????????

        //conversation = JMessageClient.getSingleConversation(targetUser)
        // ????????????????????????????????????????????????????????????????????????????????????Conversation?????????
        conversation = Conversation.createSingleConversation(targetUser)
    }

    override fun initData() {
        getMessageHistory()
        // mode???0???????????????Activity, ??????????????????????????????????????????
        val mode = intent.getIntExtra("mode", 0)
        val phoneNum = chatViewModel.queryIsLoginUser()?.phoneNum
        if (mode == 0) {
            sendCustomMessage("request", "??????????????????", phoneNum)
        }
        chatViewModel.isReceiveOrderSuccess.observe(this, {
            sendCustomMessage("hint", "???????????????????????????????????????", phoneNum)
        })
        chatViewModel.isRefuseOrderSuccess.observe(this, {
            sendCustomMessage("hint", "????????????????????????????????????", phoneNum)
        })
    }

    private fun sendCustomMessage(key: String, content: String, phoneNum: String?) {
        val customContent = CustomContent()
        customContent.setStringValue(key, content)
        customContent.setStringValue("phoneNum", phoneNum)
        customContent.setStringValue("oid", oid)
        val message = conversation.createSendMessage(customContent)
        sendMessage(message, Msg.ORDER_REQUEST_SENT)
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
                    if (bean.contentType == ContentType.text) {
                        msgList.add(Msg(bean, Msg.TEXT_SENT, myselfBitmap))
                    } else if (bean.contentType == ContentType.image) {
                        msgList.add(Msg(bean, Msg.IMG_SENT, myselfBitmap))
                    }
                } else {
                    if (bean.contentType == ContentType.text) {

                        msgList.add(Msg(bean, Msg.TEXT_RECEIVED, receiverBitmap))
                    } else if (bean.contentType == ContentType.image) {
                        msgList.add(Msg(bean, Msg.IMG_RECEIVED, receiverBitmap))
                    } else {
                        val customContent = bean.content as CustomContent
                        if (customContent.getStringValue("request") != null &&
                            customContent.getStringValue("phoneNum")
                                .equals(chatViewModel.queryIsLoginUser()?.phoneNum)) {
                            msgList.add(Msg(bean, Msg.ORDER_REQUEST_SENT, myselfBitmap))
                        } else if (customContent.getStringValue("hint") != null &&
                            !customContent.getStringValue("phoneNum")
                                .equals(chatViewModel.queryIsLoginUser()?.phoneNum)
                        ) {
                            msgList.add(Msg(bean, Msg.ORDER_REQUEST_SENT, myselfBitmap))
                        } else if (customContent.getStringValue("request") != null &&
                            !customContent.getStringValue("phoneNum")
                                .equals(chatViewModel.queryIsLoginUser()?.phoneNum)
                        ) {
                            msgList.add(Msg(bean, Msg.ORDER_REQUEST_RECEIVED, receiverBitmap))
                        }
                    }
                }
            }
            adapter.addListMsg(msgList)
        }
        // ?????????recycleView???scrollToPosition??????smoothScrollToPosition????????????
        // ??????recycleView?????????????????????????????????????????????????????????????????????????????????view?????????????????????
        chatRecycleView.viewTreeObserver.addOnGlobalLayoutListener {
            if (msgList.size == 0) return@addOnGlobalLayoutListener
            chatRecycleView.smoothScrollToPosition(msgList.size - 1)
        }
    }

    private fun sendMessage(message: Message, messageType: Int) {
        JMessageClient.sendMessage(message)
        if (message.contentType != ContentType.custom ||
            (message.contentType == ContentType.custom &&
                    (message.content as CustomContent).getStringValue("request") != null)) {
            val msg = Msg(message, messageType, myselfBitmap)
            msgList.add(msg)
            adapter.addOneMsg(msg)
        }
        chatRecycleView.scrollToPosition(msgList.size - 1)
        message.setOnSendCompleteCallback(object : BasicCallback() {
            override fun gotResult(p0: Int, p1: String?) {
                if (p0 != 0) return
            }
        })
    }

    override fun onClick(v: View?) {
        when (v) {
            sendMessage -> {
                val content = editText.text.toString()
                if (content.isNotEmpty()) {
                    sendMessage(conversation.createSendMessage(TextContent(content)), Msg.TEXT_SENT)
                    editText.text.clear()
                }
            }
            back -> {
                onBackPressed()
            }
            iv_album -> {
                PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofAll())
                    .maxSelectNum(1)
                    .minSelectNum(1)
                    .selectionMode(PictureConfig.SINGLE)
                    .previewImage(true)
                    .compress(true)
                    .forResult(VariableName.REQUEST_CODE_ONE)
            }
            editText -> {
                chatRecycleView.scrollToPosition(msgList.size - 1)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                VariableName.REQUEST_CODE_ONE -> {
                    val selectList: MutableList<LocalMedia> =
                        PictureSelector.obtainMultipleResult(data)
                    if (PictureMimeType.isPictureType(selectList[0].pictureType) == PictureConfig.TYPE_IMAGE) {
                        ImageContent.createImageContentAsync(File(selectList[0].path),
                            object : ImageContent.CreateImageContentCallback() {
                                override fun gotResult(
                                    responseCode: Int,
                                    responseMessage: String?,
                                    imageContent: ImageContent?
                                ) {
                                    if (responseCode == 0) {
                                        imageContent?.setStringExtra(
                                            VariableName.TYPE,
                                            VariableName.IMG
                                        )
                                        val msg = conversation.createSendMessage(imageContent)
                                        sendMessage(msg, Msg.IMG_SENT)
                                    }
                                }
                            })
                    }
                }
            }
        }
    }

    /**
     * ??????????????????
     */
    fun onEventMainThread(event: MessageEvent) {
        val messageType: Int
        when (event.message.content.contentType) {
            ContentType.image -> {
                messageType = Msg.IMG_RECEIVED
            }
            ContentType.text -> {
                messageType = Msg.TEXT_RECEIVED
            }
            else -> { // custom
                val customContent = event.message.content as CustomContent
                if (customContent.getStringValue("request") != null) {
                    messageType = Msg.ORDER_REQUEST_RECEIVED
                } else if (customContent.getStringValue("hint") != null
                    && !customContent.getStringValue("phoneNum")
                        .equals(chatViewModel.queryIsLoginUser()?.phoneNum)
                ) {
                    messageType = Msg.ORDER_REQUEST_SENT
                } else {
                    messageType = Msg.OTHER
                }
            }
        }
        val msg = Msg(event.message, messageType, receiverBitmap)
        msgList.add(msg)
        adapter.addOneMsg(msg)
        chatRecycleView.scrollToPosition(msgList.size - 1)
    }

    /**
     * ????????????
     */

    override fun onDestroy() {
        super.onDestroy()
        JMessageClient.unRegisterEventReceiver(this)
    }

    override fun refuseRequest(oid: String?) {
        chatViewModel.refuseOrder(oid)
    }

    override fun agreeRequest(oid: String?) {
        promptDialog.showSuccess("????????????")
        chatViewModel.receiveOrder(oid, targetUser)
    }

    override fun clickAvatar() {
        val intent = Intent(this, UserHomePageActivity::class.java)
        intent.putExtra("phoneNum", targetUser)
        intent.putExtra("nickname", nickname)
        startActivity(intent)
    }
}