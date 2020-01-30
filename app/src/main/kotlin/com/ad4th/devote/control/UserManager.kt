package com.ad4th.devote.control

import android.content.Context
import com.ad4th.devote.model.UserVo
import com.ad4th.devote.utils.SharedProperty
import com.google.gson.Gson

/**
 *  사용자 정보 관리
 */
class UserManager(context: Context) {

    private val sharedProperty:SharedProperty = SharedProperty(context)
    var userVo:UserVo

    init {
        try {
            val jsonData =  sharedProperty.getValue(SharedProperty.KEY_USER_DATA, "")
            userVo = if (jsonData.equals("")) UserVo() else Gson().fromJson(jsonData, UserVo::class.java)
        }catch (e:Exception) {
            userVo = UserVo()
        }
    }

    /**
     * 유저정보 조회
     */
    fun getData() : UserVo{
        val jsonData =  sharedProperty.getValue(SharedProperty.KEY_USER_DATA, "")
        userVo = if (jsonData.equals("")) UserVo() else Gson().fromJson(jsonData, UserVo::class.java)
        return userVo
    }

    /**
     * 유저정보 설정
     */
    fun setData(userVo: UserVo){
        try {
            this.userVo= userVo
            sharedProperty.put(SharedProperty.KEY_USER_DATA, Gson().toJson(userVo))
        } catch (e: Exception) {}
    }

    /**
     * 유저정보 초기화
     */
    fun reset() {
        this.userVo = UserVo()
        sharedProperty.remove(SharedProperty.KEY_USER_DATA)
    }
}
