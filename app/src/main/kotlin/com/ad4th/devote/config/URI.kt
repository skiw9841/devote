package com.ad4th.devote.config

import com.ad4th.devote.BuildConfig
import com.ad4th.devote.application.DevoteApplication


object URI {

    var scheme = BuildConfig.SCHEME
    var host = BuildConfig.HOST
    var port = BuildConfig.PORT

    /* 혹여 있을지 모를 분기처리를 위해 남겨둠  */
    init {
        val config = DevoteApplication.server.server
        when (config) {
            /* 개발 버전  */
            Config.DEV -> {
            }

            /* 상용 버전  */
            Config.REAL -> {
            }
        }
    }

    class Builder {
        private var scheme: String? = URI.scheme
        private var host: String? = URI.host
        private var port: String? = URI.port
        private var path: String? = null

        /**
         * 스키마를 지정한다(http,https)
         *
         * @see URI.scheme
         */
        fun setScheme(scheme: String): Builder {
            this.scheme = scheme
            return this
        }

        /**
         * 인증방식을 지정한다(host)
         */
        fun setHost(host: String): Builder {
            this.host = host
            return this
        }

        /**
         * 인증방식을 지정한다(host)
         */
        fun setPort(port: String): Builder {
            this.port = port
            return this
        }


        /**
         * 경로를 지정한다
         */
        fun setPath(path: String): Builder {
            this.path = path
            return this
        }

        /**
         * 매개 변수를 조합해 url을 생성한다
         */
        fun build(): String {
            val uri = StringBuilder()
            if (scheme != null) {
                uri.append(scheme)
                uri.append(":")
            }
            if (host != null) {
                uri.append("//")
                uri.append(host)
            }
            if (port != null) {
                uri.append(":")
                uri.append(port)
            }

            if (path != null) {
                uri.append(path)
            }
            return uri.toString()
        }
    }

    /** 이벤트 결과 조회 */
    val URL_EVENT_RESULT = "/events/{eventId}/results"
    /** 유저 등록  */
    val URL_EVENT_USER = "/events/{eventId}/users"
    /** 이벤트 조회  */
    val URL_EVENT = "/events/{eventId}"
    /** 투표 가능여부 */
    val URL_EVENT_VOTING_AVAIL = "/events/{eventId}/votings/avail"
    /** 투표 등록 */
    var URL_EVENT_VOTING = "/events/{eventId}/votings"



}
