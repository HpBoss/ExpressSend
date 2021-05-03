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
import com.noah.express_send.ui.view.PriceKeyBoardDialog
import com.noah.express_send.viewModle.ReleaseViewModel
import com.noah.internet.request.RequestOrderEntity
import com.noah.internet.response.BestNewOrderEntity
import kotlinx.android.synthetic.main.activity_release.*
import me.leefeng.promptlibrary.PromptDialog

class ReleaseActivity : BaseActivity(), IReleaseInfo,
    PayIntegralDialog.IClickCompleteInputIntegral {
    private val releaseList = ArrayList<ReleaseInfo>()
    private lateinit var adapter: ReleaseAdapter
    private lateinit var priceKeyBoardDialog: PriceKeyBoardDialog
    private lateinit var payIntegralDialog: PayIntegralDialog
    private val resultList = ArrayList<String>()
    private lateinit var promptDialog: PromptDialog
    private var orderInfo: BestNewOrderEntity? = null
    private var maxPayIntegralNum: Int? = null
    private val releaseViewModel by lazy {
        ViewModelProvider(this).get(ReleaseViewModel::class.java)
    }

    override fun isSetSoftInputMode() = true

    override fun getLayoutId(): Int {
        return R.layout.activity_release
    }

    override fun initView() {
        immersionBar {
            statusBarColor(R.color.blue_364)
            fitsSystemWindows(true)
        }
        val mode = intent.getIntExtra("mode", 0)
        if (mode == 1) { // 当mode为1时，表示当前页面在进行订单修改任务
            btn_release.text = "确认修改"
            orderInfo = intent.getParcelableExtra<BestNewOrderEntity>("orderInfo")
        }
        init(orderInfo)

        val layoutManager = LinearLayoutManager(this)
        recycleView.layoutManager = layoutManager
        recycleView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter = ReleaseAdapter(releaseList, this, this)
        recycleView.adapter = adapter
    }

    override fun initData() {
        iconBack.setOnClickListener {
            onBackPressed()
        }

        btn_addressBook.setOnClickListener {
            startActivity(Intent(this, AddressBookActivity::class.java))
        }

        promptDialog = PromptDialog(this)
        btn_release?.setOnClickListener {
            resultList.clear()
            // 判断所有的内容是否填写完毕
            for (element in releaseList) {
                if (element.hints[0] != '请') {
                    resultList.add(element.hints)
                }
            }
            if (resultList.size == 4 && detail_address.text.toString().isNotBlank()) {
                resultList.add(detail_address.text.toString())
                // 提交resultList
                val user = releaseViewModel.queryIsLoginUser()
                val requestOrderEntity = RequestOrderEntity(
                    user?.phoneNum,
                    resultList[0],
                    resultList[1],
                    resultList[2],
                    resultList[3].substring(0, resultList[3].length - 2).toInt(),
                    resultList[4]
                )
                releaseViewModel.releasePersonalOrder(requestOrderEntity)
                promptDialog.showLoading(getString(R.string.release_loading))
                releaseViewModel.isReleaseSuccess.observe(this, Observer {
                    if (it) {
                        promptDialog.showSuccess(getString(R.string.release_success))
                        Handler(Looper.getMainLooper()).postDelayed({
                            onBackPressed()
                        }, 1000)
                    } else {
                        promptDialog.showError(getString(R.string.release_fail))
                    }
                })
            } else {
                promptDialog.showError(getString(R.string.incomplete_information))
            }
        }
    }

    private fun init(orderInfo: BestNewOrderEntity?) {
        if (orderInfo == null) {
            releaseList.add(ReleaseInfo(R.drawable.ic_express, "快递品牌", "请选择"))
            releaseList.add(ReleaseInfo(R.drawable.ic_category, "快递分类", "请选择"))
            releaseList.add(ReleaseInfo(R.drawable.ic_weight, "预估重量", "请输入"))
            val queryIsLoginUser = releaseViewModel.queryIsLoginUser()
            maxPayIntegralNum = queryIsLoginUser?.integralNum
            releaseList.add(ReleaseInfo(R.drawable.ic_integral_16, "支付积分", "请输入"))
        } else {
            releaseList.add(ReleaseInfo(R.drawable.ic_express, "快递品牌", orderInfo.express!!))
            releaseList.add(ReleaseInfo(R.drawable.ic_category, "快递分类", orderInfo.typeName!!))
            releaseList.add(ReleaseInfo(R.drawable.ic_weight, "预估重量", orderInfo.weight!!))
            releaseList.add(
                ReleaseInfo(
                    R.drawable.ic_integral_16, "支付积分", orderInfo.payIntegralNum.toString())
            )
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
        payIntegralDialog = PayIntegralDialog(this, R.style.ReleaseDialog, 5)
        payIntegralDialog.setIClickCompleteInputIntegral(this)
        payIntegralDialog.show()
    }

    override fun clickComplete(integralNum: Int) {
        if (integralNum == 0 || (maxPayIntegralNum != null && integralNum > maxPayIntegralNum!!)) {
            Toast.makeText(this, "支付积分设置错误", Toast.LENGTH_SHORT).show()
        } else {
            releaseList[3].hints = getString(R.string.payIntegralNum, integralNum)
            adapter.notifyItemChanged(3)
        }
    }
}