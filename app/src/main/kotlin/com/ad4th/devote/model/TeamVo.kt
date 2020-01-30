package com.ad4th.devote.model

import java.io.Serializable
import java.util.*

/**
 * Team Model
 */
data class TeamVo(  val id: Int = -1                              // 팀ID
                  , val number: Int = -1                          // 팀 번호
                  , val name: String? = null                      // 팀 이름
                  , val imageUrl: String? = null                  // 팀 이미지 URl
                  , val description: String? = null               // 팀 기술
                  , val createdAt: Date? = null                   // 등록일시
                  , val updatedAt: Date? = null                   // 수정일시
                  , val wallet:WalletVo? = null                   // 지갑정보
                  , var choice: Boolean = false                   // 투표 시 사용 -선택여부
                  , var rank: Int = -1                            // 투표결과 순위
) : Serializable
