package com.ad4th.devote.network

import com.ad4th.devote.config.URI
import com.ad4th.devote.model.*
import retrofit2.Call
import retrofit2.http.*

interface RetroApiService {

    /**    Intro 조회      */
    @GET("intro/ANDROID")
    fun getSelectIntro(@Query("version") version:String): Call<IntroVo>

    /**     이벤트 조회    */
    @GET
    fun getSelectEvent(@Url url: String): Call<EventVo>

    /**     지갑 및 유저 등록    */
    @POST
    fun postInsertUser(@Url url: String, @Body userVo: UserVo): Call<UserResVo>

    /**     투표등록      */
    @POST
    fun postInsertVotings(@Url url:String, @Body votingVo: VotingVo): Call<VotingVo>

    /**     투표가능여부체크      */
    @POST
    fun postInsertVotingsAvail(@Url url: String, @Body availVo: AvailVo): Call<AvailVo>

    /**     투표결과 조회      */
    @GET
    fun getSelectListEventResult(@Url url: String): Call<List<VotingResultVo>>

    /**     QR코드 사용 가능여부      */
    @POST("qrcodes/use")
    fun postValidationWithRegeditQrCode(@Body qrCodeVo: QrCodeVo): Call<QrCodeVo>


    companion object {
        val Base_URL = URI.Builder().build()
    }
}