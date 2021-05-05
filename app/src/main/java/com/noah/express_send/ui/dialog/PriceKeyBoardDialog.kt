package com.noah.express_send.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.noah.express_send.R
import com.noah.pricekeyboard.KeyboardUtil

/**
 * @Auther: 何飘
 * @Date: 2/27/21 16:55
 * @Description:
 */
class PriceKeyBoardDialog constructor(context: Context, themeResId: Int, activity: Activity) : Dialog(
    context,
    themeResId
) {
    private lateinit var llPriceSelect: LinearLayout
    private lateinit var etFreightStart: EditText
    private lateinit var etFreightEnd: EditText
    private lateinit var mKeyboardUtil: KeyboardUtil
    private var mActivity = activity
    private var mIOnKeyBoardListener: IOnKeyBoardListener? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.price_keyboard_view)
        val window = this.window!!
        window.setGravity(Gravity.BOTTOM)
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle)
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        initPriceKeyBoard()

        mKeyboardUtil = KeyboardUtil(mActivity, this)
        mKeyboardUtil.attachTo(etFreightStart)

        // 点击起始重量
        etFreightStart.setOnTouchListener { v, event ->
            mKeyboardUtil.attachTo(etFreightStart)
            false
        }

        // 点击结尾重量
        etFreightEnd.setOnTouchListener { v, event ->
            mKeyboardUtil.attachTo(etFreightEnd)
            false
        }

        // 点击完成按钮
        mKeyboardUtil.setOnOkClick(
            object : KeyboardUtil.OnOkClick {
                override fun onOkClick() {
                    if (etFreightStart.text.toString() == "" || etFreightEnd.text.toString() == "") {
                        Toast.makeText(context,"内容不能为空!", Toast.LENGTH_SHORT).show()
                    } else if (etFreightStart.text.toString().toFloat() > etFreightEnd.text.toString().toFloat()) {
                        Toast.makeText(context,"重量范围输入错误!", Toast.LENGTH_SHORT).show()
                    } else {
                        mKeyboardUtil.hideKeyboard()
                        mIOnKeyBoardListener?.getKeyboardValue(etFreightStart.text.toString(),
                            etFreightEnd.text.toString())
                    }
                }
            })

        // 点击取消按钮
        mKeyboardUtil.setOnCancelClick(
            object : KeyboardUtil.IonCancelClick {
                override fun onCancelClick() {
                    mIOnKeyBoardListener?.cancelKeyboardInput()
                }
            })
    }

    fun setOnKeyboardListener(IOnKeyBoardListener: IOnKeyBoardListener) {
        mIOnKeyBoardListener = IOnKeyBoardListener
    }

    private fun initPriceKeyBoard() {
        llPriceSelect = findViewById(R.id.ll_price_select)
        etFreightStart = findViewById(R.id.et_freight_start)
        etFreightEnd = findViewById(R.id.et_freight_end)
    }

    interface IOnKeyBoardListener {
        fun getKeyboardValue(startValue: String?, endValue: String?)
        fun cancelKeyboardInput()
    }

}