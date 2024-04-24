package com.study.messengerfintech.presentation.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.data.repository.RepositoryImpl
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.repository.Repository
import com.study.messengerfintech.domain.usecase.SearchTopicsUseCase
import com.study.messengerfintech.domain.usecase.SearchTopicsUseCaseImpl
import com.study.messengerfintech.domain.usecase.SearchUsersUseCase
import com.study.messengerfintech.domain.usecase.SearchUsersUseCaseImpl
import com.study.messengerfintech.presentation.events.Event
import com.study.messengerfintech.presentation.fragments.ChatFragment.Companion.STREAM
import com.study.messengerfintech.presentation.fragments.ChatFragment.Companion.TOPIC
import com.study.messengerfintech.presentation.fragments.ChatFragment.Companion.USER_MAIL
import com.study.messengerfintech.presentation.fragments.ChatFragment.Companion.USER_NAME
import com.study.messengerfintech.presentation.state.State
import com.study.messengerfintech.utils.SendType
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

class MainViewModel : ViewModel() {
    private val repository: Repository = RepositoryImpl
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    //Event
    val chatInstance = SingleLiveEvent<Bundle>()
    val positionToScroll = SingleLiveEvent<Int>()

    //Subject
    private val searchUsersSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val searchStreamsSubject: BehaviorSubject<String> = BehaviorSubject.create()

    //Usecase
    private val searchTopicsUseCase: SearchTopicsUseCase = SearchTopicsUseCaseImpl()
    private val searchUserUseCase: SearchUsersUseCase = SearchUsersUseCaseImpl()

    //State
    val streamsAll = MutableLiveData(State.Streams(listOf()))
    val streamsSubscribed = MutableLiveData(State.Streams(listOf()))
    val chat = MutableLiveData<State.Chat>()
    val users = MutableLiveData<State.Users>()
    val userStatus = MutableLiveData<State.Profile>()

    //Screen state
    private val _screenState: MutableLiveData<State> = MutableLiveData()
    val screenState: LiveData<State>
        get() = _screenState

    init {
        subscribeToSearchUsers()
        subscribeToSearchStreams()
        initUser()
    }

    fun processEvent(event: Event) {
        when (event) {
            is Event.SearchForUsers ->
                searchUsersSubject.onNext(event.query)

            is Event.SearchForStreams ->
                searchStreamsSubject.onNext(event.query)

            is Event.OpenChat.Private ->
                openPrivateChat(event.user)

            is Event.OpenChat.Topic ->
                openPublicChat(event.streamId, event.topic)

            is Event.SendMessage.Private ->
                sendPrivateMessage(event.userEmail, event.content)

            is Event.SendMessage.Topic ->
                sendPublicMessage(event.streamId, event.topicName, event.content)

            is Event.LoadMessages.Private ->
                manageChatMessages(getPrivateMessages(event.userEmail))

            is Event.LoadMessages.Topic ->
                manageChatMessages(getTopicMessages(event.streamId, event.topicName))

            is Event.Emoji.Add ->
                addReaction(event.messageId, event.emojiName)

            is Event.Emoji.Remove ->
                deleteReaction(event.messageId, event.emojiName)

            is Event.SetUserStatus ->
                loadStatus(event.user)
        }
    }

    private fun initUser() {
        repository.loadOwnUser().subscribeBy(
            onSuccess = { user -> User.ME = user },
            onError = { error -> Log.e("initOwnUser", error.toString()) }
        ).addTo(compositeDisposable)
    }

    private fun subscribeToSearchUsers() {
        searchUsersSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _screenState.postValue(State.Loading) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery -> searchUserUseCase(searchQuery) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    users.value = State.Users(it)
                    _screenState.value = State.Success
                },
                onError = { _screenState.value = State.Error }
            )
            .addTo(compositeDisposable)
    }

    private fun subscribeToSearchStreams() {
        val subject = searchStreamsSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _screenState.postValue(State.Loading) }
            .doOnError { error ->
                Log.e("subscribeToSearchStreams", error.message.toString())
                _screenState.postValue(State.Error)
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

    private fun manageChatMessages(flow: Single<List<Message>>) {
        loading()
        val chat = chat.value!!.copy()
        flow
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { messageList ->
                    result()
                    val isMessagesRemaining = messageList.isEmpty()
                    val messages =
                        chat.messages.toMutableList().apply { addAll(messageList) }
                    this.chat.value = chat.copy(messages = messages, loaded = isMessagesRemaining)
                    if (chat.messages.isEmpty())
                        positionToScroll.value = 0
                },
                onError = { error(it) }
            )
            .addTo(compositeDisposable)
    }

    private fun getTopicMessages(streamId: Int, topic: String): Single<List<Message>> =
        repository.loadTopicMessages(streamId, topic)

    private fun getPrivateMessages(user: String): Single<List<Message>> =
        repository.loadPrivateMessages(user)

    private fun openPrivateChat(user: User) {
        Bundle().apply {
            putString(USER_MAIL, user.email)
            putString(USER_NAME, user.name)
            chatInstance.postValue(this)
        }
        chat.value = State.Chat(name = user.name, messages = listOf())
    }

    private fun openPublicChat(streamId: Int, topic: String) {
        Bundle().apply {
            putInt(STREAM, streamId)
            putString(TOPIC, topic)
            chatInstance.postValue(this)
        }
        chat.value = State.Chat(name = topic, messages = listOf())
    }

    private fun sendMessage(
        type: SendType,
        to: String,
        content: String,
        topic: String = ""
    ) {
        loading()
        repository.sendMessage(type, to, content, topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { id ->
                    val message = Message(id, content, User.ME.id, true)
                    val state = chat.value!!
                    val updatedList =
                        state.messages.toMutableList().apply { add(0, message) }
                    chat.value = state.copy(messages = updatedList)
                    positionToScroll.value = 0
                    result()
                },
                onError = { error(it) }
            ).addTo(compositeDisposable)
    }

    private fun loadStatus(user: User) = repository.loadStatus(user)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { userStatus.value = State.Profile(it) },
            onError = { error ->
                Log.e("loadStatus", error.toString())
            }
        ).addTo(compositeDisposable)

    private fun sendPrivateMessage(userEmail: String, content: String) =
        sendMessage(SendType.PRIVATE, userEmail, content)

    private fun sendPublicMessage(stream: Int, topic: String, content: String) =
        sendMessage(SendType.STREAM, "[$stream]", content, topic)

    private fun addReaction(messageId: Int, emojiName: String) {
        repository.addEmoji(messageId, emojiName)
            .subscribeBy(
                onComplete = {},
                onError = { error(it) }
            ).addTo(compositeDisposable)
    }

    private fun deleteReaction(messageId: Int, emojiName: String) {
        repository.deleteEmoji(messageId, emojiName)
            .subscribeBy(
                onComplete = {},
                onError = { error(it) }
            ).addTo(compositeDisposable)
    }

    private fun result() {
        _screenState.postValue(State.Success)
    }

    fun error(error: Throwable) {
        _screenState.postValue(State.Error)
    }

    private fun loading() {
        _screenState.postValue(State.Loading)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}