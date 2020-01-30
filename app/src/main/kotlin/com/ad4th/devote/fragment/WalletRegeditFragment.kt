package com.ad4th.devote.fragment

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.ad4th.devote.R
import com.ad4th.devote.activity.WalletActivity
import com.ad4th.devote.activity.WalletRegeditFinishActivity
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseFragment
import com.ad4th.devote.config.URI
import com.ad4th.devote.model.UserResVo
import com.ad4th.devote.model.UserVo
import com.ad4th.devote.network.RetroCallback
import com.ad4th.devote.utils.MaterialDialogUtil
import com.ad4th.devote.utils.SharedProperty
import com.ad4th.devote.utils.StringUtil
import com.ad4th.devote.utils.ValidationUtil


/**
 * 지갑 등록 Fragment
 */
class WalletRegeditFragment : BaseFragment() {


    internal lateinit var view: View

    var editTextAddress: EditText? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // 상태바 색상 변경(WHITE)
        if (Build.VERSION.SDK_INT >= 21) {
            getmActivity()!!.window.statusBarColor = Color.BLACK
            getmActivity()!!.window.decorView.systemUiVisibility = 0
        }


        view = inflater.inflate(R.layout.fragment_wallet_regedit, null)
        init()
        return view
    }

    /**
     * 초기화
     */
    fun init() {

        //화면 리사이즈 설정
        getmActivity()!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        editTextAddress = view.findViewById(R.id.editTextAddress)

        view.findViewById<ImageButton>(R.id.imageButtonLeft).setOnClickListener {
            (getmActivity() as WalletActivity).replaceFragment(WalletActivity.CODE_WALLET_DISPLAY)

        }


        /* 지갑등록 버튼 */
        view.findViewById<Button>(R.id.buttonJoin).setOnClickListener {

            val strAddress = editTextAddress!!.text.toString()
            val errorMsgCode = ValidationUtil.walletAddress(strAddress)
            if(errorMsgCode != -1) {
                getmActivity()?.handler?.post({
                    MaterialDialogUtil.alert(getmActivity() as Context, getString(errorMsgCode)).show()
                })
            } else {
                val vo = getmActivity()!!.mUserManager.getData()
                vo.eventId = DevoteApplication.event!!.id
                vo.walletAddress = strAddress
                vo.networkType = DevoteApplication.event!!.networkType
                vo.pushToken = getmActivity()!!.mSharedProperty.getValue(SharedProperty.KEY_PUSH_TOKEN,"")
                getmActivity()!!.mUserManager.setData(vo)

                // 지갑등록 요청
                networkPostInsertUser(StringUtil.replace(URI.URL_EVENT_USER, DevoteApplication.event!!.id!!), vo)

            }
        }
    }

    /**`
     * 지갑/유저 등록
     */
    private fun networkPostInsertUser(url: String, userVo: UserVo) {
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
                    getmActivity()!!.mUserManager.setData(vo)
                    getmActivity()!!.finish()
                } else {
                    getmActivity()!!.mUserManager.setData(vo)
                    getmActivity()!!.startActivity(WalletRegeditFinishActivity.newIntent(getmActivity() as Context))
                    getmActivity()!!.finish()
                }
            }

            override fun onFailure(code: Int) {
                super.onFailure(code)
            }
        })
    }
}