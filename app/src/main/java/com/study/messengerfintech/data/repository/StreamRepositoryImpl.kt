package com.study.messengerfintech.data.repository

import android.util.Log
import com.study.messengerfintech.data.database.AppDatabase
import com.study.messengerfintech.data.network.ZulipApiService
import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.domain.model.Topic
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.repository.StreamRepository
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StreamRepositoryImpl @Inject constructor(
    private val service: ZulipApiService,
    private val database: AppDatabase
) : StreamRepository {

    override fun requestAllStreams(): Completable {
        return service.getStreams()
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { streamResponses ->
                val streams = streamResponses.streams
                    .map { streamResponse ->
                        loadTopics(streamResponse.id)
                            .map { topics ->
                                streamResponse.toStream(topics, false)
                            }
                    }
                Single.zip(streams) { it.toList() as List<Stream> }
                    .flatMapCompletable { streamList ->
                        database.streamDao().insert(streamList.toListStreamDto())
                    }
                    .doOnError { error: Throwable ->
                        Log.e("requestAllStreams", "${error.message}")
                    }
            }
            .onErrorComplete()
    }

    override fun getAllStreams(): Observable<List<Stream>> {
        return database.streamDao().getAll()
            .toObservable()
            .observeOn(Schedulers.io())
            .map { it.toListStream() }
            .doOnEach {
                Log.d("getAllStreams", "$it")
            }
    }

    override fun requestSubscribedStreams(): Completable {
        return service.getSubscribedStreams()
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { streamResponses ->
                val streams = streamResponses.subscriptions
                    .map { streamResponse ->
                        loadTopics(streamResponse.id)
                            .map { topics ->
                                streamResponse.toStream(topics, true)
                            }
                    }
                Single.zip(streams) { it.toList() as List<Stream> }
                    .flatMapCompletable { streamList ->
                        database.streamDao().insert(streamList.toListStreamDto())
                    }
                    .doOnError { error: Throwable ->
                        Log.e("requestSubscribedStreams", "${error.message}")
                    }
            }
            .onErrorComplete()
    }

    override fun getSubscribedStreams(): Observable<List<Stream>> {
        return database.streamDao().getSubscribed()
            .toObservable()
            .observeOn(Schedulers.io())
            .map { it.toListStream() }
            .doOnEach {
                Log.d("getSubscribedStreams", "$it")
            }
    }

    /*override fun getMessageCountForTopic(stream: Int, topic: String): Single<Int> =
        loadTopicMessages(stream, topic, "newest").map { it.size }
            .doOnError { Log.e("getMessagesCountForTopic", it.message.toString()) }
            .onErrorResumeNext { Single.just(0) }*/

    private fun loadTopics(streamId: Int): Single<List<Topic>> {
        val localAnswer = database.topicDao().getTopicsInStream(streamId)
            .doOnSuccess { Log.d("getTopicsInStream", "$streamId $it") }
            .map { it.toListTopic() }

        val remoteAnswer = service.getTopicsInStream(streamId)
            .subscribeOn(Schedulers.io())
            .map { it.topics.toTopics(streamId = streamId) }
            .flatMap { topics ->
                database.topicDao().insert(topics.toListTopicDto()).toSingleDefault(topics)
            }
            .onErrorResumeNext { localAnswer }

        return remoteAnswer.subscribeOn(Schedulers.io())
    }

    override fun loadOwnUser(): Single<User> = service.getOwnUser()
        .subscribeOn(Schedulers.io())
        .map { it.toUser() }
        .flatMap { database.userDao().insert(it.toUserDto()).toSingleDefault(it) }
        .onErrorResumeNext { database.userDao().getOwnUser().map { it.toUser() } }
}