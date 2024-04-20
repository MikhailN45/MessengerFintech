package com.study.messengerfintech.data.repository

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.study.messengerfintech.data.model.StreamResponse
import com.study.messengerfintech.data.model.toMessage
import com.study.messengerfintech.data.model.toReaction
import com.study.messengerfintech.data.model.toStream
import com.study.messengerfintech.data.model.toTopics
import com.study.messengerfintech.data.model.toUser
import com.study.messengerfintech.data.network.AuthInterceptor
import com.study.messengerfintech.data.network.NarrowInt
import com.study.messengerfintech.data.network.NarrowStr
import com.study.messengerfintech.data.network.ZulipApi
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.domain.model.Topic
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.model.UserStatus
import com.study.messengerfintech.domain.repository.Repository
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.encodeToJsonElement
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

object RepositoryImpl : Repository {
    private const val BASE_URL = "https://tinkoff-android-spring-2024.zulipchat.com/api/v1/"
    private val compositeDisposable = CompositeDisposable()

    private val format = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(AuthInterceptor())
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    private var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(format.asConverterFactory("application/json".toMediaType()))
        .build()

    private var service = retrofit.create(ZulipApi::class.java)


    override fun loadStreams(): Single<List<Stream>> =
        service.getStreams()
            .subscribeOn(Schedulers.io())
            .map { it.streams }
            .flatMap { topicsPreload(it) }


    override fun loadSubscribedStreams(): Single<List<Stream>> =
        service.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .map { it.subscriptions }
            .flatMap { topicsPreload(it) }

    private fun topicsPreload(streamResponses: List<StreamResponse>): Single<List<Stream>> =
        Single.create { emitter ->
            val streams = mutableListOf<Stream>()
            var count = 0
            streamResponses.onEach { streamResponse ->
                loadTopics(streamResponse.id)
                    .map { topics ->
                        val stream: Stream = streamResponse.toStream(topics)
                        streams.add(stream)
                    }.subscribeBy(
                        onSuccess = {
                            count += 1
                            if (count == streamResponses.size) emitter.onSuccess(streams)
                        },
                        onError = {
                            emitter.onError(it)
                            Log.e("TopicsPreload", it.message.toString())
                        }
                    ).addTo(compositeDisposable)
            }
        }

    override fun loadTopics(id: Int): Single<List<Topic>> =
        service.getTopicsInStream(id)
            .subscribeOn(Schedulers.io())
            .map { it.topics.toTopics() }


    override fun loadUsers(): Single<List<User>> =
        service.getUsers()
            .subscribeOn(Schedulers.io())
            .map {
                it.members.map { userResponse -> userResponse.toUser() }
            }

    override fun loadStatus(user: User): Single<UserStatus> =
        service.getPresence(user.id)
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.presence["aggregated"]?.status?.let { status ->
                    user.status = UserStatus.decodeFromStringToStatus(status)
                    user.status
                }
            }.doOnError {
                Log.e("LoadUserStatus", it.message.toString())
            }

    override fun loadOwnUser(): Single<User> =
        service.getOwnUser()
            .subscribeOn(Schedulers.io())
            .map { it.toUser() }

    override fun loadTopicMessages(stream: Int, topic: String): Single<List<Message>> {
        val narrow = listOf(
            NarrowInt("stream", stream),
            NarrowStr("topic", topic)
        ).map {
            Json.encodeToJsonElement(it)
        }.let {
            JsonArray(it).toString()
        }

        return service.getMessages(narrow = narrow)
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.messages.map { messageResponse ->
                    messageResponse.toMessage(
                        reactions = messageResponse.reactions.map { it.toReaction() }
                    )
                }
            }
    }

    override fun loadPrivateMessages(userEmail: String): Single<List<Message>> {
        val narrow = listOf(
            NarrowStr("pm-with", userEmail)
        ).map {
            Json.encodeToJsonElement(it)
        }.let {
            JsonArray(it).toString()
        }

        return service.getMessages(narrow = narrow)
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.messages.map { messageResponse ->
                    messageResponse.toMessage()
                }
            }
    }

    override fun sendMessage(
        type: SendType,
        to: String,
        content: String,
        topic: String
    ): Single<Int> =
        service.sendMessage(type.type, to, content, topic)
            .map { it.id }


    override fun addEmoji(messageId: Int, emojiName: String): Completable =
        service.addEmojiReaction(messageId, name = emojiName).ignoreElement()


    override fun deleteEmoji(messageId: Int, emojiName: String): Completable =
        service.deleteEmojiReaction(messageId, name = emojiName).ignoreElement()



    enum class SendType(val type: String) {
        PRIVATE("private"),
        STREAM("stream")
    }
}