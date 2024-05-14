package com.study.messengerfintech.presentation.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.domain.model.StreamItem
import com.study.messengerfintech.domain.model.TopicItem
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.repository.Repository
import com.study.messengerfintech.domain.usecase.SearchTopicsUseCase
import com.study.messengerfintech.presentation.events.StreamsEvent
import com.study.messengerfintech.presentation.fragments.ChatFragment.Companion.STREAM
import com.study.messengerfintech.presentation.fragments.ChatFragment.Companion.TOPIC
import com.study.messengerfintech.presentation.fragments.ChatFragment.Companion.USER_MAIL
import com.study.messengerfintech.presentation.fragments.ChatFragment.Companion.USER_NAME
import com.study.messengerfintech.presentation.state.State
import com.study.messengerfintech.utils.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class StreamsViewModel @Inject constructor(
    private val repository: Repository,
    private val searchTopicsUseCase: SearchTopicsUseCase
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchStreamsSubject: BehaviorSubject<String> = BehaviorSubject.create()
    val chatInstance = SingleLiveEvent<Bundle>()

    val streamsAll = MutableLiveData(State.Streams(listOf()))
    val streamsSubscribed = MutableLiveData(State.Streams(listOf()))

    private val _screenState: MutableLiveData<State> = MutableLiveData()
    val screenState: LiveData<State>
        get() = _screenState

    init {
        subscribeToSearchStreams()
        initUser()
    }

    fun processEvent(event: StreamsEvent) {
        when (event) {
            is StreamsEvent.SearchForStreams ->
                searchStreamsSubject.onNext(event.query)

            is StreamsEvent.OpenChat.Private ->
                openPrivateChat(event.user)

            is StreamsEvent.OpenChat.Topic ->
                openPublicChat(event.streamId, event.topic)

            is StreamsEvent.ExpandStream ->
                updateTopicsMessagesCount(event.stream)
        }
    }

    private fun subscribeToSearchStreams() {
        val subject = searchStreamsSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _screenState.postValue(State.Loading) }
            .doOnError { error ->
                Log.e("subscribeToSearchStreams", error.message.toString())
              //  _screenState.postValue(State.Error)
            }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .share()

        subject.flatMap { searchQuery ->
            Observables.zip(
                Observable.just(searchQuery),
                repository.loadStreams().toObservable(),
                repository.loadSubscribedStreams().toObservable(),
            )
        }.map {
            val (searchQuery, allStreams, subscribedStreams) = it
            val streamsAllTopics = searchTopicsUseCase(searchQuery, allStreams)
            val streamsSubscribedTopics = searchTopicsUseCase(searchQuery, subscribedStreams)
            Pair(streamsAllTopics, streamsSubscribedTopics)
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    val (allStreams, subscribedStreams) = it
                    streamsSubscribed.value = State.Streams(subscribedStreams)
                    streamsAll.value = State.Streams(allStreams)
                    _screenState.value = State.Success
                },
                onError = { _screenState.value = State.Error }
            )
            .addTo(compositeDisposable)
    }

    private fun updateTopicsMessagesCount(stream: StreamItem) {
        val topicList: MutableList<Single<TopicItem>> = mutableListOf()
        stream.topics.forEach { topic ->
            val topicListWithCount = repository.getMessageCountForTopic(
                stream.streamId,
                topic.title
            )
                .map { messageCount ->
                    topic.messageCount = messageCount
                    topic
                }
            topicList.add(topicListWithCount)
        }

        Single.zip(topicList) { topic -> topic.map { it as TopicItem } }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { updatedTopics ->
                    stream.topics = updatedTopics.sortedByDescending { it.messageCount }
                },
                { error ->
                    Log.e("updateTopicsMessageCount", error.message.toString())
                }
            )
            .addTo(compositeDisposable)
    }

    private fun initUser() {
        repository.loadOwnUser().subscribeBy(
            onSuccess = { user -> User.ME = user },
            onError = { error -> Log.e("initOwnUser", error.toString()) }
        ).addTo(compositeDisposable)
    }

    private fun openPrivateChat(user: User) {
        Bundle().apply {
            putString(USER_MAIL, user.email)
            putString(USER_NAME, user.name)
            chatInstance.value = (this)
        }
    }

    private fun openPublicChat(streamId: Int, topic: String) {
        Bundle().apply {
            putInt(STREAM, streamId)
            putString(TOPIC, topic)
            chatInstance.postValue(this)
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}