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
import android.widget.TextView
import com.ad4th.devote.R
import com.ad4th.devote.activity.WalletActivity
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseFragment
import com.ad4th.devote.config.CODE
import com.ad4th.devote.model.WalletVo
import com.ad4th.devote.utils.CustomInputFilter
import com.ad4th.devote.utils.IconUtil
import com.ad4th.devote.utils.MaterialDialogUtil
import com.ad4th.devote.utils.ValidationUtil
import com.google.gson.Gson
import com.voting.kotlin.utils.LogUtil


/**
 * 지갑 생성-Step1 Fragmet
 */
class WalletCreateStep1Fragment : BaseFragment() {

    internal lateinit var view: View

    private var editTextPassword :EditText? = null
    private var editTextPasswordConfirm :EditText? = null;

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // 상태바 색상 변경(WHITE)
        if (Build.VERSION.SDK_INT >= 21) {
            getmActivity()!!.window.statusBarColor = Color.WHITE
            getmActivity()!!.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        view = inflater.inflate(R.layout.fragment_wallet_create_step1, null)
        init()
        return view;
    }

    /**
     * 초기화
     */
    fun init() {
        //화면 리사이즈 설정
        getmActivity()!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)
        editTextPasswordConfirm = view.findViewById<EditText>(R.id.editTextPasswordConfirm)
        // 공백 입력 제한
        editTextPassword!!.setFilters(arrayOf(CustomInputFilter.noSpace))
        editTextPasswordConfirm!!.setFilters(arrayOf(CustomInputFilter.noSpace))

        /* 테스트넷 여부에 따른 안내 */
        if(DevoteApplication.event!!.networkType.equals(CODE.NET_TYPE_TEST, true)){
            view.findViewById<TextView>(R.id.textViewTestNet).visibility= View.VISIBLE
        } else {
            view.findViewById<TextView>(R.id.textViewTestNet).visibility= View.INVISIBLE
        }

        /* Toolbar 닫기 버튼 */
        view.findViewById<ImageButton>(R.id.imageButtonRight).setOnClickListener {
            getmActivity()!!.finish()
        }

        /* 다음 단계 버튼 */
        view.findViewById<Button>(R.id.buttonNext).setOnClickListener {
            if (validationPassword()) iconWalletCreate(editTextPasswordConfirm?.text.toString().trim())
        }
    }

    /**
     * 지갑생성 요청
     */
    fun iconWalletCreate(pwd : String){

        IconUtil.WalletCreate(pwd, object : IconUtil.WalletCallback(getmActivity()) {

            override fun successed(result: String) {

                var datas : Array<String> = Gson().fromJson(result, Array<String>::class.java)
                var vo = WalletVo(address = datas[0], keyStore = datas[1], privateKey = datas[2])
                (getmActivity() as WalletActivity).replaceFragment(WalletCreateStep2Fragment(vo))

                super.successed(result)


                LogUtil.e("TAGS", result)
            }
        }).start()
    }


    /**
     * 비밀번호 validation
     */
    private fun validationPassword() : Boolean{

        val strPassword :String = editTextPassword?.text.toString().trim()
        val strPasswordConfirm :String = editTextPasswordConfirm?.text.toString().trim()


        val vo = WalletVo(pwd=strPassword, pwdConfirm=strPasswordConfirm)
        val errorMsgCode : Int = ValidationUtil.walletPassword(vo)
        if(errorMsgCode != -1) {
            getmActivity()?.handler?.post(Runnable {
                MaterialDialogUtil.alert(getmActivity() as Context, getString(errorMsgCode)).show()
            })
            return false
        } else {
            return true
        }
    }

}