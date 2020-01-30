package com.ad4th.devote.utils;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ad4th.devote.R;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

public class MaterialDialogUtil {


    public interface OnSelectionListener {
        void onSelection(@NonNull MaterialDialog dialog, View view, int i, CharSequence charSequence);
    }

    /**
     * 긍정 버튼 리스너
     */
    public interface OnPositiveListener {
        void onPositive(@NonNull MaterialDialog dialog);
    }

    /**
     * 부정 버튼 리스너
     */
    public interface OnNegativeListener {
        void onNegative(@NonNull MaterialDialog dialog);
    }

    /**
     * 부정 버튼 리스너
     */
    public interface OnNegativeInputListener {
        void onNegative(@NonNull MaterialDialog dialog, @NonNull EditText edit, CharSequence charSequence);
    }

    /**
     * 긍정 버튼 리스너
     */
    public interface OnPositiveInputListener {
        void onPositive(@NonNull MaterialDialog dialog, @NonNull EditText edit, CharSequence charSequence);
    }

    /**
     * 기본 얼럿 다이얼로그
     *
     * @param context  context
     * @param msgResId Message Resource ID
     */
    public static MaterialDialog alert(@NonNull Context context, int msgResId) {
        return alert(context, null, context.getString(msgResId), "", "", null, null);
    }

    /**
     * 기본 얼럿 다이얼로그
     *
     * @param context context
     * @param msg     Message String
     */
    public static MaterialDialog alert(@NonNull Context context, String msg) {
        return alert(context, null, msg, "", "", null, null);
    }

    /**
     * 기본 얼럿 다이얼로그
     *
     * @param context    context
     * @param titleResId Title Resource ID
     * @param msgResId   Message Resource ID
     */
    public static MaterialDialog alert(@NonNull Context context, int titleResId, int msgResId) {
        return alert(context, context.getString(titleResId), context.getString(msgResId), "", "", null, null);
    }

    /**
     * 기본 얼럿 다이얼로그
     *
     * @param context context
     * @param title   Title String
     * @param msg     Message String
     */
    public static MaterialDialog alert(@NonNull Context context, String title, String msg) {
        return alert(context, title, msg, "", "", null, null);
    }

    /**
     * 기본 얼럿 다이얼로그
     */
    public static MaterialDialog alert(@NonNull Context context, int titleResId, int msgResId, int negativeBtnTxtResId, int postiveBtnTxtResId, OnNegativeListener onNegativeListener, OnPositiveListener onPositiveListener) {
        return alert(context, titleResId == 0 ? null : context.getString(titleResId),
                context.getString(msgResId), context.getString(negativeBtnTxtResId),
                context.getString(postiveBtnTxtResId), onNegativeListener, onPositiveListener);
    }


    /**
     * 커스텀 다이얼로그 빌드
     */
    public static MaterialDialog customDialog(@NonNull Context context, String titleRes, @NonNull String msgResId, @NonNull String postiveRes, final View.OnClickListener onClickListener) {
        final MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        //테마
        builder.theme(Theme.LIGHT);
        builder.backgroundColorRes(R.color.dialog_bg_white);
        builder.customView(R.layout.layout_custom_alert, false);
        builder.cancelable(false);
        final MaterialDialog dialog = builder.build();
        View view = dialog.getCustomView();

        //타이틀
        if (!TextUtils.isEmpty(titleRes)) ((TextView) view.findViewById(R.id.textViewTitle)).setText(titleRes);
        else view.findViewById(R.id.textViewTitle).setVisibility(View.GONE);
        //컨텐츠
        ((TextView) view.findViewById(R.id.textViewMessage)).setText(msgResId);
        //버튼
        Button button = ((Button) view.findViewById(R.id.buttonConfirm));
        if (!org.apache.http.util.TextUtils.isEmpty(postiveRes)) button.setText(postiveRes);
        else button.setText(R.string.confirm);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) onClickListener.onClick(v);
                dialog.dismiss();
            }
        });

        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

            }
        });

        return dialog;
    }

    /**
     * 커스텀 다이얼로그 빌드
     */
    public static MaterialDialog customDialog(@NonNull Context context, int titleResId, int layoutResId, int negativeResId, int postiveResId, final OnNegativeListener onNegativeListener, final OnPositiveListener onPositiveListener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        //테마
        builder.theme(Theme.LIGHT);
        builder.backgroundColorRes(R.color.dialog_bg_white);

        //타이틀
        if (titleResId != 0) builder.title(titleResId);
        //컨텐츠
        if (layoutResId != 0) builder.customView(layoutResId, false);
        //부정 버튼
        if (negativeResId != 0) {
            builder.negativeText(negativeResId);
        } else if (onPositiveListener != null) builder.negativeText(R.string.cancel);
        //긍정버튼
        if (postiveResId != 0) {
            builder.positiveText(postiveResId);
        } else {
            builder.positiveText(R.string.confirm);
        }
        //리스너 셋팅
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                if (onPositiveListener != null) onPositiveListener.onPositive(dialog);
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                if (onNegativeListener != null) onNegativeListener.onNegative(dialog);
            }
        });
        builder.positiveColorRes(R.color.dialog_color_positive)
                .negativeColorRes(R.color.dialog_color_negative);
        return builder.build();
    }

    /**
     * 기본 얼럿 다이얼로그
     *
     * @param context            context
     * @param title              Title String
     * @param msg                Message String
     * @param onNegativeListener 확인 리스너 (사용하지 않는경우  null)
     * @param onPositiveListener 취소 리스너 (사용하지 않는경우  null)
     */
    public static MaterialDialog alert(
            @NonNull
                    Context context, String title, String msg, String negativeBtnTxt, String postiveBtnTxt,
            final OnNegativeListener onNegativeListener, final OnPositiveListener onPositiveListener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.cancelable(false);
        builder.contentGravity(GravityEnum.CENTER);
        //테마
        builder.theme(Theme.LIGHT);
        //타이틀
        if (!TextUtils.isEmpty(title)) builder.title(title);
        //컨텐츠
        if (!TextUtils.isEmpty(msg)) builder.content(msg);
        //부정 버튼
        if (!TextUtils.isEmpty(negativeBtnTxt)) {
            builder.negativeText(negativeBtnTxt);
        } else if (onNegativeListener != null) builder.negativeText(R.string.cancel);
        //긍정버튼
        if (!TextUtils.isEmpty(postiveBtnTxt)) {
            builder.positiveText(postiveBtnTxt);
        } else {
            builder.positiveText(R.string.confirm);
        }
        //리스너 셋팅
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                if (onPositiveListener != null) onPositiveListener.onPositive(dialog);
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                if (onNegativeListener != null) onNegativeListener.onNegative(dialog);
            }
        });
        builder.positiveColorRes(R.color.dialog_color_positive)
                .negativeColorRes(R.color.dialog_color_negative);
        return builder.build();
    }

    /**
     * 확인버튼 만 있는 얼럿
     */
    public static MaterialDialog confirmAlert(@NonNull
                                                      Context context, String title, String msg, String postiveBtnTxt,
                                              final OnPositiveListener onPositiveListener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.cancelable(false);
        builder.contentGravity(GravityEnum.CENTER);
        //테마
        builder.theme(Theme.LIGHT);
        //타이틀
        if (!TextUtils.isEmpty(title)) builder.title(title);
        //컨텐츠
        if (!TextUtils.isEmpty(msg)) builder.content(msg);
        //긍정버튼
        if (!TextUtils.isEmpty(postiveBtnTxt)) {
            builder.positiveText(postiveBtnTxt);
        } else {
            builder.positiveText(R.string.confirm);
        }
        //리스너 셋팅
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                if (onPositiveListener != null) onPositiveListener.onPositive(dialog);
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });
        builder.positiveColorRes(R.color.dialog_color_positive)
                .negativeColorRes(R.color.dialog_color_negative);
        return builder.build();
    }

    /**
     * 기본 얼럿 다이얼로그
     *
     * @param context            context
     * @param title              Title String
     * @param msg                Message String
     * @param itemRes            Single Choice String List
     * @param defaultSelection   초기 선택 아이템의 포지션
     * @param onNegativeListener 확인 리스너 (사용하지 않는경우  null)
     * @param onPositiveListener 취소 리스너 (사용하지 않는경우  null)
     */
    public static MaterialDialog singleChoiceAlert(
            @NonNull
                    Context context, String title, String msg, int itemRes, int defaultSelection,
            String negativeBtnTxt, String postiveBtnTxt, final OnNegativeListener onNegativeListener,
            final OnPositiveListener onPositiveListener, final OnSelectionListener onSelectionListener) {
        return singleChoiceAlert(context, title, msg, context.getResources().getStringArray(itemRes),
                defaultSelection, negativeBtnTxt, postiveBtnTxt, onNegativeListener, onPositiveListener,
                onSelectionListener);
    }

    public static MaterialDialog singleChoiceAlert(
            @NonNull
                    Context context, String title, String msg, final String[] data, int defaultSelection,
            String negativeBtnTxt, String postiveBtnTxt, final OnNegativeListener onNegativeListener,
            final OnPositiveListener onPositiveListener, final OnSelectionListener onSelectionListener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        //테마
        builder.theme(Theme.LIGHT);
        //타이틀
        if (TextUtils.isEmpty(title)) {
            //builder.title(R.string.alert_title);
        } else {
            builder.title(title);
        }
        //컨텐츠
        if (!TextUtils.isEmpty(msg)) builder.content(msg);
        if (data != null && data.length > 0) builder.items(data);
        //부정 버튼
        if (!TextUtils.isEmpty(negativeBtnTxt)) {
            builder.negativeText(negativeBtnTxt);
        } else if (onPositiveListener != null) builder.negativeText(R.string.cancel);
        //긍정버튼
        if (!TextUtils.isEmpty(postiveBtnTxt)) {
            builder.positiveText(postiveBtnTxt);
        } else {
            builder.positiveText(R.string.confirm);
        }
        //리스너 셋팅
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                if (onPositiveListener != null) onPositiveListener.onPositive(dialog);
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
                if (onNegativeListener != null) onNegativeListener.onNegative(dialog);
            }
        });
        builder.itemsCallbackSingleChoice(defaultSelection,
                new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int i,
                                               CharSequence charSequence) {
                        if (onSelectionListener != null) {
                            int size = data.length;
                            if (size > i) {
                                onSelectionListener.onSelection(materialDialog, view, i, charSequence);
                            }
                        }
                        return false;
                    }
                });
        builder.positiveColorRes(R.color.dialog_color_positive)
                .negativeColorRes(R.color.dialog_color_negative);
        return builder.build();
    }


    /**
     * 에러 얼럿
     */
    public static MaterialDialog errorAlert(@NonNull Context context, String msg, final OnPositiveListener onPositiveListener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        //테마
        builder.theme(Theme.LIGHT);
        builder.contentGravity(GravityEnum.CENTER);
        //타이틀
        //builder.title(R.string.alert_title);
        //컨텐츠
        if (!TextUtils.isEmpty(msg)) builder.content(msg);
        //확인버튼
        builder.positiveText(R.string.confirm);
        //리스너 셋팅
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                if (onPositiveListener != null) onPositiveListener.onPositive(dialog);
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });
        builder.positiveColorRes(R.color.dialog_color_positive);

        return builder.build();
    }

    /**
     * EditText Valid 얼럿 . 얼럿 확인버튼을 누르면 해당에디트 텍스트 포커스로 이동한다.
     */
    public static MaterialDialog validEditAlert(
            @NonNull
                    Context context, int msgResId,
            @NonNull
                    EditText editText) {
        return validEditAlert(context, context.getString(msgResId), editText);
    }

    /**
     * EditText Valid 얼럿 . 얼럿 확인버튼을 누르면 해당에디트 텍스트 포커스로 이동한다.
     */
    public static MaterialDialog validEditAlert(
            @NonNull
                    Context context, String msg,
            @NonNull final EditText editText) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        //테마
        builder.theme(Theme.LIGHT);
        //컨텐츠
        if (!TextUtils.isEmpty(msg)) builder.content(msg);
        //긍정버튼
        builder.positiveText(R.string.confirm);
        //리스너 셋팅
        builder.callback(new MaterialDialog.ButtonCallback() {
            @Override
            public void onPositive(MaterialDialog dialog) {
                super.onPositive(dialog);
                editText.requestFocus(0);
            }

            @Override
            public void onNegative(MaterialDialog dialog) {
                super.onNegative(dialog);
            }
        });
        builder.positiveColorRes(R.color.dialog_color_positive)
                .negativeColorRes(R.color.dialog_color_negative);
        return builder.build();
    }


    /**
     * 마쉬멜로우 퍼미션 체크
     */
    @TargetApi(Build.VERSION_CODES.M)
    public static void showPermissionDeniedAlert(final Activity context, String deniedTitle, String deniedContent) {
        showPermissionDeniedAlert(context, deniedTitle, deniedContent, null);
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void showPermissionDeniedAlert(final Activity context, String deniedTitle, String deniedContent, MaterialDialogUtil.OnNegativeListener negativeListener) {
        if (!context.isFinishing()) {
            MaterialDialogUtil.alert(context, deniedTitle, deniedContent, context.getString(R.string.close), context.getString(R.string.setting), negativeListener, new MaterialDialogUtil.OnPositiveListener() {
                @Override
                public void onPositive(MaterialDialog dialog) {
                    // OK 를 누르게 되면 설정창으로 이동합니다.
                    try {
                        //Open the specific App Info page:
                        Intent intent =
                                new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + context.getPackageName()));
                        context.startActivityForResult(intent, 0);
                    } catch (ActivityNotFoundException e) {
                        //Open the generic Apps page:
                        Intent intent =
                                new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                        context.startActivityForResult(intent, 0);
                    }
                }
            }).show();
        }
    }

}
