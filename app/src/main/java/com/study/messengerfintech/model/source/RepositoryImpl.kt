package com.study.messengerfintech.model.source

import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.study.messengerfintech.domain.data.Message
import com.study.messengerfintech.domain.data.Stream
import com.study.messengerfintech.domain.data.Topic
import com.study.messengerfintech.domain.data.User
import com.study.messengerfintech.domain.data.UserStatus
import com.study.messengerfintech.model.data.MessageResponse
import com.study.messengerfintech.model.data.StreamResponse
import com.study.messengerfintech.model.data.TopicResponse
import com.study.messengerfintech.model.data.UserResponse
import com.study.messengerfintech.model.data.toMessage
import com.study.messengerfintech.model.data.toStream
import com.study.messengerfintech.model.data.toTopics
import com.study.messengerfintech.model.data.toUser
import com.study.messengerfintech.model.network.AuthInterceptor
import com.study.messengerfintech.model.network.NarrowInt
import com.study.messengerfintech.model.network.NarrowStr
import com.study.messengerfintech.model.network.ZulipApi
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.CompletableSubject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

object RepositoryImpl : Repository {
    private const val BASE_URL = "https://tinkoff-android-spring-2024.zulipchat.com/api/v1/"
    private val compositeDisposable = CompositeDisposable()
    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .addInterceptor(AuthInterceptor())
        .build()

    @OptIn(ExperimentalSerializationApi::class)
    private var retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    private var service = retrofit.create(ZulipApi::class.java)
    private val format = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    override fun loadStreams(): Single<List<Stream>> =
        service.getStreams()
            .subscribeOn(Schedulers.io())
            .map { body ->
                val answer =
                    format.decodeFromString<JsonObject>(body.string())["streams"]
                format.decodeFromString<List<StreamResponse>>(answer.toString())
            }.flatMap { topicsPreload(it) }


    override fun loadSubscribedStreams(): Single<List<Stream>> =
        service.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .map { body ->
                val answer =
                    format.decodeFromString<JsonObject>(body.string())["subscriptions"]
                format.decodeFromString<List<StreamResponse>>(answer.toString())
            }.flatMap { topicsPreload(it) }

    private fun topicsPreload(streamResponses: List<StreamResponse>): Single<List<Stream>> =
        Single.create { emitter ->
            val streams = mutableListOf<Stream>()
            var count = 0
            streamResponses.onEach { streamResponse ->
                loadTopics(streamResponse.id)
                    .map { topics ->
                        val stream: Stream = streamResponse.toStream(topics)
                        streams.add(stream)
                    }
                    .doOnError {
                        Log.e("TopicsPreload", it.message.toString())
                    }
                    .doOnSuccess {
                        count += 1
                        if (count == streamResponses.size) emitter.onSuccess(streams)
                    }.subscribe().addTo(compositeDisposable)
            }
        }

    override fun loadTopics(id: Int): Single<List<Topic>> =
        service.getTopicsInStream(id)
            .subscribeOn(Schedulers.io())
            .map { body ->
                val answer =
                    format.decodeFromString<JsonObject>(body.string())["topics"]
                val topicResponse =
                    format.decodeFromString<List<TopicResponse>>(answer.toString())
                topicResponse.toTopics()
            }


    override fun loadUsers(): Single<List<User>> =
        service.getUsers()
            .subscribeOn(Schedulers.io())
            .map { body ->
                val answer =
                    format.decodeFromString<JsonObject>(body.string())["members"]
                val userResponseList =
                    format.decodeFromString<List<UserResponse>>(answer.toString())
                userResponseList.map { userResponse ->
                    userResponse.toUser()
                }
            }

    override fun loadStatus(user: User): Single<UserStatus> =
        service.getPresence(user.id)
            .subscribeOn(Schedulers.io())
            .map { body ->
                val answer = Json.decodeFromString<JsonObject>(body.string())
                    .jsonObject["presence"]
                    ?.jsonObject?.get("aggregated")
                    ?.jsonObject?.get("status")

                answer?.jsonPrimitive?.content?.let { status ->
                    user.status = UserStatus.decodeFromString(status)
                    user.status
                }
            }.doOnError {
                Log.e("LoadUserStatus", it.message.toString())
            }

    override fun loadOwnUser(): Single<User> =
        service.getOwnUser()
            .subscribeOn(Schedulers.io())
            .map { body ->
                val userResponse = format.decodeFromString<UserResponse>(body.string())
                userResponse.toUser()
            }

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
            .map { body ->
                val answer = Json.decodeFromString<JsonObject>(body.string())["messages"]
                val messageResponses =
                    format.decodeFromString<List<MessageResponse>>(answer.toString())

                messageResponses.map { messageResponse ->
                    messageResponse.toMessage()
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
            .map { body ->
                val answer = Json.decodeFromString<JsonObject>(body.string())["messages"]
                val messageResponses =
                    format.decodeFromString<List<MessageResponse>>(answer.toString())
                messageResponses.map { messageResponse ->
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
            .subscribeOn(Schedulers.io())
            .map { body ->
                Json.decodeFromString<JsonObject>(body.string())["id"]
                    ?.jsonPrimitive
                    ?.content.let {
                        it?.toInt()
                    } ?: -1
            }


    override fun addEmoji(messageId: Int, emojiName: String): Completable {
        val completable = CompletableSubject.create()
        service.addEmojiReaction(messageId, name = emojiName)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { completable.onComplete() },
                { e -> completable.onError(e) }
            ).addTo(compositeDisposable)
        return completable
    }

    override fun deleteEmoji(messageId: Int, emojiName: String): Completable {
        val completable = CompletableSubject.create()
        service.deleteEmojiReaction(messageId, name = emojiName)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { completable.onComplete() },
                { e -> completable.onError(e) }
            ).addTo(compositeDisposable)
        return completable
    }

    enum class SendType(val type: String) {
        PRIVATE("private"),
        STREAM("stream")
    }
}