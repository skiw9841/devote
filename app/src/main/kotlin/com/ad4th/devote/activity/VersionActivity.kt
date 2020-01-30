package com.ad4th.devote.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import com.ad4th.devote.R
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.utils.DeviceUtil
import org.w3c.dom.Text

class VersionActivity : AppCompatActivity() {
    var current_version : TextView?=null
    var new_version : TextView?=null
    var update_btn : Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_version)

        current_version = findViewById(R.id.current_version_tv)
        new_version = findViewById(R.id.new_version_tv)
        update_btn = findViewById(R.id.update_btn)


        current_version!!.text = DeviceUtil.getAppVersion(this@VersionActivity)
        new_version!!.text = DevoteApplication.metaData!!.version.latest

        update_btn!!.setOnClickListener{
            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ad4th.devote"))
            startActivity(intent)
        }

        findViewById<ImageButton>(R.id.imageButtonLeft).setOnClickListener { onBackPressed() }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, VersionActivity::class.java)
            return intent
        }
    }
}
