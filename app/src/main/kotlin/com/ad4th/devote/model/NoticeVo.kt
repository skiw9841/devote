package com.ad4th.devote.model

import java.util.*
/**
 * 공지사항
 */
data class NoticeVo(  val id: Int = -1                              // 공지사항 ID
                    , val deviceType:String? = null                 // 노출대상 디바이스
                    , val noticeStatus:String? = null               // 공지 노출 여부
                    , val title:String? = null                      // 공지 타이틀
                    , val url:String? = null                        // 공지 Url
                    , val validStartDatetime:Date? = null           // 공지유효기간-시작
                    , val validEndDatetime:Date? = null             // 공지유효기간-종료
                    , val createdAt: Date? = null                   // 등록일시
                    , val updatedAt: Date? = null                   // 수정일시
)
