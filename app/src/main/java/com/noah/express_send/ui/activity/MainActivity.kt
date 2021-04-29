package com.noah.express_send.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.event.MessageEvent
import cn.jpush.im.api.BasicCallback
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.gyf.immersionbar.ktx.immersionBar
import com.noah.database.User
import com.noah.express_send.R
import com.noah.express_send.ui.adapter.io.IReturnLoginToken
import com.noah.express_send.ui.base.BaseActivity
import com.noah.express_send.ui.view.KeepStateNavigator
import com.noah.express_send.ui.view.AutoLoginView
import com.noah.express_send.utils.MD5Utils
import com.noah.express_send.utils.SizeUtils
import com.noah.express_send.utils.UnreadMsgUtil
import com.noah.express_send.viewModle.MainViewModel
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_nav_header.*
import kotlinx.android.synthetic.main.right_layout_call.*
import kotlinx.android.synthetic.main.right_layout_dormitory.*
import kotlinx.android.synthetic.main.right_layout_integral.*
import kotlinx.android.synthetic.main.right_layout_room_number.*
import kotlinx.android.synthetic.main.right_layout_school.*
import me.leefeng.promptlibrary.PromptDialog

class MainActivity : BaseActivity(), IReturnLoginToken {
    var mNavController: NavController? = null
    private var isVisibilitySearch = true
    private lateinit var btnEditProfile: Button
    private lateinit var iconImage: CircleImageView
    private lateinit var autoLoginView: AutoLoginView
    private lateinit var numView: TextView

    private val mainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun isSetSoftInputMode() = true

    override fun onResume() {
        super.onResume()
        loadIconAvatar()
    }

    fun onEvent(event: MessageEvent) {}

    override fun initView() {
        // 去掉系统状态栏，添加自定义的statusView，目的：保证侧边栏滑出呈现的是沉浸式
        immersionBar {
            statusBarView(status_bar_view)
            statusBarDarkFont(false)
        }
        autoLoginView = AutoLoginView(this)
        autoLoginView.preLogin()
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false) // 不显示标题
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        // 获取headerView
        val headerView = navView.getHeaderView(0)
        iconImage = headerView.findViewById(R.id.iconImage)
        btnEditProfile = headerView.findViewById(R.id.btn_edit)

        val user = mainViewModel.queryIsLoginUser()
        if (user != null) {
            mainViewModel.requestUserProfile(user.phoneNum)
            mainViewModel.profiles.observe(this, {
                if (it == null) return@observe
                mainViewModel.updateUser(
                    User(
                        user.uid, user.phoneNum,
                        it.isLogin, it.avatarUrl,
                        it.nickName, it.schoolName,
                        it.integralNum, it.email,
                        it.dormitory, it.roomNumber
                    )
                )
            })
        }
    }

    override fun initData() {
        // 底部按钮点击切换fragment
        mNavController = Navigation.findNavController(this, R.id.navController)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navController) as NavHostFragment?
        val navigator =
            KeepStateNavigator(
                this,
                navHostFragment!!.childFragmentManager,
                R.id.navController
            )
        mNavController!!.navigatorProvider.addNavigator(navigator)
        mNavController!!.setGraph(R.navigation.navigation)
        val navController: NavController = navHostFragment.navController
        tv_topTitle.text = resources.getString(R.string.icon_index)
        navigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_index -> {
                    tv_topTitle.text = resources.getString(R.string.icon_index)
                    isVisibilitySearch = true
                }
                R.id.navigation_message -> {
                    tv_topTitle.text = resources.getString(R.string.icon_message)
                    isVisibilitySearch = false
                }
                R.id.navigation_history -> {
                    tv_topTitle.text = resources.getString(R.string.icon_order)
                    isVisibilitySearch = false
                }
            }
            invalidateOptionsMenu()
            navController.navigate(it.itemId)
            true
        }

        navView.setNavigationItemSelectedListener {
            val user = mainViewModel.queryIsLoginUser()
            if (user == null) {
                Toast.makeText(this, resources.getString(R.string.please_login), Toast.LENGTH_SHORT).show()
                false
            } else {
                when (it.itemId) {
                    R.id.navSchool -> {
                        jumpToModifyProfileActivity(it, user.schoolName, "school_name")
                    }
                    R.id.navDormitory -> {
                        jumpToModifyProfileActivity(it, user.dormitory, "dormitory")
                    }
                    R.id.navRoomNumber -> {
                        jumpToModifyProfileActivity(it, user.roomNumber, "room_number")
                    }
                }
                true
            }
        }

        btnEditProfile.setOnClickListener {
            // CloseDrawLayoutManager.instance?.setICloseDrawLayout(this)
            startActivity(Intent(this, EditProfileActivity::class.java))
            /*overridePendingTransition(R.anim.translate_right_in,
                R.anim.translate_left_out)*/
        }

        signOut.setOnClickListener {
            val user = mainViewModel.queryIsLoginUser()
            if (user != null) {
                mainViewModel.signOut(user.phoneNum)
                mainViewModel.isSignOutSuccess.observe(this, Observer {
                    user.isLogin = 0
                    mainViewModel.updateUser(user)
                    signOut.visibility = View.GONE
                    drawerLayout.closeDrawers()
                    // 极光im用户退出登录
                    JMessageClient.logout()
                    autoLoginView.loginAuth(false, this)
                })
            }
        }

        // 手指滑动drawerLayout，直到完全打开，然后开始从本地数据库加载数据
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}
            override fun onDrawerOpened(drawerView: View) {
                loadPageData()
            }
        })
    }

    override fun setLoginToken(loginToken: String) {
        mainViewModel.getPhoneNum(loginToken, this)
        val promptDialog = PromptDialog(this)
        promptDialog.showLoading("")
        mainViewModel.phoneNum.observe(this, Observer {
            // 登录成功，添加一个user到Room数据库中，并这个user的isLogin为true
            val user = mainViewModel.queryUser(it)
            if (user == null) {
                mainViewModel.insertUser(User(0, it, 1))
            } else {
                user.isLogin = 1
                mainViewModel.updateUser(user)
            }
            autoLoginView.finishLoginLoad()
            JMessageClient.login(it, MD5Utils.string2MD5(it), object :
                BasicCallback() {
                override fun gotResult(p0: Int, p1: String?) {
                    if (p0 == 0) {
                        promptDialog.dismiss()
                    } else {
                        promptDialog.showError("登录失败！")
                        finish()
                    }
                }
            })
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                loadPageData()
            }
            R.id.search_badge -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.index_toolbar_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu!!.findItem(R.id.search_badge).isVisible = isVisibilitySearch
        return super.onPrepareOptionsMenu(menu)
    }

    private fun jumpToModifyProfileActivity(item: MenuItem, oldValue: String?, columnName: String) {
        val intent = Intent(this, ModifyProfileActivity::class.java)
        intent.putExtra("title", resources.getString(R.string.modify) + item.title)
        intent.putExtra("oldValue", oldValue)
        intent.putExtra("column", columnName)
        startActivity(intent)
    }


    @SuppressLint("RestrictedApi")
    private fun setButtonBadge(position: Int, num: Int) {
        if (num <= 0) {
            return
        }
        val menuView = navigationView.getChildAt(0) as BottomNavigationMenuView
            ?: return
        if (position > menuView.childCount - 1) {
            return
        }
        val itemView = menuView.getChildAt(position) as BottomNavigationItemView
        val badge: View = LayoutInflater.from(this).inflate(
            R.layout.layout_view_badge,
            itemView,
            false
        )
        numView = badge.findViewById<TextView>(R.id.tv_badge_num)
        UnreadMsgUtil.show(numView, num)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.leftMargin = menuView.itemIconSize
        lp.bottomMargin = SizeUtils.dp2px(28F) //BottomNavigationView的高度为50dp
        itemView.addView(badge, lp)
    }

    private fun loadPageData() {
        val user = mainViewModel.queryIsLoginUser()
        if (user != null) {
            if (user.email != null) {
                mailText.text = user.email
            } else mailText.text = resources.getString(R.string.please_add_email)
            if (user.nickName != null) {
                userName.text = user.nickName
            } else userName.text = resources.getString(R.string.please_set_nickname)
            if (user.dormitory != null) {
                tv_dormitory.text = user.dormitory
            } else tv_dormitory.text = resources.getString(R.string.no_write)
            if (user.roomNumber != null) {
                tv_room_number.text = user.roomNumber
            } else tv_room_number.text = resources.getString(R.string.no_write)
            tv_call.text = user.phoneNum
            if (user.integralNum != null) tv_integral.text = user.integralNum.toString()
            else tv_integral.text = resources.getString(R.string.zero)
            if (user.schoolName != null) {
                tv_school.text = user.schoolName
            } else tv_school.text = resources.getString(R.string.no_write)
            mainViewModel.updateUser(user)

            signOut.visibility = View.VISIBLE
            btnEditProfile.visibility = View.VISIBLE
        } else {
            mailText.text = resources.getString(R.string.please_add_email)
            userName.text = resources.getString(R.string.please_set_nickname)
            // iconImage对图片资源的加载
            tv_dormitory.text = resources.getString(R.string.no_write)
            tv_room_number.text = resources.getString(R.string.no_write)
            tv_call.text = resources.getString(R.string.no_write)
            tv_integral.text = resources.getString(R.string.zero)
            tv_school.text = resources.getString(R.string.no_write)
            signOut.visibility = View.GONE
            btnEditProfile.visibility = View.GONE
            iconImage.setImageResource(R.drawable.ic_place_holder)
        }
    }

    private fun loadIconAvatar() {
        val user = mainViewModel.queryIsLoginUser()
        if (user != null) {
            if (user.avatarPath == null) {
                mainViewModel.requestAvatarUrl(user.phoneNum)
            } else {
                Glide.with(this).load(user.avatarPath)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .preload()
                Glide.with(this)
                    .load(user.avatarPath).placeholder(R.drawable.ic_place_holder)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(iconImage)
            }
            mainViewModel.avatarUrl.observe(this, {
                if (it == null) return@observe
                Glide.with(this).load(it)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .preload()
                Glide.with(this)
                    .load(it).placeholder(R.drawable.ic_place_holder)
                    .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(iconImage)
                // 更新本地avatarPath
                user.avatarPath = it
                mainViewModel.updateUser(user)
            })
        }
    }

    override fun onStop() {
        super.onStop()
        drawerLayout.close()
    }
}