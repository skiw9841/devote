package com.ad4th.devote.activity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.widget.ImageButton
import com.ad4th.devote.R

class ProtectionActivity : AppCompatActivity() {

    var wv : WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_protection)

        wv = findViewById(R.id.protection_wv)
        wv!!.loadUrl("https://s3.ap-northeast-2.amazonaws.com/files.ad4th.com/devote/privacyPolicy.html")

        findViewById<ImageButton>(R.id.imageButtonLeft).setOnClickListener { onBackPressed() }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, ProtectionActivity::class.java)
            return intent
        }
    }
}
