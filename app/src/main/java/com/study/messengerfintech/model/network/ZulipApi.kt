package com.study.messengerfintech.model.network

import io.reactivex.Single
import kotlinx.serialization.Serializable
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ZulipApi {
    @GET("streams")
    fun getStreams(): Single<ResponseBody>

    @GET("users/me/subscriptions")
    fun getSubscribedStreams(): Single<ResponseBody>

    @GET("users/me/{stream_id}/topics")
    fun getTopicsInStream(@Path("stream_id") streamId: Int): Single<ResponseBody>

    @GET("users")
    fun getUsers(): Single<ResponseBody>

    @GET("users/{id}/presence")
    fun getPresence(@Path("id") userId: Int): Single<ResponseBody>

    @GET("users/me")
    fun getOwnUser(): Single<ResponseBody>

    @FormUrlEncoded
    @POST("messages")
    fun sendMessage(
        @Field("type") type: String,
        @Field("to") to: String,
        @Field("content") content: String,
        @Field("topic") topic: String = ""
    ): Single<ResponseBody>

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: String = "newest",
        @Query("num_before") numBefore: Int = 1000,
        @Query("num_after") numAfter: Int = 1000,
        @Query("narrow") narrow: String,
    ): Single<ResponseBody>

    @FormUrlEncoded
    @POST("messages/{message_id}/reactions")
    fun addEmojiReaction(
        @Path("message_id") messageId: Int,
        @Field("emoji_name") name: String,
    ): Single<ResponseBody>

    @DELETE("messages/{message_id}/reactions")
    fun deleteEmojiReaction(
        @Path("message_id") messageId: Int,
        @Query("emoji_name") name: String,
    ): Single<ResponseBody>

    @Serializable
    sealed class Narrow

    @Serializable
    data class NarrowStr(val operator: String, val operand: String) : Narrow()

    @Serializable
    data class NarrowInt(val operator: String, val operand: Int) : Narrow()
}