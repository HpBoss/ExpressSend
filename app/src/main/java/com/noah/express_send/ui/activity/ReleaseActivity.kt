package com.noah.express_send.ui.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.express_send.R
import com.noah.express_send.bean.ReleaseInfo
import com.noah.express_send.ui.adapter.ReleaseAdapter
import com.noah.express_send.ui.adapter.io.IReleaseInfo
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.ui.dialog.PayIntegralDialog
import com.noah.express_send.ui.dialog.PriceKeyBoardDialog
import com.noah.express_send.utils.VariableName
import com.noah.express_send.viewModle.ReleaseViewModel
import com.noah.internet.request.RequestOrder
import com.noah.internet.response.BestNewOrder
import com.noah.internet.response.ResponseAddressBook
import kotlinx.android.synthetic.main.activity_release.*
import kotlinx.android.synthetic.main.item_action_bar.*
import kotlinx.android.synthetic.main.item_status_bar.*
import me.leefeng.promptlibrary.PromptDialog

class ReleaseActivity : BaseActivity(), IReleaseInfo,
    PayIntegralDialog.IClickCompleteInputIntegral {
    private val releaseList = ArrayList<ReleaseInfo>()
    private lateinit var adapter: ReleaseAdapter
    private lateinit var priceKeyBoardDialog: PriceKeyBoardDialog
    private lateinit var payIntegralDialog: PayIntegralDialog
    private val resultList = ArrayList<String>()
    private lateinit var promptDialog: PromptDialog
    private var orderInfo: BestNewOrder? = null
    private var maxPayIntegralNum: Int? = null
    private var mode: Int = RELEASE_ORDER
    private lateinit var loadingMsg: String
    private lateinit var resultSuccessMsg: String
    private lateinit var resultFailureMsg: String
    private var oldIntegralNum: Int? = 0
    private val releaseViewModel by lazy {
        ViewModelProvider(this).get(ReleaseViewModel::class.java)
    }

    companion object {
        const val RELEASE_ORDER = 0
        const val MODIFY_ORDER = 1
    }

    override fun isSetSoftInputMode() = true

    override fun getLayoutId(): Int {
        return R.layout.activity_release
    }

    override fun initView() {
        immersionBar {
            statusBarView(status_bar_view)
            statusBarDarkFont(false)
        }
        actionBar_title.text = getString(R.string.release)
        loadingMsg = getString(R.string.release_loading)
        resultSuccessMsg = getString(R.string.release_success)
        resultFailureMsg = getString(R.string.release_failure)
        mode = intent.getIntExtra("mode", RELEASE_ORDER)
        if (mode == MODIFY_ORDER) { // 当mode为1时，表示当前页面在进行订单修改任务
            btn_release.text = getString(R.string.sure_modify)
            actionBar_title.text = getString(R.string.modify_order)
            loadingMsg = getString(R.string.modify_loading)
            resultSuccessMsg = getString(R.string.modify_success)
            resultFailureMsg = getString(R.string.modify_failure)
            orderInfo = intent.getParcelableExtra<BestNewOrder>("orderInfo")
        }
        init(orderInfo)

        val layoutManager = LinearLayoutManager(this)
        recycleView.layoutManager = layoutManager
        recycleView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter = ReleaseAdapter(releaseList, this, this)
        recycleView.adapter = adapter
    }

    override fun initData() {
        back.setOnClickListener {
            onBackPressed()
        }

        btn_addressBook.setOnClickListener {
            startActivityForResult(Intent(this, AddressBookActivity::class.java), 10)
        }

        releaseViewModel.isReleaseSuccess.observe(this, Observer {
            if (it) {
                promptDialog.showSuccess(resultSuccessMsg)
                Handler(Looper.getMainLooper()).postDelayed({
                    onBackPressed()
                }, 1000)
            } else {
                promptDialog.showError(resultFailureMsg)
            }
        })

        promptDialog = PromptDialog(this)
        btn_release?.setOnClickListener {
            resultList.clear()
            // 判断所有的内容是否填写完毕
            for (element in releaseList) {
                if (element.hints[0] != '请') {
                    resultList.add(element.hints)
                }
            }
            if (resultList.size == 4 && et_addressName.text.toString().isNotBlank()) {
                resultList.add(et_addressName.text.toString())
                // 提交resultList
                val user = releaseViewModel.queryIsLoginUser()
                val requestOrderEntity = RequestOrder(
                    orderInfo?.oid, user?.phoneNum,
                    resultList[0], user?.schoolName,
                    resultList[1], resultList[2],
                    resultList[3].substring(0, resultList[3].length - 2).toInt(), resultList[4])
                releaseViewModel.releasePersonalOrder(requestOrderEntity)
                promptDialog.showLoading(loadingMsg)
            } else {
                promptDialog.showError(getString(R.string.incomplete_information))
            }
        }
    }

    private fun init(orderInfo: BestNewOrder?) {
        if (orderInfo == null) {
            releaseList.add(ReleaseInfo(R.drawable.ic_express, "快递品牌", "请选择"))
            releaseList.add(ReleaseInfo(R.drawable.ic_category, "快递分类", "请选择"))
            releaseList.add(ReleaseInfo(R.drawable.ic_weight, "预估重量", "请输入"))
            val queryIsLoginUser = releaseViewModel.queryIsLoginUser()
            maxPayIntegralNum = queryIsLoginUser?.integralNum
            releaseList.add(ReleaseInfo(R.drawable.ic_integral_16, "支付积分", "请输入"))
        } else {
            releaseList.add(ReleaseInfo(R.drawable.ic_express, "快递品牌", orderInfo.expressName!!))
            releaseList.add(ReleaseInfo(R.drawable.ic_category, "快递分类", orderInfo.typeName!!))
            releaseList.add(ReleaseInfo(R.drawable.ic_weight, "预估重量", orderInfo.weight!!))
            releaseList.add(
                ReleaseInfo(
                    R.drawable.ic_integral_16,
                    "支付积分",
                    getString(R.string.payIntegralNum, orderInfo.payIntegralNum)
                )
            )
            oldIntegralNum = orderInfo.payIntegralNum
            et_addressName.text.clear()
            et_addressName.text.insert(0, orderInfo.addressName)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == RESULT_OK) {
            val addressBook =
                data?.getParcelableExtra<ResponseAddressBook>(VariableName.ADDRESS_BOOK)
            et_addressName.text.clear()
            et_addressName.text.insert(0, addressBook?.addressName)
        }
    }

    override fun updateRelease(position: Int, result: String) {
        releaseList[position].hints = result
        adapter.notifyDataSetChanged()
    }

    override fun startKeyBoard() {
        priceKeyBoardDialog = PriceKeyBoardDialog(this, R.style.ReleaseDialog, this)
        priceKeyBoardDialog.show()
        priceKeyBoardDialog.setOnKeyboardListener(object : PriceKeyBoardDialog.IOnKeyBoardListener {
            override fun getKeyboardValue(startValue: String?, endValue: String?) {
                if (!startValue.isNullOrBlank() || !endValue.isNullOrEmpty()) {
                    releaseList[2].hints = "$startValue~$endValue Kg"
                    adapter.notifyItemChanged(2)
                }
                priceKeyBoardDialog.dismiss()
            }

            override fun cancelKeyboardInput() {
                priceKeyBoardDialog.dismiss()
            }
        })
    }

    override fun startSetPayIntegralNum() {
        payIntegralDialog = PayIntegralDialog(
            this,
            R.style.ReleaseDialog,
            releaseViewModel.queryIsLoginUser()?.integralNum,
            oldIntegralNum
        )
        payIntegralDialog.setIClickCompleteInputIntegral(this)
        payIntegralDialog.show()
    }

    override fun clickComplete(integralNum: Int) {
        if (integralNum == 0 || (maxPayIntegralNum != null && integralNum > maxPayIntegralNum!!)) {
            Toast.makeText(this, getString(R.string.pay_integral_set_wrong), Toast.LENGTH_SHORT)
                .show()
        } else {
            releaseList[3].hints = getString(R.string.payIntegralNum, integralNum)
            oldIntegralNum = integralNum
            adapter.notifyItemChanged(3)
        }
    }
}