package com.ad4th.devote.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ad4th.devote.R;


public class MaterialProgressDialog {
    private static CustomDialog mProgressDialog;
    private static final String TAG = MaterialProgressDialog.class.getName();
    private static boolean mTransparentMode; // 배경 투명모드

    public static class CustomDialog extends Dialog {

        public CustomDialog(Activity context) {
            super(context, mTransparentMode ? R.style.TransparentNewDialog : R.style.NewDialog);
        }

        public static CustomDialog show(Activity context) {
            CustomDialog dialog = new CustomDialog(context);
            View viewGroup = View.inflate(context, R.layout.material_progress_item, null);
            dialog.setOnKeyListener(new OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK &&
                            event.getAction() == KeyEvent.ACTION_UP &&
                            !event.isCanceled()) {
                        dialog.dismiss();
                        return true;
                    }
                    return false;
                }
            });

            dialog.setContentView(viewGroup);
            dialog.setCancelable(false);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            dialog.show();
            return dialog;
        }

        @Override
        public void dismiss() {
            super.dismiss();
            mProgressDialog = null;
        }
    }

    /**
     * 프로그래스 다이얼로그 활성화
     */
    public void show(Activity context) {
        try {
            if (!context.isFinishing()) {
                if (mProgressDialog == null) {
                    mTransparentMode = false;
                    mProgressDialog = CustomDialog.show(context);
                } else {
                    dismiss();
                    show(context);
                }
            }
        } catch (WindowManager.BadTokenException e) {
        }
    }

    /**
     * 프로그래스 다이얼로그 활성화
     *
     * @param context
     * @param transparentMode 투명모드 true on false off
     */
    public void show(Activity context, boolean transparentMode) {
        try {
            if (!context.isFinishing()) {
                if (mProgressDialog == null) {
                    mTransparentMode = transparentMode;
                    mProgressDialog = CustomDialog.show(context);
                } else {
                    dismiss();
                    show(context);
                }
            }
        } catch (WindowManager.BadTokenException e) {
        }
    }

    /**
     * 프로그래스 다이얼로그 비활성화
     */
    public void dismiss() {
        if (mProgressDialog != null) {
            try {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
            } catch (IllegalArgumentException error) {
                mProgressDialog.cancel();
            }
        }
    }
}
