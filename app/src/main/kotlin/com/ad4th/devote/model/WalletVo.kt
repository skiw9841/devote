package com.ad4th.devote.model

import java.io.Serializable
import java.util.*

/**
 * Wallet Model
 */
data class WalletVo(  val id:Int? = null                                // 지갑 ID
                    , val address: String? = null                       // 지갑주소
                    , val privateKey: String? = null                    // Private Key
                    , val pwd: String? = null                           // 비밀번호
                    , val pwdConfirm: String? = null                    // 비밀번호 확인
                    , val networkType:String? = null                    // 넷 구분[테스트/메인]
                    , val keyStore:String?=null                         // 키스토어
                    , val updatedAt : Date? = null                      // 수정일자
                    , val createdAt: Date? = null                       // 생성일자
) : Serializable