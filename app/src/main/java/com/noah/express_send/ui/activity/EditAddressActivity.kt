package com.noah.express_send.ui.activity

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.ViewModelProvider
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.express_send.R
import com.noah.express_send.bean.EditAddressInfo
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.utils.VariableName
import com.noah.express_send.viewModle.EditAddressViewModel
import com.noah.internet.response.ResponseAddressBook
import kotlinx.android.synthetic.main.activity_modify_profile.*
import kotlinx.android.synthetic.main.item_action_bar.*
import me.leefeng.promptlibrary.PromptDialog

class EditAddressActivity : BaseActivity(){
    private var addressBook: ResponseAddressBook? = null
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
            statusBarColor(R.color.blue_364)
            fitsSystemWindows(true)
        }
        tvSurplus.visibility = View.GONE
        tvSchoolName.visibility = View.VISIBLE
        tvSchoolName.text = editAddressBookViewModel.queryIsLoginUser()?.schoolName
        editText.hint = getString(R.string.detailNameHint)
        val mode = intent.getIntExtra(getString(R.string.key_mode), VariableName.CREATE_ADDRESS)
        addressBook = intent.getParcelableExtra(VariableName.ADDRESS_BOOK)
        if (mode == VariableName.CREATE_ADDRESS) {
            modifyTitle.text =
                getString(R.string.create_address_title)
        } else {
            modifyTitle.text =
                getString(R.string.edit_address_title)
            editText.text.clear()
            editText.text.insert(0, addressBook?.detailName)
        }
        editAddressBookViewModel.isSuccessEditAddressBook.observe(this, {
            promptDialog.showSuccess("编辑成功")
            Handler(Looper.getMainLooper()).postDelayed({
                onBackPressed()
            }, 500)
        })

        // 保证进入页面时就弹出软键盘
        editText.postDelayed({
            editText.requestFocus()
            val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(0, 0)
        }, 200)
    }

    override fun initData() {
        tvCompleted.setOnClickListener {
            // 检查信息填写是否完整
            promptDialog.showLoading("")
            editAddressBookViewModel.editAddressBook(addressBook?.id.toString())
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