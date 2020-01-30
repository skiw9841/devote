package com.ad4th.devote.model

import java.io.Serializable

/**
 * User Model
 */
data class UserVo(  var id : Int? = null                                   // 유저 ID
                  , var walletAddress: String? = null                      // 사용자 지갑주소
                  , var pushToken: String? = null                          // 푸시토큰
                  , val deviceType: String = "ANDROID"                     // 사용자 디바이스 종류[1:Android, 2:iOS]
                  , var networkType: String? = null                        // 넷 타입[테스트/메인]
                  , val permitPushNotification: Boolean? = true            // 푸시 동의여부
                  , val permitLocationInfo: Boolean? = true                // 위치 동의여부
                  , var code: String? = null                               // QR 코드
                  , var eventId: Int? = -1                                 // 이벤트 ID
                  , var isVoting: Boolean = false                          // 투표참여여부
) : Serializable

/**
 * User 등록 Response
 */
data class UserResVo(  var id: Int? = null                                 // 유저 ID
                     , var qrCode: QrCodeVo? = null                        // QR코드
)
