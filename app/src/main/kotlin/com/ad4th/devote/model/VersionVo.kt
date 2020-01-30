package com.ad4th.devote.model

import java.util.*

data class VersionVo(  val latest:String? = null                     // 최신버전정보
                     , val deviceType:String? = null                 // 디바이스 구분
                     , val forceUpdate:Boolean = false               // 강제업데이트 여부
                     , val needUpdate:Boolean = false                // 안내업데이트 여부
                     , val createdAt: Date? = null                   // 등록일시
                     , val updatedAt: Date? = null                   // 수정일시
)