package com.noah.express_send.utils


import android.content.Context
import android.content.Intent
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.event.NotificationClickEvent
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import com.noah.express_send.ui.activity.ReleaseActivity

class NotificationClickEventReceiver(var mContext: Context?) {

    init {
        //注册接收消息事件
        JMessageClient.registerEventReceiver(this)
    }

    /**
     * 收到消息处理
     * @param notificationClickEvent 通知点击事件
     */
    fun onEvent(notificationClickEvent: NotificationClickEvent?) {
        if(mContext == null){
            return
        }

        if (null == notificationClickEvent) {
            return
        }
        val msg = notificationClickEvent.message
        if (msg != null) {
            val targetId = msg.targetID
            val appKey = msg.fromAppKey
            val type = msg.targetType

            val notificationIntent = Intent(mContext, ReleaseActivity::class.java)
            if (type == ConversationType.single) {
                notificationIntent.putExtra(VariableName.TYPE,VariableName.SINGLE)
                notificationIntent.putExtra(VariableName.DATA, (msg.targetInfo as UserInfo).userName)
                notificationIntent.putExtra(VariableName.DATA_TWO,  (msg.targetInfo as UserInfo).userName)
                notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                mContext?.startActivity(notificationIntent)

            } else if(type== ConversationType.group) {
                notificationIntent.putExtra(VariableName.TYPE,VariableName.GROUP)
                notificationIntent.putExtra(VariableName.DATA, (msg.targetInfo as GroupInfo).groupID)
                notificationIntent.putExtra(VariableName.DATA_TWO,(msg.targetInfo as GroupInfo).groupName)
                notificationIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                mContext?.startActivity(notificationIntent)

            }
        }
    }

}
