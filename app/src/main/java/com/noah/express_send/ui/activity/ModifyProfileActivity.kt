package com.noah.express_send.ui.activity

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.api.BasicCallback
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.express_send.R
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.viewModle.ModifyProfileViewModel
import com.noah.internet.request.RequestUser
import kotlinx.android.synthetic.main.activity_modify_profile.*

class ModifyProfileActivity : BaseActivity(), TextWatcher, View.OnClickListener {
    private val modifyProfileViewModel: ModifyProfileViewModel by viewModels<ModifyProfileViewModel>()
    private var columnName: String? = null
    private var title: String? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_modify_profile
    }

    override fun isSetSoftInputMode() = true

    override fun initView() {
        immersionBar {
            statusBarColor(R.color.blue_364)
            fitsSystemWindows(true)
        }
        title = intent.getStringExtra("title")
        val oldValue = intent.getStringExtra("oldValue")
        columnName = intent.getStringExtra("column")
        val inputType = intent.getIntExtra("type", InputType.TYPE_CLASS_TEXT)
        modifyTitle.text = title
        editText.inputType = inputType
        if (oldValue != null) {
            editText.text.clear()
            editText.text.insert(0, oldValue)
            modifyProfileViewModel.reduceAny(oldValue.length)
        }
        editText.addTextChangedListener(this)

        editText.postDelayed({
            editText.requestFocus()
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, 0)
        }, 200)

        modifyProfileViewModel.num.observe(this, {
            tvSurplus.text = it.toString()
        })
        tvCancel.setOnClickListener(this)
        tvCompleted.setOnClickListener(this)
        btnCleanUp.setOnClickListener(this)
    }

    override fun initData() {

    }

    private var oldLen = 0
    private var nowLen = 0
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        oldLen = count
    }

    override fun afterTextChanged(s: Editable?) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s.toString().length > 30) {
            val str = s.toString()
            editText.text.delete(0, s.toString().length)
            editText.text.insert(0, str.substring(0, 30))
            editText.setSelection(30)
            return
        }
        nowLen = count
        if (nowLen > oldLen) {
            modifyProfileViewModel.reduceAny(nowLen - oldLen)
        } else if (nowLen < oldLen) {
            modifyProfileViewModel.addOne()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tvCancel -> onBackPressed()
            R.id.tvCompleted -> {
                // 先保存早本地Room数据库中，再提交数据
                val user = modifyProfileViewModel.queryIsLoginUser()
                if (user != null && editText.text.toString() != "") {
                    when (columnName) {
                        "nick_name" -> user.nickName = editText.text.toString()
                        "school_name" -> user.schoolName = editText.text.toString()
                        "email" -> user.email = editText.text.toString()
                        "dormitory" -> user.dormitory = editText.text.toString()
                        "room_number" -> user.roomNumber = editText.text.toString()
                    }
                    modifyProfileViewModel.updateUserProfile(
                        user.phoneNum, RequestUser(
                            user.nickName,
                            user.schoolName,
                            user.email,
                            user.dormitory,
                            user.roomNumber
                        )
                    )
                    modifyProfileViewModel.isUpdateSuccess.observe(this, Observer {
                        if (it == true) {
                            modifyProfileViewModel.updateUser(user)
                            if (columnName == "nick_name") {
                                val myInfo = JMessageClient.getMyInfo()
                                myInfo.nickname = user.nickName
                                JMessageClient.updateMyInfo(UserInfo.Field.nickname,
                                    myInfo, object : BasicCallback() {
                                        override fun gotResult(p0: Int, p1: String?) {
                                            if (p0 == 0) {
                                                Log.d("JMessageClient", "用户名更新成功")
                                            }
                                        }
                                    })
                            }
                            Toast.makeText(this, "修改" + title + "成功！", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                        } else {
                            Toast.makeText(this, "修改" + title + "失败！", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "修改" + title + "失败！", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btnCleanUp -> {
                editText.text.clear()
                editText.requestFocus()
                modifyProfileViewModel.cleanUp()
            }
        }
    }
}