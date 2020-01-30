package com.ad4th.devote.utils.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat

object PermissionUtil {
    // 위치정보 퍼미션
    private val ACCESS_FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION
    // 카메라 퍼미션
    private val CAMERA = android.Manifest.permission.CAMERA

    /**
     * 현재 버전이 마쉬멜로우 이상 버전인지 체크 하여 반환
     */
    val isVersionMarshmallow: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    /**
     * 퍼미션 권한 습득 여부 반환
     *
     * @param context    컨텍스트
     * @param permission 체크해야할 대상 퍼미션
     */
    fun checkSelfPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 위치정보 권한이 있는지 반환 (feat. 마쉬멜로우인 경우만 이하버전은 무조건 true)
     *
     * @param context 컨텍스트
     */
    fun hasLocationGranted(context: Context): Boolean {
        return if (isVersionMarshmallow) checkSelfPermission(context, ACCESS_FINE_LOCATION) else true
    }

    /**
     * 카메라 권한이 있는지 반환 (feat. 마쉬멜로우인 경우만 이하버전은 무조건 true)
     *
     * @param context 컨텍스트
     */
    fun hasCameraGranted(context: Context): Boolean {
        return if (isVersionMarshmallow) checkSelfPermission(context, CAMERA) else true
    }
}