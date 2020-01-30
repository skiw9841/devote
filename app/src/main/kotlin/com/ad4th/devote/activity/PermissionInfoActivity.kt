package com.ad4th.devote.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.ad4th.devote.R
import com.ad4th.devote.base.BaseActivity
import com.ad4th.devote.utils.AnimUtil
import com.ad4th.devote.utils.BlockerActivityUtil


/**
 * 퍼미션 안내 Activity
 */
class PermissionInfoActivity : BaseActivity() {

    private var mBlockerParam: BlockerActivityUtil.BlockerActivityResult? = null
    private var receivedIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AnimUtil.left(this)
        setContentView(R.layout.activity_permission_info)
        init()
    }

    /**
     * 초기화
     */
    fun init() {
        receivedIntent = getIntent();
        /* Blocker Parameter */
        mBlockerParam = BlockerActivityUtil.getParam(this, receivedIntent)

        // 확인 버튼
        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            finish()
        }

    }

    override fun onBackPressed() {
        // 뒤로가기 이벤트 무효처리
//        super.onBackPressed()
    }

    override fun finish() {
        super.finish()
        AnimUtil.fade(this)
        BlockerActivityUtil.finishBlockerActivity(mBlockerParam)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, PermissionInfoActivity::class.java)
            return intent
        }
    }
}
