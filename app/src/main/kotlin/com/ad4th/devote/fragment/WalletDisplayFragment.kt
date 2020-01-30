package com.ad4th.devote.fragment

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import com.ad4th.devote.R
import com.ad4th.devote.activity.WalletActivity
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseFragment
import com.ad4th.devote.config.CODE

/**
 * 지갑 메인 Fragment
 */
class WalletDisplayFragment : BaseFragment() {


    internal lateinit var view: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // 상태바 색상 변경(WHITE)
        if (Build.VERSION.SDK_INT >= 21) {
            getmActivity()!!.window.statusBarColor = Color.BLACK
            getmActivity()!!.window.decorView.systemUiVisibility = 0
        }

        view = inflater.inflate(R.layout.fragment_wallet_display, null)
        init()
        return view;
    }

    /**
     * 초기화
     */
    fun init() {


        /* 테스트넷 여부에 따른 안내 */
        if(DevoteApplication.event!!.networkType.equals(CODE.NET_TYPE_TEST, true)){
            val buttonTestWalletCreate = view.findViewById<Button>(R.id.buttonTestWalletCreate)
            buttonTestWalletCreate.visibility = View.VISIBLE
            buttonTestWalletCreate.setOnClickListener {
                (getmActivity() as WalletActivity).replaceFragment(WalletActivity.CODE_WALLET_CREATE_STEP1)
            }

            view.findViewById<LinearLayout>(R.id.layoutMainBottom).visibility = View.GONE
        } else {

            view.findViewById<Button>(R.id.buttonTestWalletCreate).visibility = View.GONE
            view.findViewById<LinearLayout>(R.id.layoutMainBottom).visibility = View.VISIBLE

            /* 지갑 등록 버튼 */
            view.findViewById<Button>(R.id.buttonWalletRegedit).setOnClickListener {
                (getmActivity() as WalletActivity).replaceFragment(WalletActivity.CODE_WALLET_REGEDIT)
            }
        }

        //화면 리사이즈 설정
        getmActivity()!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        /* 지갑 만들기 버튼 */
        view.findViewById<Button>(R.id.buttonWalletCreate).setOnClickListener {
            (getmActivity() as WalletActivity).replaceFragment(WalletActivity.CODE_WALLET_CREATE_STEP1)
        }


    }


}
