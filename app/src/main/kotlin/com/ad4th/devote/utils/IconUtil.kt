package com.ad4th.devote.utils

import android.os.AsyncTask
import android.text.TextUtils
import com.ad4th.devote.application.DevoteApplication
import com.ad4th.devote.base.BaseActivity
import com.google.gson.Gson
import loopchain.sdk.core.Constants
import loopchain.sdk.core.response.LCResponse
import loopchain.sdk.service.LoopChainClient
import loopchain.sdk.service.crypto.KeyStoreUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object IconUtil {

    private val CODE_OK = 0

    // 9월행사대응
//    public var TEMP_ICX = 0
    /**
     * 지갑 생성
     */
    class WalletCreate internal constructor(private val password: String, private val callback: WalletCallback) {


        fun start() {
            callback.start()
            CreateWallet().execute()
        }

        /**
         * 지갑 생성
         */
        internal inner class CreateWallet : AsyncTask<Void, Void, Array<String>>() {
            override fun doInBackground(vararg voids: Void): Array<String>? {

                try {
                    return KeyStoreUtils.generateICXKeystore(password)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return null
                }
            }

            override fun onPostExecute(result: Array<String>?) {
                super.onPostExecute(result)

                if (result != null) {
                    callback.successed(Gson().toJson(result))
                } else {
                    callback.failed()
                }
            }

        }
    }

    /**
     * 지갑 잔액 조회.
     */
    class WalletBalance internal constructor(private val address: String, private val callback: WalletCallback) {

        fun start() {
            callback.start()
            try {
                val client = LoopChainClient(Constants.TRUSTED_HOST_TEST)
                // getBalance
                // from
                val response = client.getBalance(address)
                response.enqueue(object : Callback<LCResponse> {
                    override fun onResponse(call: Call<LCResponse>, response: Response<LCResponse>) {

                        val resCode = response.body()!!.result.asJsonObject.get("response_code").asInt
                        if (resCode == CODE_OK) {
                            val balance = response.body()!!.result.asJsonObject.get("response").asString
                            callback.successed(client.printICX(balance))
                        } else {
                            callback.failed()
                        }
                    }

                    override fun onFailure(call: Call<LCResponse>, t: Throwable) {
                        callback.errored(t)
                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /*
    class WalletBalance internal constructor(private val address: String, private val callback: WalletCallback) {

        fun start() {
            callback.start()
            callback.successed(""+TEMP_ICX )
        }
    }
*/

    /**
     * 송금
     */
    class RequestTransaction internal constructor(internal var from: String, internal var to: String, internal var value: String, internal var hexPrivateKey: String, internal var callback: WalletCallback) {

        fun start() {
            callback.start()
            try {
                // 지갑 비밀번호로 Key Store 에서 private key 가져오기.
                //                String hexPrivKey = Hex.toHexString(priv);

                val client = LoopChainClient(Constants.TRUSTED_HOST_TEST)
                // sendTransaction
                // from, to, value, privateKey
                val response = client.sendTransaction(from, to, value, hexPrivateKey)
                response.enqueue(object : Callback<LCResponse> {
                    override fun onResponse(call: Call<LCResponse>, response: Response<LCResponse>) {
                        val result = response.body()!!.result
                        if (result != null) {
                            val resCode = result.asJsonObject.get("response_code").asInt
                            if (resCode == CODE_OK) {
                                val txHash = result.asJsonObject.get("tx_hash").asString
                                callback.successed(txHash)

                            } else {
                                val message = result.asJsonObject.get("message").asString
                                callback.successed(message)
                                //                                txtTxResult.setText(String.valueOf(resCode) + " : " + message);
                            }
                        } else {
                            callback.failed()
                        }
                    }

                    override fun onFailure(call: Call<LCResponse>, t: Throwable) {
                        callback.errored(t)

                    }
                })
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }

    /*
    class RequestTransaction internal constructor(internal var from: String, internal var to: String, internal var value: String, internal var hexPrivateKey: String, internal var callback: WalletCallback) {

        fun start() {
            callback.start()
            callback.successed("tx validate success")


        }

    }
    */



    fun formatIcx(icx: String) : String{

        return if(icx.toDouble() > 0) {
            var i = icx.indexOf(".")
            if( i < 0 ) return icx
            icx.substring(0, i + 7)
        } else {
            "0"
        }
    }

    open class WalletCallback(val mActivity:BaseActivity?) : BaseWalletCallback{
        override fun start() {
            mActivity?.showProgress()
        }

        override fun errored(t: Throwable) {
            mActivity?.dismissProgress()

        }

        override fun failed() {
            mActivity?.dismissProgress()
        }

        override fun successed(result: String) {
            mActivity?.dismissProgress()
        }
    }


    interface BaseWalletCallback {
        fun start()
        fun successed(result: String)
        fun failed()
        fun errored(t: Throwable)
    }

}
