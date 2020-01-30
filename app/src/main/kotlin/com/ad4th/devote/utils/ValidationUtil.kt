package com.ad4th.devote.utils

import com.ad4th.devote.R
import com.ad4th.devote.model.WalletVo
import java.util.regex.Pattern

object ValidationUtil {

    /**
     * 지갑 등록 시 Validation
     */
    fun walletAddrRegCheck(): Int {
        return -1
    }

    /**
     * 지갑 생성 시 비밀번호 Validation
     */
    fun walletPassword(walletVo: WalletVo): Int {

        if (walletVo.pwd.isNullOrEmpty()) {
            return R.string.alert_password_null
        } else if (walletVo.pwdConfirm.isNullOrEmpty()) {
            return R.string.alert_password_null
        } else if (!walletVo.pwd.equals(walletVo.pwdConfirm)) {
            return R.string.alert_password_not
        } else if (!Pattern.compile("^.{4,}").matcher(walletVo.pwd).matches()) {
            return R.string.alert_password_format
        }
        return -1
    }


    /**
     * Address validation
     */
    fun walletAddress(strAddress: String): Int {

        if (strAddress.isNullOrEmpty()) {
            return R.string.alert_wallet_address_not
        } else if (!Pattern.compile("^[0-9a-zA-Z]{42}$").matcher(strAddress).matches()) {
            return R.string.alert_wallet_address_not_formet
        }
        return -1
    }

    /**
     * PrivateKey validation
     */
    fun walletPrivateKey(strAddress: String): Int {

        if (strAddress.isNullOrEmpty()) {
            return R.string.alert_wallet_private_key_not
        } else if (!Pattern.compile("^[0-9a-zA-Z]{64}$").matcher(strAddress).matches()) {
            return R.string.alert_wallet_private_key_not_formet
        }
        return -1
    }

}
