package com.ad4th.devote.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.ad4th.devote.R
import com.ad4th.devote.activity.WalletActivity
import com.ad4th.devote.activity.WalletRegeditFinishActivity
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseFragment
import com.ad4th.devote.config.CODE
import com.ad4th.devote.config.URI
import com.ad4th.devote.model.UserResVo
import com.ad4th.devote.model.UserVo
import com.ad4th.devote.model.WalletVo
import com.ad4th.devote.network.RetroCallback
import com.ad4th.devote.utils.MaterialDialogUtil
import com.ad4th.devote.utils.SharedProperty
import com.ad4th.devote.utils.StringUtil


/**
 * 지갑 생성-Step2 Fragmet
 */
@SuppressLint("ValidFragment")
class WalletCreateStep2Fragment constructor(private val walletVo: WalletVo) : BaseFragment(){

    internal lateinit var view: View

    private var textViewAddress : TextView? = null
    private var textViewPrivateKey : TextView? = null

    private var privateKeySaveYn : Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        view = inflater.inflate(R.layout.fragment_wallet_create_step2, null)
        init()
        return view
    }

    /**
     * 초기화
     */
    fun init() {

        // 상태바 색상 변경(WHITE)
        if (Build.VERSION.SDK_INT >= 21) {
            getmActivity()!!.window.statusBarColor = Color.WHITE
            getmActivity()!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        //화면 리사이즈 설정
        getmActivity()!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        textViewAddress = view.findViewById(R.id.textViewAddress)
        textViewPrivateKey = view.findViewById(R.id.textViewPrivateKey)

        textViewAddress?.text = walletVo.address + ""
        textViewPrivateKey?.text = walletVo.privateKey + ""


        /* Toolbar 닫기 버튼 */
        view.findViewById<ImageButton>(R.id.imageButtonRight).setOnClickListener {
            getmActivity()!!.finish()
        }

        /* 주소 복사하기 버튼
        view.findViewById<ImageButton>(R.id.imageButtonAddressCopy).setOnClickListener {
            StringUtil.setClipboard(getmActivity() as Context, walletVo.address + "")
            Toast.makeText(getmActivity(), getString(R.string.wallet_address_copy), Toast.LENGTH_SHORT).show()
        }
        */

        /* Private Key 복사하기 버튼 */
        view.findViewById<Button>(R.id.buttonPrivateCopy).setOnClickListener {

            // 클립보드 복사
//            StringUtil.setClipboard(getmActivity() as Context, walletVo.privateKey + "")
//            Toast.makeText(getmActivity(), getString(R.string.wallet_privateKey_copy), Toast.LENGTH_SHORT).show()
            privateKeySaveYn = true
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, walletVo.privateKey)
            sendIntent.type = "text/plain"
            getmActivity()!!.startActivity(Intent.createChooser(sendIntent, getString(R.string.wallet_privateKey_save)))

        }

        /* 지갑 등록 버튼 */
        view.findViewById<Button>(R.id.buttonReg).setOnClickListener {

            if(!privateKeySaveYn) {
                MaterialDialogUtil.alert(getmActivity()!!, getString(R.string.alert_privateKey_not_save)).show()
                return@setOnClickListener
            }

            MaterialDialogUtil.alert(getmActivity()!!, 0
                    , R.string.alert_privateKey_save_check
                    , R.string.no
                    ,  R.string.yes
                    , {}
                    , {

                val vo = getmActivity()!!.mUserManager.getData()
                vo.eventId = DevoteApplication.event!!.id
                vo.networkType = DevoteApplication.event!!.networkType
                vo.walletAddress = walletVo.address
                vo.pushToken = getmActivity()!!.mSharedProperty.getValue(SharedProperty.KEY_PUSH_TOKEN,"")
                getmActivity()!!.mUserManager.setData(vo)

                // 지갑등록 요청
                networkPostInsertUser(StringUtil.replace(URI.URL_EVENT_USER, DevoteApplication.event!!.id!!), vo)

            }).show()

        }
    }

    /**`
     * 지갑/유저 등록
     */
    private fun networkPostInsertUser(url : String, userVo: UserVo) {
        getmActivity()!!.mRetroClient.postInsertUser(url, userVo, object : RetroCallback<Any>(getmActivity()) {
            override fun onError(t: Throwable) {
                super.onError(t)
            }

            override fun onSuccess(code: Int, receivedData: Any?) {
                super.onSuccess(code, receivedData)

                val vo = getmActivity()!!.mUserManager.getData()
                val userResVo = receivedData as UserResVo
                vo.id = userResVo.id
                if(userResVo.qrCode != null) {
                    vo.code= userResVo.qrCode!!.code
                }

                getmActivity()!!.mUserManager.setData(vo)
                getmActivity()!!.startActivity(WalletRegeditFinishActivity.newIntent(getmActivity() as Context))
                getmActivity()!!.finish()
            }

            override fun onFailure(code: Int) {
                super.onFailure(code)
            }
        })
    }

}