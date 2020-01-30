package com.ad4th.devote.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.ad4th.devote.R
import com.ad4th.devote.base.BaseActivity
import com.ad4th.devote.config.CODE
import com.ad4th.devote.fragment.WalletCreateStep1Fragment
import com.ad4th.devote.fragment.WalletDisplayFragment
import com.ad4th.devote.fragment.WalletRegeditFragment


/**
 * 지갑 Activity
 */
class WalletActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        init()
    }

    /**
     * 초기화
     */
    fun init() {

        // 상태바 색상 변경(WHITE)
        if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        // 기본 디스플레이 노출
        replaceFragment(CODE_WALLET_CREATE_STEP1)
    }

    /**
     *  프레그먼트 가져오기
     */
    private fun getFragment(idx: Int): Fragment {
        var newFragment: Fragment? = null
        when (idx) {
            CODE_WALLET_DISPLAY -> newFragment = WalletDisplayFragment()
            CODE_WALLET_CREATE_STEP1 -> newFragment = WalletCreateStep1Fragment()
            //CODE_WALLET_CREATE_STEP2 -> newFragment = WalletCreateStep2Fragment()
            CODE_WALLET_REGEDIT -> newFragment = WalletRegeditFragment()
        }
        return newFragment!!
    }

    /**
     * 프레그먼트 변경
     */
    fun replaceFragment(fragment: Any) {

        var newFragment: Fragment = if(fragment is Fragment) (fragment as Fragment) else getFragment((fragment as Int))
        val manager = supportFragmentManager
        val newStateName = newFragment!!.javaClass.name
        var currentStateName = ""
        val fragmentsCnt = manager.backStackEntryCount

        if (fragmentsCnt > 0) {
            currentStateName = manager.getBackStackEntryAt(fragmentsCnt - 1).name
        }
        // 현재 활성화 되어있는 fragment 와 동일 할 경우 pass 한다.
        if (currentStateName == newStateName) return

        try {
            val fragmentPopped = manager.popBackStackImmediate(newStateName, 0)
            if (!fragmentPopped) { //fragment not in back stack, create it.
                var transaction = manager.beginTransaction()
                if(newStateName != WalletCreateStep1Fragment::class.java.name)
                    transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left)

                    transaction.replace(R.id.layout, newFragment)
                            .addToBackStack(newStateName)
                            .commitAllowingStateLoss()
            }
        } catch (ignored: IllegalStateException) {
        }

    }

    override fun onBackPressed() {
        // 현재 WalletDisplayFragment에서 Back 이벤트 발생 시 앱 종료
        val fragmentsCnt = supportFragmentManager.backStackEntryCount
        if (fragmentsCnt > 0) {
            if(supportFragmentManager.getBackStackEntryAt(fragmentsCnt - 1).name == WalletCreateStep1Fragment::class.java.name) {
                finish()
                return
            }
        }
        super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE.ACTIVITY_CODE_QR && resultCode == CODE.RESULT_CODE_QR_SUCCESS) {
            startActivity(VotingActivity.newIntent(this@WalletActivity))
            finish()
        }
    }

    companion object {
        val CODE_WALLET_DISPLAY = 0x012             // 지갑 메인화면
        val CODE_WALLET_CREATE_STEP1 = 0x013        // 지갑 생성-STEP1
        val CODE_WALLET_CREATE_STEP2 = 0x014        // 지갑 생성-STEP2
        val CODE_WALLET_REGEDIT = 0x015             // 지갑 등록

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, WalletActivity::class.java)
            return intent
        }
    }
}

