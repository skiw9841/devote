package com.ad4th.devote.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.ad4th.devote.R
import com.ad4th.devote.base.BaseActivity
import com.ad4th.devote.utils.DeviceUtil
import com.ad4th.devote.utils.IconUtil
import android.widget.Toast



class MenuActivity : BaseActivity() {

    var mywallet_layout : LinearLayout?= null
    var version_layout : LinearLayout?= null
    var protection_layout : LinearLayout?= null
    var menu_icx_tv : TextView? = null
    var menu_version_tv : TextView? = null
    var isWallet : Boolean? = false
    var x1: Float = 0.toFloat()
    var x2: Float = 0.toFloat()
    val MIN_DISTANCE = 150


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        mywallet_layout = findViewById(R.id.mywallet_layout)
        version_layout = findViewById(R.id.version_layout)
        protection_layout = findViewById(R.id.protection_layout)
        menu_icx_tv = findViewById(R.id.menu_icx_tv)
        menu_version_tv = findViewById(R.id.menu_version_tv)

        mywallet_layout!!.setOnClickListener{
            if( mUserManager.getData().walletAddress != null )
                startActivity(MyActivity.newIntent( this@MenuActivity))
            else
                startActivity(WalletActivity.newIntent( this@MenuActivity))
        }
        version_layout!!.setOnClickListener{
            startActivity(VersionActivity.newIntent( this@MenuActivity))
        }
        protection_layout!!.setOnClickListener{
            startActivity(ProtectionActivity.newIntent( this@MenuActivity))
        }

        // ICX
        if( mUserManager.getData().walletAddress != null ) {

            // 9월행사대응
            networkWalletBalance(mUserManager.getData().walletAddress!!)
            //menu_icx_tv!!.text = ""+ IconUtil.TEMP_ICX + " ICX"
            //

            isWallet = true
        }
        else {
            menu_icx_tv!!.text = getText(R.string.non_wallet)//"지갑이 없습니다." // don't create wallet
            isWallet = false
        }
        // VERSION
        menu_version_tv!!.text = DeviceUtil.getAppVersion(this@MenuActivity)

    }

    @SuppressLint("ResourceType")
    override fun onResume() {
        super.onResume()

        if( isWallet == false && mUserManager.getData().walletAddress != null  )
        {
            finish()
        }
    }

    fun networkWalletBalance(address:String) {

        IconUtil.WalletBalance(address, object : IconUtil.WalletCallback(this@MenuActivity) {

            override fun successed(result: String) {

                menu_icx_tv!!.text = IconUtil.formatIcx(result) + " ICX"
                super.successed(result)
            }
        }).start()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if( event!!.action == MotionEvent.ACTION_DOWN ) {
            x1 = event.getX();
        }
        else if( event!!.action == MotionEvent.ACTION_UP ) {
            x2 = event.x
            val deltaX = x2 - x1
            if ( deltaX > MIN_DISTANCE ) {
                finish()
            } else {
              }
        }
        return super.onTouchEvent(event)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, MenuActivity::class.java)
            return intent
        }
    }
}
