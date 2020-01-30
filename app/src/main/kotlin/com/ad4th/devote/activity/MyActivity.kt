package com.ad4th.devote.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.ad4th.devote.R
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseActivity
import com.ad4th.devote.config.CODE
import com.ad4th.devote.utils.IconUtil
import com.ad4th.devote.utils.StringUtil

/**
 * My Activity
 */
class MyActivity : BaseActivity() {


    var textViewBalance: TextView? = null
    var textViewPublicKey: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)
        init()
    }

    /**
     * 초기화
     */
    fun init() {

        textViewBalance = findViewById(R.id.textViewBalance)
        textViewPublicKey = findViewById(R.id.textViewPublicKey)

        val address = mUserManager.getData().walletAddress!!

        textViewPublicKey!!.text = address

        /* 테스트넷 여부에 따른 안내 */
        if(DevoteApplication.event!!.networkType.equals(CODE.NET_TYPE_TEST, true)){
            findViewById<TextView>(R.id.textViewTestNet).visibility= View.VISIBLE
        } else {
            findViewById<TextView>(R.id.textViewTestNet).visibility= View.INVISIBLE
        }

        // 뒤로가기
        findViewById<ImageButton>(R.id.imageButtonLeft).setOnClickListener { finish() }

        // 주소 복사
        findViewById<ViewGroup>(R.id.layoutAddressCopy).setOnClickListener {
            StringUtil.setClipboard(this,address)
            Toast.makeText(this, getString(R.string.wallet_address_copy), Toast.LENGTH_SHORT).show()
        }

        // 툴바 닫기 버튼
        findViewById<ImageButton>(R.id.imageButtonRight).setOnClickListener { finish() }


        // 잔액 조회
        networkWalletBalance(address)
    }

    /**
     * 잔액 조회
     */
    fun networkWalletBalance(address:String) {

        IconUtil.WalletBalance(address, object : IconUtil.WalletCallback(this@MyActivity) {

            override fun successed(result: String) {

                textViewBalance!!.text = IconUtil.formatIcx(result)
                super.successed(result)
            }
        }).start()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, MyActivity::class.java)
            return intent
        }
    }

}
