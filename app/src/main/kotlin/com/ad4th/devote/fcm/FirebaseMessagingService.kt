package com.ad4th.devote.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.support.v4.app.NotificationCompat
import com.ad4th.devote.R
import com.ad4th.devote.activity.IntroActivity
import com.google.firebase.messaging.RemoteMessage
import com.voting.kotlin.utils.LogUtil
import java.util.concurrent.atomic.AtomicInteger


class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    // [START receive_message]
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        try {
            if(remoteMessage!!.data.isNotEmpty()) {
                sendPushNotification(remoteMessage!!.data["title"], remoteMessage!!.data["body"])
            } else {
                sendPushNotification(remoteMessage!!.notification!!.title, remoteMessage!!.notification!!.body)
            }
        }catch (e : Exception) { }
    }

    private fun sendPushNotification(title: String?, message: String?) {
        LogUtil.e(TAG, "=======received message : $message")
        if(message == null) return

        val mBuilder = NotificationCompat.Builder(this, "notify")
        val ii = Intent(this, IntroActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, ii, 0)

        //Notification 알림 사운드 설정
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val bigText = NotificationCompat.BigTextStyle()
        bigText.bigText(message)
        bigText.setBigContentTitle(title)
        //bigText.setSummaryText("Text in detail")
        mBuilder.setContentIntent(pendingIntent)
        mBuilder.setSmallIcon(R.drawable.icon)
        mBuilder.setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.icon))
        mBuilder.setContentTitle(title)
        mBuilder.setContentText(message)
        mBuilder.setSound(soundUri)
        mBuilder.priority = Notification.PRIORITY_MAX
        mBuilder.setStyle(bigText)

        val mNotificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("notify", "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            mNotificationManager.createNotificationChannel(channel)
        }

        mNotificationManager.notify(NotificationID.id, mBuilder.build())

        val pm = this.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakelock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG")
        wakelock.acquire(5000)
    }

    object NotificationID {
        private val c = AtomicInteger(0)
        val id: Int
            get() = c.incrementAndGet()
    }

    companion object {
        val TAG = FirebaseMessagingService::class.java!!.getName()
    }
}