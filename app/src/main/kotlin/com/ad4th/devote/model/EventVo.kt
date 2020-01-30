package com.ad4th.devote.model

import java.util.*

/**
 * 행사정보-장소 Model
 */
data class VenueVo(  val id: Int? = null                            // 행사-주소 ID
                   , val name: String? = null                       // 행사-건물명
                   , val createdAt: Date? = null                    // 행사-등록일시
                   , val updatedAt: Date? = null                    // 행사-수정일시
                   , val address: String? = null                    // 행사-건물주소
                   , val addressDetail: String? = null              // 행사-건물주소 상세
                   , val latitude: Double? = null                   // 행사-위도
                   , val longitude: Double? = null                  // 행사-경도
                   , val zipCode: String? = null                    // 우편번호
)

/**
 * 행사정보 Model
 */
data class EventVo(  val id: Int? = null                            // 이벤트 ID
                   , val mainTitle: String? = null                  // 이벤트 메인 타이틀
                   , val subTitle: String? = null                   // 이벤트 서브 타이틀
                   , val titleImageUrl: String? = null              // 이벤트 이미지
                   , val eventStatus: String? = null                // 이벤트 상태
                   , val eventStartDateTime: Date? = null           // 이벤트 시작일시
                   , val eventEndDateTime: Date? = null             // 이벤트 종료일시
                   , val qrCode: String? = null                     // 이벤트 QR CODE
                   , val networkType: String? = null                // 넷 타입[테스트/메인]
                   , val userLimit: Int? = 0                        // 이벤트 Limit
                   , val icxPrize: Int? = 0                         // 우승 지급액
                   , val icxQuantity: Int? = 0                      // 코인 지급액
                   , val createdAt: Date? = null                    // 이벤트 등록일시
                   , val updatedAt: Date? = null                    // 이벤트 등록일시
                   , val voteStatus: String? = null                 // 투표가능 상태
                   , val venue: VenueVo? = null                     // 계최지 정보
                   , val teams: List<TeamVo>? = null                // 행사참여 팀
                   , val votingOver:Boolean? = true                 // 투표종료여부
                   , val votingStatus: String? = null               // 투표상태
)






