package com.ad4th.devote.fcm

import com.ad4th.devote.utils.SharedProperty
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService


class FirebaseInstanceIDService : FirebaseInstanceIdService() {

    // [START refresh_token]
    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val token = FirebaseInstanceId.getInstance().token
        token?.let { SharedProperty(this).put(SharedProperty.KEY_PUSH_TOKEN, token) }
    }

}