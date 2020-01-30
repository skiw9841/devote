package com.ad4th.devote.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.Locale;

@SuppressWarnings("deprecation")
public class UiUtil {
    /**
     * wvga(480x800)
     */
    static final String TYPE_1 = "TYPE_1";
    /**
     * hd(1280*720)
     */
    static final String TYPE_2 = "TYPE_2";
    /**
     * full-hd(1920*1080)
     */
    static final String TYPE_3 = "TYPE_3";

    public static String getScreenType(Context ctx) {
        int width = getScreenWidth(ctx);
        int height = getScreenHeight(ctx);
        int longSide = width < height ? height : width;
        if (longSide >= 1920) {
            return TYPE_3;
        } else if (longSide >= 1280) {
            return TYPE_2;
        } else if (longSide >= 800) {
            return TYPE_1;
        } else {
            return TYPE_1;
        }
    }

    /**
     * 디바이스 가로 사이즈를 가져온다
     */
    public static int getScreenWidth(Context ctx) {
        Display display =
                ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int displayWidth = display.getWidth();
        int displayHeight = display.getHeight();
        return displayWidth > displayHeight ? displayHeight : displayWidth;
    }

    /**
     * 디바이스 세로 사이즈를 가져온다
     */
    public static int getScreenHeight(Context ctx) {
        Display display =
                ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int displayWidth = display.getWidth();
        int displayHeight;
        try {
            Point p = new Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                display.getRealSize(p);
            else
                display.getSize(p);
            displayHeight = p.y;
        } catch (NoSuchMethodError ne) {
            displayHeight = display.getHeight();
        } catch (Exception e) {
            displayHeight = display.getHeight();
        }
        return displayWidth < displayHeight ? displayHeight : displayWidth;
    }

    /**
     * dp를 px로 변환
     */
    public static int convertDpToPx(Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * px를 dp로 변환
     */
    public static int convertPxToDp(Context context, float pixels) {
        // Get the screen's density scale
        final float scale = context.getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    /**
     * sp를 px로 변환
     */
    public static float convertSpToPx(Context context, float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 리소스 이미지의 사이즈를 가져온다.
     *
     * @param context
     * @param resId
     */
    public static String getResouceImgSize(Context context, int resId) {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), resId, bounds);
        int imageHeight = bounds.outHeight;
        int imageWidth = bounds.outWidth;
        return imageWidth + "*" + imageHeight;
    }


    public static int getAlpha(int opacity) {
        int res = (int) (256 * ((double) opacity / (double) 100) - 1);
        return res < 0 ? 0 : res;
    }

    public static boolean isKorean() {
        return (Locale.getDefault().equals(Locale.KOREA) || Locale.getDefault().equals(Locale.KOREAN));
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        Context context = v.getContext();
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(convertPxToDp(context, l), convertPxToDp(context, t), convertPxToDp(context, r), convertPxToDp(context, b));
            v.requestLayout();
        }
    }

    public static int getDisplayWidth(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static int getDisplayHeight(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels;
    }

    public static String getIntroImageSpec(Context context) {
        if (context.getResources().getDisplayMetrics().densityDpi >= DisplayMetrics.DENSITY_XXHIGH) {
            return TYPE_2;
        } else {
            return TYPE_1;
        }
    }


    /**
     * 색상
     */
    public static final int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context,id);
        } else {
            return context.getResources().getColor(id);
        }
    }


}