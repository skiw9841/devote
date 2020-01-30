package com.ad4th.devote.utils

import android.content.Context
import android.content.pm.PackageManager

object DeviceUtil {
    /**
     * 앱 버전 가져오기
     */
    fun getAppVersion(ctx: Context): String {
        var version = ""
        try {
            val i = ctx.packageManager.getPackageInfo(ctx.packageName, 0)
            version = i.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return version
    }

}
