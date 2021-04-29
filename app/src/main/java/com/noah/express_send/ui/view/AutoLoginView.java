package com.noah.express_send.ui.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.noah.express_send.R;
import com.noah.express_send.ui.adapter.io.IReturnLoginToken;

import cn.jiguang.verifysdk.api.AuthPageEventListener;
import cn.jiguang.verifysdk.api.JVerificationInterface;
import cn.jiguang.verifysdk.api.JVerifyUIClickCallback;
import cn.jiguang.verifysdk.api.JVerifyUIConfig;
import cn.jiguang.verifysdk.api.LoginSettings;
import cn.jiguang.verifysdk.api.PreLoginListener;
import cn.jiguang.verifysdk.api.RequestCallback;
import cn.jiguang.verifysdk.api.VerifyListener;

/**
 * @Auther: 何飘
 * @Date: 3/9/21 22:01
 * @Description:
 */
public class AutoLoginView {
    private static final String TAG = "AutoLoginView";
    private CheckBox isDialogModeCB;
    private final Context mContext;
    private static final int CENTER_ID = 1000;
    private static final int LOGIN_CODE_UNSET = -1562;
    private static final String LOGIN_CODE = "login_code";
    private static final String LOGIN_CONTENT = "login_content";
    private static final String LOGIN_OPERATOR = "login_operator";
    private static Bundle savedLoginState = new Bundle();

    public AutoLoginView(Context context) {
        mContext = context;
    }

    public void delPreLoginCache() {
        JVerificationInterface.clearPreLoginCache();
        Log.d(TAG, "清楚缓存成功");
    }

    public void preLogin() {
        boolean verifyEnable = JVerificationInterface.checkVerifyEnable(mContext);
        if (!verifyEnable) {
            Log.d(TAG, "[2016],msg = 当前网络环境不支持认证");
            return;
        }
        //showLoadingDialog();
        JVerificationInterface.preLogin(mContext, 5000, new PreLoginListener() {
            @Override
            public void onResult(final int code, final String content) {
                savedLoginState = null;
                Log.d(TAG, "[" + code + "]message=" + content);
                //dismissLoadingDialog();
            }
        });
    }

    public void loginAuth(boolean isDialogMode, IReturnLoginToken iReturnLoginToken) {
        boolean verifyEnable = JVerificationInterface.checkVerifyEnable(mContext);
        if (!verifyEnable) {
            Log.d(TAG, "[2016],msg = 当前网络环境不支持认证");
            Toast.makeText(mContext, "当前网络环境不支持认证", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingDialog();
        setUIConfig(isDialogMode);
        LoginSettings settings = new LoginSettings();
        settings.setAutoFinish(false);//设置登录完成后是否自动关闭授权页
        settings.setTimeout(15 * 1000);//设置超时时间，单位毫秒。 合法范围（0，30000],范围以外默认设置为10000
        settings.setAuthPageEventListener(new AuthPageEventListener() {
            @Override
            public void onEvent(int cmd, String msg) {
                if (cmd == 1) dismissLoadingDialog();
            }
        });//设置授权页事件监听
        JVerificationInterface.loginAuth(mContext, settings, (code, content, operator) -> {
            Log.d(TAG, "[" + code + "]message=" + content + ", operator=" + operator);
            Bundle bundle = new Bundle();
            bundle.putInt(LOGIN_CODE, code);
            bundle.putString(LOGIN_CONTENT, content);
            bundle.putString(LOGIN_OPERATOR, operator);
            savedLoginState = bundle;
            if (code == 6000) iReturnLoginToken.setLoginToken(content);
        });
    }

    public void finishLoginLoad() {
        JVerificationInterface.dismissLoginAuthActivity(true, new RequestCallback<String>() {
            @Override
            public void onResult(int code, String desc) {
                Log.i(TAG, "[dismissLoginAuthActivity] code = " + code + " desc = " + desc);
                dismissLoadingDialog();
            }
        });
    }

    private void setUIConfig(boolean isDialogMode) {
        JVerifyUIConfig portrait = getPortraitConfig(isDialogMode);
        JVerifyUIConfig landscape = getLandscapeConfig(isDialogMode);

        //支持授权页设置横竖屏两套config，在授权页中触发横竖屏切换时，sdk自动选择对应的config加载。
        JVerificationInterface.setCustomUIWithConfig(portrait, landscape);
    }

    private JVerifyUIConfig getPortraitConfig(boolean isDialogMode) {
        JVerifyUIConfig.Builder configBuilder = new JVerifyUIConfig.Builder();

        //自定义按钮示例1
        ImageView mBtn = new ImageView(mContext);
        mBtn.setImageResource(R.drawable.jverification_qq);
        RelativeLayout.LayoutParams mLayoutParams1 =
                new RelativeLayout.LayoutParams(dp2Pix(mContext, 40), dp2Pix(mContext, 40));
        mLayoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);

        //自定义按钮示例2
        ImageView mBtn2 = new ImageView(mContext);
        mBtn2.setImageResource(R.drawable.jverification_weixin);
        RelativeLayout.LayoutParams mLayoutParams2 =
                new RelativeLayout.LayoutParams(dp2Pix(mContext, 40), dp2Pix(mContext, 40));
        mLayoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        ImageView loadingView = new ImageView(mContext);
        loadingView.setImageResource(R.drawable.umcsdk_load_dot_white);
        RelativeLayout.LayoutParams loadingParam =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingParam.addRule(RelativeLayout.CENTER_IN_PARENT);
        loadingView.setLayoutParams(loadingParam);

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.umcsdk_anim_loading);

        if (isDialogMode) {
            //窗口竖屏
            //自定义返回按钮示例
            ImageButton sampleReturnBtn = new ImageButton(mContext);
            sampleReturnBtn.setImageResource(R.drawable.umcsdk_return_bg);
            RelativeLayout.LayoutParams returnLP =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            returnLP.setMargins(10, 10, 0, 0);
            sampleReturnBtn.setLayoutParams(returnLP);

            configBuilder.setAuthBGImgPath("main_bg")
                    .setNavColor(0xff0086d0)
                    .setNavText("登录")
                    .setNavTextColor(0xffffffff)
                    .setNavReturnImgPath("umcsdk_return_bg")
                    .setLogoWidth(70)
                    .setLogoHeight(70)
                    .setLogoHidden(false)
                    .setNumberColor(0xff333333)
                    .setLogBtnText("本机号码一键登录")
                    .setLogBtnTextColor(0xffffffff)
                    .setLogBtnImgPath("umcsdk_login_btn_bg")
                    //私条款url为网络网页或本地网页地址(sd卡的地址，需自行申请sd卡读权限)，
                    // 如：assets下路径："file:///android_asset/t.html"，
                    // sd卡下路径："file:"+Environment.getExternalStorageDirectory().getAbsolutePath() +"/t.html"
                    .setAppPrivacyColor(0xff666666, 0xff0085d0)
                    .setUncheckedImgPath("umcsdk_uncheck_image")
                    .setCheckedImgPath("umcsdk_check_image")
                    .setSloganTextColor(0xff999999)
                    .setLogoOffsetY(25)
                    .setLogoImgPath("jverification_logo")
                    .setNumFieldOffsetY(130)
                    .setSloganOffsetY(160)
                    .setLogBtnOffsetY(184)
                    .setNumberSize(18)
                    .setPrivacyState(false)
                    .setNavTransparent(false)
                    .setPrivacyOffsetY(5)
                    .setDialogTheme(360, 390, 0, 0, false)
                    .setLoadingView(loadingView, animation)
                    .enableHintToast(true, Toast.makeText(mContext, "checkbox未选中，自定义提示", Toast.LENGTH_SHORT));
        } else {
            //全屏竖屏

            configBuilder.setAuthBGImgPath("main_bg")
                    .setNavColor(0xff0086d0)
                    .setNavText("登录")
                    .setNavTextColor(0xffffffff)
                    .setNavReturnImgPath("umcsdk_return_bg")
                    .setLogoWidth(70)
                    .setLogoHeight(70)
                    .setLogoHidden(false)
                    .setNumberColor(0xff333333)
                    .setLogBtnText("本机号码一键登录")
                    .setLogBtnTextColor(0xffffffff)
                    .setLogBtnImgPath("umcsdk_login_btn_bg")
                    //私条款url为网络网页或本地网页地址(sd卡的地址，需自行申请sd卡读权限)，
                    // 如：assets下路径："file:///android_asset/t.html"，
                    // sd卡下路径："file:"+Environment.getExternalStorageDirectory().getAbsolutePath() +"/t.html"
                    .setAppPrivacyColor(0xff666666, 0xff0085d0)
                    .setUncheckedImgPath("umcsdk_uncheck_image")
                    .setCheckedImgPath("umcsdk_check_image")
                    .setSloganTextColor(0xff999999)
                    .setLogoOffsetY(50)
                    .setLogoImgPath("ic_avatar")
                    .setNumFieldOffsetY(190)
                    .setSloganOffsetY(230)
                    .setLogBtnOffsetY(280)
                    .setNumberSize(24)
                    .setSloganTextSize(13)
                    .setPrivacyState(false)
                    .setNavTransparent(false)
                    .setPrivacyOffsetY(35)
                    .setPrivacyCheckboxInCenter(true)
                    .setPrivacyTextSize(12)
                    .setPrivacyCheckboxSize(14);
        }
        return configBuilder.build();
    }

    private JVerifyUIConfig getLandscapeConfig(boolean isDialogMode) {
        JVerifyUIConfig.Builder configBuilder = new JVerifyUIConfig.Builder();

        if (isDialogMode) {
            //窗口横屏
            //自定义返回按钮示例
            ImageButton sampleReturnBtn = new ImageButton(mContext);
            sampleReturnBtn.setImageResource(R.drawable.umcsdk_return_bg);
            RelativeLayout.LayoutParams returnLP =
                    new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            returnLP.setMargins(10, 10, 0, 0);
            sampleReturnBtn.setLayoutParams(returnLP);

            configBuilder.setAuthBGImgPath("main_bg")
                    .setNavColor(0xff0086d0)
                    .setNavText("登录")
                    .setNavTextColor(0xffffffff)
                    .setNavReturnImgPath("umcsdk_return_bg")
                    .setLogoWidth(70)
                    .setLogoHeight(70)
                    .setLogoHidden(false)
                    .setNumberColor(0xff333333)
                    .setLogBtnText("本机号码一键登录")
                    .setLogBtnTextColor(0xffffffff)
                    .setLogBtnImgPath("umcsdk_login_btn_bg")
                    //私条款url为网络网页或本地网页地址(sd卡的地址，需自行申请sd卡读权限)，
                    // 如：assets下路径："file:///android_asset/t.html"，
                    // sd卡下路径："file:"+Environment.getExternalStorageDirectory().getAbsolutePath() +"/t.html"
                    .setAppPrivacyOne("应用自定义服务条款一", "https://www.jiguang.cn/about")
                    .setAppPrivacyTwo("应用自定义服务条款二", "https://www.jiguang.cn/about")
                    .setAppPrivacyColor(0xff666666, 0xff0085d0)
                    .setUncheckedImgPath("umcsdk_uncheck_image")
                    .setCheckedImgPath("umcsdk_check_image")
                    .setSloganTextColor(0xff999999)
                    .setLogoOffsetY(25)
                    .setLogoImgPath("jverification_logo")
                    .setNumFieldOffsetY(120)
                    .setSloganOffsetY(155)
                    .setLogBtnOffsetY(180)
                    .setPrivacyOffsetY(10)
                    .setDialogTheme(500, 350, 0, 0, false)
                    .enableHintToast(true, null);
        } else {
            configBuilder
                    .setAuthBGImgPath("main_bg")
                    .setNavColor(0xff0086d0)
                    .setNavText("登录")
                    .setNavTextColor(0xffffffff)
                    .setNavReturnImgPath("umcsdk_return_bg")
                    .setLogoWidth(70)
                    .setLogoHeight(70)
                    .setLogoHidden(false)
                    .setNumberColor(0xff333333)
                    .setLogBtnText("本机号码一键登录")
                    .setLogBtnTextColor(0xffffffff)
                    .setLogBtnImgPath("umcsdk_login_btn_bg")
                    //私条款url为网络网页或本地网页地址(sd卡的地址，需自行申请sd卡读权限)，
                    // 如：assets下路径："file:///android_asset/t.html"，
                    // sd卡下路径："file:"+Environment.getExternalStorageDirectory().getAbsolutePath() +"/t.html"
                    .setAppPrivacyOne("应用自定义服务条款一", "https://www.jiguang.cn/about")
                    .setAppPrivacyTwo("应用自定义服务条款二", "https://www.jiguang.cn/about")
                    .setAppPrivacyColor(0xff666666, 0xff0085d0)
                    .setUncheckedImgPath("umcsdk_uncheck_image")
                    .setCheckedImgPath("umcsdk_check_image")
                    .setSloganTextColor(0xff999999)
                    .setLogoOffsetY(30)
                    .setLogoImgPath("jverification_logo")
                    .setNumFieldOffsetY(150)
                    .setSloganOffsetY(185)
                    .setLogBtnOffsetY(210)
                    .setPrivacyOffsetY(10);
        }
        return configBuilder.build();
    }


    private AlertDialog alertDialog;

    public void showLoadingDialog() {
        dismissLoadingDialog();
        alertDialog = new AlertDialog.Builder(mContext).create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        alertDialog.setCancelable(false);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH || keyCode == KeyEvent.KEYCODE_BACK)
                    return true;
                return false;
            }
        });
        alertDialog.show();
        alertDialog.setContentView(R.layout.jverification_loading_alert);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void dismissLoadingDialog() {
        if (null != alertDialog && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    private int dp2Pix(Context context, float dp) {
        try {
            float density = context.getResources().getDisplayMetrics().density;
            return (int) (dp * density + 0.5F);
        } catch (Exception e) {
            return (int) dp;
        }
    }
}
