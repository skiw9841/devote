package com.ad4th.devote.network;

import android.support.annotation.NonNull;

import com.ad4th.devote.R;
import com.ad4th.devote.activity.IntroActivity;
import com.ad4th.devote.base.BaseActivity;
import com.ad4th.devote.utils.MaterialDialogUtil;
import com.afollestad.materialdialogs.MaterialDialog;
import com.voting.kotlin.utils.LogUtil;

import org.jetbrains.annotations.NotNull;

public class RetroCallback<T>  implements  RetroCallbackBase<T>{

    private static final String TAG = RetroCallback.class.getName();

    BaseActivity baseActivity;

    public RetroCallback(BaseActivity baseActivity) {
        this.baseActivity = baseActivity;
        if (!(baseActivity instanceof IntroActivity)) baseActivity.showProgress();
    }

    @Override
    public void onError(@NotNull Throwable t) {
        LogUtil.INSTANCE.e(TAG, t.getMessage());
        baseActivity.dismissProgress();
        MaterialDialogUtil.errorAlert(baseActivity, baseActivity.getString(R.string.alert_network_error), new MaterialDialogUtil.OnPositiveListener() {
            @Override
            public void onPositive(@NonNull MaterialDialog dialog) {
                baseActivity.finish();
            }
        }).show();
    }

    @Override
    public void onSuccess(int code, Object receivedData) {
        baseActivity.dismissProgress();
    }

    @Override
    public void onFailure(int code) {
        baseActivity.dismissProgress();
        MaterialDialogUtil.errorAlert(baseActivity, baseActivity.getString(R.string.alert_network_error), new MaterialDialogUtil.OnPositiveListener() {
            @Override
            public void onPositive(@NonNull MaterialDialog dialog) {
                baseActivity.finish();
            }
        }).show();

    }
}
