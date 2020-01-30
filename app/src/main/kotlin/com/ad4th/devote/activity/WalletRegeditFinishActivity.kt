package com.ad4th.devote.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import android.widget.TextView
import com.ad4th.devote.R
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseActivity
import com.ad4th.devote.config.CODE
import com.ad4th.devote.model.QrCodeVo
import com.ad4th.devote.network.RetroCallback
import com.ad4th.devote.utils.*
import com.ad4th.devote.utils.permission.PermissionUtil
import com.ad4th.devote.widget.CustomToolbar
import com.dlazaro66.qrcodereaderview.QRCodeReaderView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.*


/**
 * 지갑 등록 완료  Activity
 */
class WalletRegeditFinishActivity : BaseActivity(), QRCodeReaderView.OnQRCodeReadListener {


    private var isScan = false
    private var qrCodeReaderView: QRCodeReaderView? = null

    override fun onQRCodeRead(text: String?, points: Array<out PointF>?) {
        if(!isScan) {
            isScan = true
            stopQrScan()
            var resultCode: String = text!!
            val qrcodeVo = QrCodeVo(eventId = DevoteApplication.event!!.id!!, userId = mUserManager.getData().id, code = resultCode)
            networkPostInsertUser(qrcodeVo)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnimUtil.left(this)
        setContentView(R.layout.activity_wallet_regedit_finish)
        init()
    }

    /**
     * 초기화
     */
    fun init() {
        // 퍼미션 요청 {카메라} 필수!
        permissionCheck()
        // QR코드
        initQrScan()
        // ICX 지급안내 문구
        findViewById<TextView>(R.id.textViewInfo).text = StringUtil.replace(getString(R.string.wallet_address_reg_finish_info2), DevoteApplication.event!!.icxQuantity.toString())
        // 툴바 닫기 버튼
        findViewById<CustomToolbar>(R.id.customToolbar).imageButtonRight.setOnClickListener { finish() }
    }

    /**
     * 필수 퍼미션 체크
     */
    fun permissionCheck() {
        var hasCameraPermission = PermissionUtil.hasCameraGranted(this)
        if (!hasCameraPermission) {
            // 카메라 퍼미션 체크
            TedPermission.with(this)
                    .setPermissions(Manifest.permission.CAMERA)
                    .setPermissionListener(object : PermissionListener {
                override fun onPermissionGranted() {
                    // QR코드
                    finish()
                    startActivity(newIntent(this@WalletRegeditFinishActivity))
                }

                override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                    MaterialDialogUtil.showPermissionDeniedAlert(this@WalletRegeditFinishActivity, null, getString(R.string.alert_permission_camera), {
                        finish()
                    })
                }
            }).check()
        }
    }

    /**
     * QR코드 init
     */
    fun initQrScan() {

        qrCodeReaderView = findViewById(R.id.qrdecoderview)

        qrCodeReaderView!!.setOnQRCodeReadListener(this)
        // Use this function to enable/disable decoding
        qrCodeReaderView!!.setQRDecodingEnabled(true)
        // Use this function to change the autofocus interval (default is 5 secs)
        qrCodeReaderView!!.setAutofocusInterval(2000L)
        // Use this function to enable/disable Torch
        qrCodeReaderView!!.setTorchEnabled(true)
        // Use this function to set front camera preview
        qrCodeReaderView!!.setFrontCamera()
        // Use this function to set back camera preview
        qrCodeReaderView!!.setBackCamera()
    }

    /**
     * Qr코드 등록
     */
    private fun networkPostInsertUser(qrCodeVo: QrCodeVo) {
        mRetroClient.postValidationWithRegeditQrCode(qrCodeVo, object : RetroCallback<Any>(this) {
            override fun onError(t: Throwable) {
                super.onError(t)
                startQrScan()
            }

            override fun onSuccess(code: Int, receivedData: Any?) {
                super.onSuccess(code, receivedData)

                if (receivedData != null) {
                    val vo = receivedData as QrCodeVo
                    if (vo.success) {
                        // 등록 성공 시
                        val userVo = mUserManager.getData()
                        userVo.code = qrCodeVo.code
                        mUserManager.setData(userVo)

                        MaterialDialogUtil.customDialog(this@WalletRegeditFinishActivity
                                , getString(R.string.voting_completed)
                                , StringUtil.replace(getString(R.string.alert_walllet_done), DevoteApplication.event!!.icxQuantity!!)
                                , getString(R.string.confirm)
                                , {

                            setResult(CODE.RESULT_CODE_QR_SUCCESS)
                            finish()
                            startActivity(VotingActivity.newIntent(this@WalletRegeditFinishActivity))
                        }).show()

                        // 9월행사대응
                        //IconUtil.TEMP_ICX += DevoteApplication.event!!.icxQuantity!!
                        //mSharedProperty.put(SharedProperty.KEY_TEMP_ICX, IconUtil.TEMP_ICX)

                    } else {
                        //등록 실패 시
                        MaterialDialogUtil.errorAlert(this@WalletRegeditFinishActivity, getString(R.string.qrcode_not_valid), { startQrScan() }).show()
                    }
                } else {
                    MaterialDialogUtil.errorAlert(this@WalletRegeditFinishActivity, getString(R.string.alert_network_error), { startQrScan() }).show()
                }
            }

            override fun onFailure(code: Int) {
                super.onFailure(code)
                startQrScan()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!isScan) startQrScan()
    }

    override fun onPause() {
        super.onPause()
        stopQrScan()
    }

    /**
     * QR코드 스캔 Stop
     */
    fun stopQrScan() {
        qrCodeReaderView!!.stopCamera()
    }

    /**
     * QR코드 스캔 Start
     */
    fun startQrScan() {
        isScan = false
        qrCodeReaderView!!.startCamera()
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var hasCameraPermission = PermissionUtil.hasCameraGranted(this)
        if (!hasCameraPermission) {
            finish()
        }
    }

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, WalletRegeditFinishActivity::class.java)
        }
    }
}

