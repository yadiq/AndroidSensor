package com.hqumath.demo.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hqumath.demo.utils.MultiLanguageUtil;

/**
 * ****************************************************************
 * 文件名称: BaseActivity
 * 作    者: Created by gyd
 * 创建时间: 2019/1/21 15:12
 * 文件描述:
 * 注意事项:
 * 版权声明:
 * ****************************************************************
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected BaseActivity mContext;
    private ProgressDialog mProgressDialog;//loaidng

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(initContentView(savedInstanceState));
        //事件监听
        initListener();
        //初始化数据
        initData();
    }

    /*@Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Density.setAppOrientation(this);//横竖屏切换后，重新设置density
    }*/

    @Override
    protected void attachBaseContext(Context newBase) {
        //Activity Resources 多语言设置
        super.attachBaseContext(MultiLanguageUtil.attachBaseContext(newBase));
    }

    protected abstract View initContentView(Bundle savedInstanceState);

    protected abstract void initListener();

    protected abstract void initData();

    public void showLoading() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setMessage("loading");
        }
        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    public void dismissLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}
