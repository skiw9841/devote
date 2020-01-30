package com.ad4th.devote.config

object CODE {
    val DEVICE_TYPE_ALL             = "ALL"                         // 디바이스 구분[공통]
    val DEVICE_TYPE                 = "ANDROID"                     // 디바이스 구분
    val NOTICE_TYPE_SHOW            = "SHOW"                        // INTRO 공지 노출 여부
    val VOTE_ON                     = "ON"                          // 투표가능상태 여부
    val VOTING_POSSIBLE             = "POSSIBLE"                    // 투표가능상태

    /** 넷[메인/테스트] 구분*/
    val NET_TYPE_MAIN               = "MAIN"                        // 넷-메인
    val NET_TYPE_TEST               = "TEST"                        // 넷-테스트
    val EVENT_ACTIVATE              = "ACTIVATE"                    // 이벤트 상태



    val ACTIVITY_CODE_QR            = 1000                          // ACTIVITY QR
    val ACTIVITY_CODE_VOTING        = 1001                          // ACTIVITY VOTING


    val RESULT_CODE_QR_SUCCESS      = 1                             // QR코드 성공
    val RESULT_CODE_VOTING_DONE     = 2                             // 투표완료


}
