package com.ad4th.devote.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import android.widget.ImageButton
import com.ad4th.devote.R
import com.ad4th.devote.base.BaseActivity
import com.ad4th.devote.config.CODE
import com.ad4th.devote.eventbus.MainEvent
import com.ad4th.devote.fragment.VotingDisplayFragment
import com.ad4th.devote.fragment.VotingResultFragment
import com.ad4th.devote.fragment.VotingTargetFragment
import com.ad4th.devote.widget.CustomToolbar
import de.greenrobot.event.EventBus

/**
 * 지갑 Activity
 */
class VotingActivity : BaseActivity() {
    var customToolbar: CustomToolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voting)
        init()
    }

    /**
     * 초기화
     */
    fun init() {
        customToolbar = findViewById(R.id.customToolbar)

        // 상태바 색상 변경(WHITE)
        if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        }

        // push result
        var pushResult = getIntent().getBooleanExtra("PUSH_RESULT", false)
        if( pushResult )
        {
            replaceFragment(CODE_VOTING_RESULT)
        }
        else {
            //기본 디스플레이 노출
            replaceFragment(CODE_VOTING_DISPLAY)
        }

        findViewById<ImageButton>(R.id.imageButtonLeft).setOnClickListener { onBackPressed() }
//        findViewById<ImageButton>(R.id.imageButtonRight).setOnClickListener { startActivity(MyActivity.newIntent(this)) }
        findViewById<ImageButton>(R.id.imageButtonRight).setOnClickListener { startActivity(MenuActivity.newIntent(this)) }

    }

    /**
     *  프레그먼트 가져오기
     */
    private fun getFragment(idx: Int): Fragment {
        var newFragment: Fragment? = null
        when (idx) {
            CODE_VOTING_DISPLAY -> newFragment = VotingDisplayFragment()
            CODE_VOTING_TARGET -> newFragment = VotingTargetFragment()
            //CODE_VOTING_CONTINU -> newFragment = WalletCreateStep2Fragment()
            CODE_VOTING_RESULT -> newFragment = VotingResultFragment()
        }
        return newFragment!!
    }

    /**
     * 프레그먼트 변경
     */
    fun replaceFragment(fragmentId: Int) {
        var newFragment: Fragment = getFragment(fragmentId)

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
                manager.beginTransaction()
                        .replace(R.id.layout, newFragment)
                        .addToBackStack(newStateName)
                        .commitAllowingStateLoss()
            }
        } catch (ignored: IllegalStateException) {}
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE.ACTIVITY_CODE_VOTING && resultCode == CODE.RESULT_CODE_VOTING_DONE) {
            replaceFragment(CODE_VOTING_DISPLAY)
        }
    }


    override fun onBackPressed() {
        // 현재 WalletDisplayFragment에서 Back 이벤트 발생 시 앱 종료
        val fragmentsCnt = supportFragmentManager.backStackEntryCount
        if (fragmentsCnt > 0) {
            if(supportFragmentManager.getBackStackEntryAt(fragmentsCnt - 1).name == VotingDisplayFragment::class.java.name) {
                finish()
                return
            }
        }
        super.onBackPressed()
    }


    companion object {
        val CODE_VOTING_DISPLAY = 0x012               // 투표 디스플레이
        val CODE_VOTING_TARGET  = 0x013               // 투표 대상
        val CODE_VOTING_CONTINU = 0x014               // 투표 진행
        val CODE_VOTING_RESULT = 0x015                // 투표 완료

        fun newIntent(context: Context): Intent {
            val intent = Intent(context, VotingActivity::class.java)
            return intent
        }
    }
}

