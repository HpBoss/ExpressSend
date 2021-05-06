package com.noah.express_send.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.express_send.R
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.viewModle.CommentViewModel
import com.noah.internet.request.RequestCommentEntity
import com.noah.internet.response.BestNewOrderEntity
import com.noah.internet.response.ResponseChipEntity
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.item_action_bar.*
import me.leefeng.promptlibrary.PromptDialog

class CommentActivity : BaseActivity(), View.OnClickListener {
    private var highChipsList = ArrayList<ResponseChipEntity>()
    private var lowChipsList = ArrayList<ResponseChipEntity>()
    private var commentState: Int = -1
    private var orderInfo: BestNewOrderEntity? = null
    private val promptDialog by lazy {
        PromptDialog(this)
    }
    private val commentViewModel by lazy {
        ViewModelProvider(this).get(CommentViewModel::class.java)
    }

    companion object {
        const val LOW_OPINION = 0
        const val HIGH_OPINION = 1
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_comment
    }

    override fun isSetSoftInputMode() = true

    override fun initView() {
        immersionBar {
            statusBarColor(R.color.blue_364)
            fitsSystemWindows(true)
        }
        actionBar_title.text = getString(R.string.release_comment)
        button.visibility = View.VISIBLE
        commentViewModel.chipsList.observe(this, {
            for (chipEntity in it) {
                if (chipEntity.type == HIGH_OPINION) {
                    highChipsList.add(chipEntity)
                } else {
                    lowChipsList.add(chipEntity)
                }
            }
        })
        commentViewModel.isSuccessCommentOrder.observe(this, {
            if (it) {
                promptDialog.showSuccess(getString(R.string.comment_success))
                Handler(Looper.getMainLooper()).postDelayed({
                    onBackPressed()
                }, 500)
            } else  promptDialog.showError(getString(R.string.comment_failure))
        })
    }

    override fun initData() {
        commentViewModel.getAllCommentChips()
        orderInfo = intent.getParcelableExtra<BestNewOrderEntity>("orderInfo")
        tv_express.text = orderInfo?.express
        tv_weight.text = orderInfo?.weight
        tv_typeName.text = orderInfo?.typeName
        tv_payIntegralNum.text = getString(R.string.payIntegralNum, orderInfo?.payIntegralNum)
        tv_dormitory.text = orderInfo?.dormitory
        tv_address.text = orderInfo?.addressName
        tv_highOpinion.setOnClickListener(this)
        tv_lowOpinion.setOnClickListener(this)
        button.setOnClickListener(this)
    }

    private fun addChipToChipGroup(chipList: ArrayList<ResponseChipEntity>, type: Int) {
        chipGroup.removeAllViews()
        var count = 0
        for (chipEntity in chipList) {
            val chip = layoutInflater.inflate(R.layout.item_chip, chipGroup,false) as Chip
            chip.text = chipEntity.chipName
            chip.id = count
            count += 1
            chipGroup.addView(chip)
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.tv_highOpinion -> {
                addChipToChipGroup(highChipsList, HIGH_OPINION)
                chipGroup.visibility = View.VISIBLE
                commentState = HIGH_OPINION
                tv_highOpinion.background =
                    ContextCompat.getDrawable(this, R.drawable.rectangle_button_login_light)
                tv_lowOpinion.background =
                    ContextCompat.getDrawable(this, R.drawable.rectangle_button_login_grey)
            }
            R.id.tv_lowOpinion -> {
                addChipToChipGroup(lowChipsList, LOW_OPINION)
                chipGroup.visibility = View.VISIBLE
                commentState = LOW_OPINION
                tv_lowOpinion.background =
                    ContextCompat.getDrawable(this, R.drawable.rectangle_button_login_light)
                tv_highOpinion.background =
                    ContextCompat.getDrawable(this, R.drawable.rectangle_button_login_grey)
            }
            R.id.button -> {
                val checkChipList = chipGroup.checkedChipIds
                if (commentState == -1 || checkChipList.size == 0) {
                    promptDialog.showError(getString(R.string.content_no_write))
                    return
                }
                val chipIdList = ArrayList<Int>()
                for (index in checkChipList) {
                    if (commentState == HIGH_OPINION) {
                        chipIdList.add(highChipsList[index].id)
                    } else {
                        chipIdList.add(lowChipsList[index].id)
                    }
                }
                commentViewModel.commentOrder(
                    RequestCommentEntity(
                        commentViewModel.queryIsLoginUser()?.phoneNum,
                        orderInfo?.oid!!.toInt(),
                        commentState,
                        chipIdList,
                        et_comment.text.toString()
                    )
                )
            }
        }
    }
}