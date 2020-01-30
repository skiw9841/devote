package com.ad4th.devote.model

/**
 * 투표
 */
data class VotingVo(  val balance: Int = -1                             // 투표 송금액
                    , val txId:String? = null                           // 트랜젝션ID
                    , val eventId:Int = -1                              // 이벤트ID
                    , val userId:Int = -1                               // 유저ID
                    , val teamId:Int = -1                               // 팀ID
)

/**
 * 투표가능여부
 */
data class AvailVo(  val eventId:Int = -1                               // 이벤트 ID
                   , val userId:Int = -1                                // 유저 ID
                   , val eventVotingStatus:String? = null               // 투표가능상태
                   , val userVotingStatus:String? = null                // 사용자 투표가능 여부
)

/**
 * 투표결과 팀
 */
data class VotingResultVo(  val score: Int = -1                         // 투표점수
                          , val team:TeamVo? =null                      // 팀
                          , val viewType:Int =0                         // 뷰정의
)


/**
 * QR코드 사용등록
 */
data class QrCodeVo(  val eventId: Int = -1                         // 이벤트 ID
                    , val userId: Int? = -1                         // 유저 ID
                    , val code: String? = null                      // QR 코드
                    , val status: String? = null                    // QR 상태
                    , val success: Boolean = false                  // 결과 - 성공여부
                    , val message: String? = null                   // 결과 - 에러메시지
)






