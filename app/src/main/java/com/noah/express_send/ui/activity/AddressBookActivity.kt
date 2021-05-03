package com.noah.express_send.ui.activity

import android.content.Intent
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.express_send.R
import com.noah.express_send.ui.adapter.AddressBookAdapter
import com.noah.express_send.ui.adapter.io.IAddressOperate
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.utils.VariableName
import com.noah.express_send.viewModle.AddressBookViewModel
import com.noah.internet.response.ResponseAddressBook
import kotlinx.android.synthetic.main.activity_address_book.*
import kotlinx.android.synthetic.main.item_action_bar.*
import me.leefeng.promptlibrary.PromptDialog
import kotlin.properties.Delegates

class AddressBookActivity : BaseActivity(), IAddressOperate {
    private lateinit var adapter: AddressBookAdapter
    private var position by Delegates.notNull<Int>()
    private val promptDialog by lazy {
        PromptDialog(this)
    }
    private val addressBookViewModel by lazy {
        ViewModelProvider(this).get(AddressBookViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_address_book
    }

    override fun isSetSoftInputMode() = true

    override fun initView() {
        immersionBar {
            statusBarColor(R.color.blue_364)
            fitsSystemWindows(true)
        }
        actionBar_title.text = getString(R.string.address_book)
        back.setOnClickListener {
            onBackPressed()
        }
        btn_create_address.setOnClickListener {
            val intent = Intent(this, EditAddressActivity::class.java)
            intent.putExtra(getString(R.string.key_mode), VariableName.CREATE_ADDRESS)
            startActivity(intent)
        }
        initRecycleView()
    }

    override fun initData() {
        addressBookViewModel.addressBookList.observe(this, {
            if (it.size > 0) hintPlaceHolder.visibility = View.GONE
            adapter.setAdapter(it)
        })
        addressBookViewModel.isSuccessDeleteAddressBook.observe(this, {
            if (adapter.removeAt(position) <= 0) hintPlaceHolder.visibility = View.VISIBLE
            promptDialog.showSuccess("删除成功")
        })
    }

    override fun onStart() {
        super.onStart()
        addAddressBookDate()
    }

    private fun addAddressBookDate() {
        val queryIsLoginUser = addressBookViewModel.queryIsLoginUser()
        addressBookViewModel.getPersonalAddressBook(queryIsLoginUser?.phoneNum)
    }

    private fun initRecycleView() {
        val layoutManager = LinearLayoutManager(this)
        recycleView.layoutManager = layoutManager
        adapter = AddressBookAdapter(this)
        recycleView.adapter = adapter
    }

    override fun deleteAddress(position: Int, id: Int) {
        promptDialog.showLoading("")
        this.position = position
        addressBookViewModel.deleteAddressBook(id.toString())
    }

    override fun editAddress(position: Int, responseAddressBook: ResponseAddressBook) {
        val intent = Intent(this, EditAddressActivity::class.java)
        intent.putExtra(getString(R.string.key_mode), VariableName.EDIT_ADDRESS)
        intent.putExtra(VariableName.ADDRESS_BOOK, responseAddressBook)
        startActivity(intent)
    }
}