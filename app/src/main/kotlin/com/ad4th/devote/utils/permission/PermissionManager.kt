package com.ad4th.devote.utils.permission


import android.Manifest
import android.app.Activity
import com.ad4th.devote.R
import com.ad4th.devote.utils.MaterialDialogUtil
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.*

class PermissionManager private constructor() : PermissionListener {
    private var mActivity: Activity? = null
    // 예외처리 대상 퍼미션
    private var mIgnorePermissions: Array<String>? = null
    //  대상 퍼미션
    private var mPermissions: Array<String>? = null
    // 거부 처리 대상  퍼미션
    private var mApplyPermissions: MutableMap<String, Boolean>? = null
    val permissions: Map<String, Boolean>?
        get() = mApplyPermissions

    /**
     * 현재 컨텍스트를 가져온다 (현재 화면에 얼럿을 띄우기 위함)
     */
    fun setContext(context: Activity): PermissionManager {
        permissionManager!!.mActivity = context
        return this
    }

    /**
     * 체크 거부 이후 처리 #onPermissionDenied 예외 대상이 있는 경우 setPermission 보다 먼저 선행해야함
     */
    fun setIgnore(vararg ignorePermissions: String): PermissionManager {
        permissionManager!!.mIgnorePermissions = ignorePermissions as Array<String>
        return this
    }

    /**
     * 체크할 퍼미션을 추가한다
     */
    fun setPermission(vararg permissions: String): PermissionManager {
        permissionManager!!.mPermissions = permissions as Array<String>
        // step 0. 기존 퍼미션 정보 초기화
        clear()
        // step 1. onPermissionDenied 에서 관리할 퍼미션 추가
        for (permission in permissions) {
            var isLoop = true
            // 예외 대상 퍼미션이 있는지 체크 하고 있으면 무시
            for (ignorePermission in mIgnorePermissions!!) {
                if (ignorePermission == permission) {
                    /* 예외 처리해야할 퍼미션이 있는 경우 무시한다 */
                    isLoop = false
                    break
                }
            }
            if (isLoop) {
                mApplyPermissions!![permission] = true
            }
        }
        return this
    }

    /**
     * 퍼미션 정보 초기화
     */
    fun clear() {
        if (mApplyPermissions == null) {
            mApplyPermissions = HashMap()
        } else {
            mApplyPermissions!!.clear()
        }
    }

    /**
     * 퍼미션 체크 시작 프로세스
     */
    fun check() {
        // step 2. 테드 퍼미션 체크시작
        TedPermission.with(mActivity!!)
                .setPermissionListener(this)
                .setPermissions(*mPermissions!!)
                .check()
    }

    override fun onPermissionGranted() {}
    override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
        val permissionMap = permissions
        loop@ for (permission in deniedPermissions) {
            val hasPermission = permissionMap!!.containsKey(permission)
            if (hasPermission) {
                when (permission) {
                /* 카메라 권한 요청 */
                    Manifest.permission.CAMERA -> {
                        MaterialDialogUtil.showPermissionDeniedAlert(mActivity, null, mActivity!!.getString(R.string.alert_permission_camera), {
                            mActivity!!.finish()
                        })
                        continue@loop
                    }
                }
            }
        }
    }

    companion object {
        private val TAG = PermissionManager::class.java.simpleName
        private var permissionManager: PermissionManager? = null
        val instance: PermissionManager
            get() {
                synchronized(PermissionManager::class.java) {
                    if (permissionManager == null) {
                        permissionManager = PermissionManager()
                    }
                }
                return permissionManager!!
            }
    }
}