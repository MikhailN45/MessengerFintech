package com.study.messengerfintech.data.network

import com.study.messengerfintech.data.network.model.AllStreamRootResponse
import com.study.messengerfintech.data.network.model.MessageSendResponse
import com.study.messengerfintech.data.network.model.MessagesReceiveResponse
import com.study.messengerfintech.data.network.model.PresenceResponse
import com.study.messengerfintech.data.network.model.SubscribedStreamsRootResponse
import com.study.messengerfintech.data.network.model.TopicsRootResponse
import com.study.messengerfintech.data.network.model.UserResponse
import com.study.messengerfintech.data.network.model.UsersRootResponse
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ZulipApiService {
    @GET("streams")
    fun getStreams(): Single<AllStreamRootResponse>

    @GET("users/me/subscriptions")
    fun getSubscribedStreams(): Single<SubscribedStreamsRootResponse>

    @GET("users/me/{stream_id}/topics")
    fun getTopicsInStream(@Path("stream_id") streamId: Int): Single<TopicsRootResponse>

    @GET("users")
    fun getUsers(): Single<UsersRootResponse>

    @GET("users/{id}/presence")
    fun getPresence(@Path("id") userId: Int): Single<PresenceResponse>

    @GET("users/me")
    fun getOwnUser(): Single<UserResponse>

    @FormUrlEncoded
    @POST("messages")
    fun sendMessage(
        @Field("type") type: String,
        @Field("to") to: String,
        @Field("content") content: String,
        @Field("topic") topic: String = ""
    ): Single<MessageSendResponse>

    @GET("messages")
    fun getMessages(
        @Query("anchor") anchor: String = "newest",
        @Query("num_before") numBefore: Int = 20,
        @Query("num_after") numAfter: Int = 0,
        @Query("narrow") narrow: String,
    ): Single<MessagesReceiveResponse>

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
}