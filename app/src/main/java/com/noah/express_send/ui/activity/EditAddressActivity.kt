package com.noah.express_send.ui.activity

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.database.User
import com.noah.express_send.R
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.utils.VariableName
import com.noah.express_send.viewModle.EditAddressViewModel
import com.noah.internet.Constant
import com.noah.internet.response.ResponseAddressBook
import kotlinx.android.synthetic.main.activity_modify_profile.*
import kotlinx.android.synthetic.main.activity_release.*
import kotlinx.android.synthetic.main.item_status_bar.*
import me.leefeng.promptlibrary.PromptDialog

class EditAddressActivity : BaseActivity() {
    private var addressBook: ResponseAddressBook? = null
    private var mode: Int = VariableName.CREATE_ADDRESS
    private var loginUser: User? = null
    private val promptDialog by lazy {
        PromptDialog(this)
    }
    private val editAddressBookViewModel by lazy {
        ViewModelProvider(this).get(EditAddressViewModel::class.java)
    }

    override fun getLayoutId() = R.layout.activity_modify_profile
    override fun isSetSoftInputMode() = true

    override fun initView() {
        immersionBar {
            statusBarView(status_bar_view)
            statusBarDarkFont(false)
        }
        tvSurplus.visibility = View.GONE
        tvSchoolName.visibility = View.VISIBLE
        loginUser = editAddressBookViewModel.queryIsLoginUser()
        tvSchoolName.text = loginUser?.schoolName
        editText.hint = getString(R.string.detailNameHint)
        mode = intent.getIntExtra(getString(R.string.key_mode), VariableName.CREATE_ADDRESS)
        if (mode == VariableName.CREATE_ADDRESS) {
            modifyTitle.text =
                getString(R.string.create_address_title)
        } else {
            modifyTitle.text =
                getString(R.string.edit_address_title)
            addressBook = intent.getParcelableExtra(VariableName.ADDRESS_BOOK)
            editText.text.clear()
            editText.text.insert(0, addressBook?.addressName)
        }
        editAddressBookViewModel.isSuccessEditAddressBook.observe(this, {
            if (it) showMissionHint(getString(R.string.edit_success), Constant.CODE_SUCCESS)
            else showMissionHint(getString(R.string.edit_failure), Constant.CODE_FAILURE)
        })
        editAddressBookViewModel.isSuccessCreateAddressBook.observe(this, {
            if (it) showMissionHint(getString(R.string.create_success), Constant.CODE_SUCCESS)
            else showMissionHint(getString(R.string.create_failure), Constant.CODE_FAILURE)
        })

        // 保证进入页面时就弹出软键盘
        editText.postDelayed({
            editText.requestFocus()
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, 0)
        }, 200)
    }

    private fun showMissionHint(msg: String, resultCode: Int) {
        promptDialog.showSuccess(msg)
        if (resultCode == Constant.CODE_SUCCESS) {
            Handler(Looper.getMainLooper()).postDelayed({
                onBackPressed()
            }, 500)
        }
    }

    override fun initData() {
        tvCompleted.setOnClickListener {
            // 检查信息填写是否完整
            if (editText.text.isEmpty()) {
                promptDialog.showError(getString(R.string.content_no_write))
                return@setOnClickListener
            }
            promptDialog.showLoading("")
            when (mode) {
                VariableName.CREATE_ADDRESS -> {
                    editAddressBookViewModel.createAddressBook(
                        loginUser?.phoneNum, editText.text.toString()
                    )
                }
                VariableName.EDIT_ADDRESS -> {
                    if (addressBook?.addressName == editText.text.toString()) {
                        showMissionHint(getString(R.string.edit_success), Constant.CODE_SUCCESS)
                        return@setOnClickListener
                    }
                    editAddressBookViewModel.editAddressBook(addressBook?.id.toString(), editText.text.toString())
                }
                else ->{promptDialog.dismiss()}
            }
        }

        tvCancel.setOnClickListener {
            onBackPressed()
        }

        btnCleanUp.setOnClickListener {
            editText.text.clear()
            editText.requestFocus()
        }
    }

}