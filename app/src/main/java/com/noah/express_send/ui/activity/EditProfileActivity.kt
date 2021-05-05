package com.noah.express_send.ui.activity

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.api.BasicCallback
import com.gyf.immersionbar.ktx.immersionBar
import com.jhworks.library.ImageSelector
import com.jhworks.library.core.MediaSelectConfig
import com.jhworks.library.core.vo.SelectMode
import com.nanchen.compresshelper.CompressHelper
import com.noah.express_send.R
import com.noah.express_send.bean.ProfileInfo
import com.noah.express_send.ui.adapter.ProfileAdapter
import com.noah.express_send.ui.adapter.io.IOpenModify
import com.noah.express_send.viewModle.EditProfileViewModel
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.item_profile_view.*
import kotlinx.android.synthetic.main.item_status_bar.*
import me.leefeng.promptlibrary.PromptDialog
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class EditProfileActivity : AppCompatActivity(), IOpenModify {
    private lateinit var adapter: ProfileAdapter
    private var profileList = ArrayList<ProfileInfo>()
    private var mSelectPath: ArrayList<String>? = null
    private var isChangeAvatar = false
    private val promptDialog by lazy {
        PromptDialog(this)
    }
    private val editProfileViewModel by lazy {
        ViewModelProvider(this).get(EditProfileViewModel::class.java)
    }

    companion object {
        private const val REQUEST_IMAGE = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        immersionBar {
            statusBarView(status_bar_view)
            statusBarDarkFont(false)
        }
        initView()
        initRecycleView()
    }

    private fun initRecycleView() {
        initData()
        val layoutManager = LinearLayoutManager(this)
        recycleView.layoutManager = layoutManager
        val user = editProfileViewModel.queryIsLoginUser()
        adapter = ProfileAdapter(profileList, this, this, user?.phoneNum)
        recycleView.adapter = adapter
    }

    override fun onRestart() {
        super.onRestart()
        val user = editProfileViewModel.queryIsLoginUser()
        profileList[0].imgResource = user?.avatarPath
        profileList[1].word = user?.email
        profileList[2].word = user?.nickName
        if (!isChangeAvatar) {
            adapter.notifyDataSetChanged()
        }
        isChangeAvatar = false
    }

    private fun initData() {
        val user = editProfileViewModel.queryIsLoginUser()
        if (user != null) {
            profileList.add(ProfileInfo("头像", imgResource = user.avatarPath))

            if (user.email != null) {
                profileList.add(ProfileInfo("邮箱", word = user.email))
            } else profileList.add(ProfileInfo("邮箱", word = "未填写"))
            if (user.nickName != null) {
                profileList.add(ProfileInfo("昵称", word = user.nickName))
            } else profileList.add(ProfileInfo("昵称", word = "未填写"))
        }
    }

    private fun initView() {
        imgCancel.setOnClickListener {
            onBackPressed() // 调用会销毁当前Activity
        }
    }

    override fun startModifyActivity(title: String, type: Int, columnName: String) {
        val intent = Intent(this, ModifyProfileActivity::class.java)
        intent.putExtra("title", title)
        val user = editProfileViewModel.queryIsLoginUser()
        when (columnName) {
            "email" -> intent.putExtra("oldValue", user?.email)
            "nick_name" -> intent.putExtra("oldValue", user?.nickName)
        }
        intent.putExtra("column", columnName)
        intent.putExtra("type", type)
        startActivity(intent)
        overridePendingTransition(
            R.anim.translate_right_in,
            R.anim.translate_left_out
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                mSelectPath = ImageSelector.getSelectResults(data)
                val sb = StringBuilder()
                mSelectPath?.forEach {
                    sb.append(it)
                }
                val user = editProfileViewModel.queryIsLoginUser()
                if (user != null) {
                    val oldFile = File(sb.toString())
                    // 对图片进行压缩(来源于nanchen的库)
                    val newFile = CompressHelper.getDefault(this).compressToFile(oldFile)
                    val body = newFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val part = MultipartBody.Part.createFormData("file", newFile.name, body)
                    promptDialog.showLoading("上传中...")
                    editProfileViewModel.updateAvatarUrl(user.phoneNum, part)
                    JMessageClient.updateUserAvatar(newFile, object : BasicCallback() {
                        override fun gotResult(p0: Int, p1: String?) {
                            if (p0 == 0) Log.d("JMessageClient", "头像更新成功")
                        }
                    })
                }
                editProfileViewModel.avatarUrl.observe(this, {
                    val bitmap = BitmapFactory.decodeFile(sb.toString())
                    profileAvatar.setImageBitmap(bitmap)
                    user?.avatarPath = it
                    editProfileViewModel.updateUser(user)
                    promptDialog.dismiss()
                })
            }
        }
    }

    override fun pickImage(isOpenCameraOnly: Boolean) {
        isChangeAvatar = true
        if (mSelectPath != null && mSelectPath!!.size > 0) mSelectPath!!.clear()
        ImageSelector.startImageAction(
            this, REQUEST_IMAGE,
            MediaSelectConfig.Builder()
                .setSelectMode(SelectMode.MODE_SINGLE)
                .setOriginData(mSelectPath)
                .setShowCamera(false)
                .setTheme(R.style.sl_theme_custom)
                .setPlaceholderResId(R.drawable.ic_place_holder)
                .setOpenCameraOnly(isOpenCameraOnly)
                .setMaxCount(20)
                .setImageSpanCount(3)
                .build()
        )
    }
}