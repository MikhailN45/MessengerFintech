package com.study.messengerfintech.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.domain.model.Topic
import com.study.messengerfintech.domain.usecase.SearchTopicsUseCase
import com.study.messengerfintech.domain.usecase.SearchTopicsUseCaseImpl
import com.study.messengerfintech.domain.usecase.SearchUsersUseCase
import com.study.messengerfintech.domain.usecase.SearchUsersUseCaseImpl
import com.study.messengerfintech.model.data.Message
import com.study.messengerfintech.model.data.StreamTopicItem
import com.study.messengerfintech.model.data.User
import com.study.messengerfintech.model.source.Repository
import com.study.messengerfintech.model.source.RepositoryImpl
import com.study.messengerfintech.view.fragments.ChatFragment
import com.study.messengerfintech.view.state.ChatState
import com.study.messengerfintech.view.state.StreamsTopicsState
import com.study.messengerfintech.view.state.UsersState
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

//todo separate to different vm's later
class MainViewModel : ViewModel() {
    private val repository: Repository = RepositoryImpl

    //todo move chat to state
    val chat = MutableLiveData<Bundle>()

    //todo move users to state
    val users = MutableLiveData<List<User>>()

    //todo move streams to state
    val streams = MutableLiveData<List<StreamTopicItem>>()

    //todo move sub. streams to state
    val subscribedStreams = MutableLiveData<List<StreamTopicItem>>()

    private val searchUsersSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val searchStreamSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val searchTopicsUseCase: SearchTopicsUseCase = SearchTopicsUseCaseImpl()
    private val searchUserUseCase: SearchUsersUseCase = SearchUsersUseCaseImpl()

    private val _userState: MutableLiveData<UsersState> = MutableLiveData()
    val userScreenState: LiveData<UsersState>
        get() = _userState

    private val _streamTopicsState: MutableLiveData<StreamsTopicsState> = MutableLiveData()
    val streamTopicsState: LiveData<StreamsTopicsState>
        get() = _streamTopicsState

    private val _chatState: MutableLiveData<ChatState> = MutableLiveData()
    val chatState: LiveData<ChatState>
        get() = _chatState

    fun searchStreams(query: String) {
        searchStreamSubject.onNext(query)
    }

    fun searchUsers(query: String) {
        searchUsersSubject.onNext(query)
    }

    init {
        subscribeToSearchUsers()
        subscribeToSearchStreams()
        initUser()
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
            .doOnNext { _userState.postValue(UsersState.Loading) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery -> searchUserUseCase(searchQuery) }
            .observeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    users.value = it
                    _userState.value = UsersState.Success
                },
                onError = { _userState.value = UsersState.Error(it) }
            )
            .addTo(compositeDisposable)
    }


    private fun subscribeToSearchStreams() {
        val flow = searchStreamSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _streamTopicsState.postValue(StreamsTopicsState.Loading) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .share()

        flow.switchMap { searchQuery ->
            searchTopicsUseCase(searchQuery, repository.loadSubscribedStreams())
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    subscribedStreams.value = it
                    _streamTopicsState.value = StreamsTopicsState.Success
                },
                onError = { _streamTopicsState.value = StreamsTopicsState.Error(it) }
            )
            .addTo(compositeDisposable)

        flow.switchMap { searchQuery ->
            searchTopicsUseCase(searchQuery, repository.loadStreams())
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    streams.value = it
                    _streamTopicsState.value = StreamsTopicsState.Success
                },
                onError = { _streamTopicsState.value = StreamsTopicsState.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    fun getTopicMessages(streamId: Int, topic: String): Single<List<Message>> =
        repository.loadTopicMessages(streamId, topic)

    fun getPrivateMessages(user: String): Single<List<Message>> =
        repository.loadPrivateMessages(user)

    fun getTopics(streamId: Int): Single<List<Topic>> = repository.loadTopics(streamId)

    fun getMessagesCount(stream: Int, topic: String): Single<Int> =
        RepositoryImpl.loadTopicMessages(stream, topic).map { it.size }

    fun openPrivateChat(user: User) {
        Bundle().apply {
            putString(ChatFragment.USER, user.email)
            putString(ChatFragment.USER_NAME, user.name)
            chat.postValue(this)
        }
    }

    fun openTopicChat(streamId: Int, topic: String) {
        Bundle().apply {
            putInt(ChatFragment.STREAM, streamId)
            putString(ChatFragment.TOPIC, topic)
            chat.postValue(this)
        }
    }

    private fun sendMessage(
        type: RepositoryImpl.SendType,
        to: String,
        content: String,
        topic: String = ""
    ): Single<Int> =
        repository.sendMessage(type, to, content, topic)


    fun sendMessageToUser(userEmail: String, content: String) =
        sendMessage(RepositoryImpl.SendType.PRIVATE, userEmail, content)

    fun sendMessageToTopic(stream: Int, topic: String, content: String) =
        sendMessage(RepositoryImpl.SendType.STREAM, "[$stream]", content, topic)

    fun addReaction(messageId: Int, emojiName: String) {
        repository.addEmoji(messageId, emojiName)
            .subscribeBy(
                onComplete = {},
                onError = { chatScreenError(it) }
            ).addTo(compositeDisposable)
    }

    fun deleteReaction(messageId: Int, emojiName: String) {
        repository.deleteEmoji(messageId, emojiName)
            .subscribeBy(
                onComplete = {},
                onError = { chatScreenError(it) }
            ).addTo(compositeDisposable)
    }

    fun chatScreenLoading() {
        _chatState.postValue(ChatState.Loading)
    }

    fun chatScreenSuccessful() {
        _chatState.postValue(ChatState.Success)
    }

    fun streamTopicScreenError(error: Throwable) {
        _streamTopicsState.postValue(StreamsTopicsState.Error(error))
    }

    fun usersScreenError(error: Throwable) {
        _userState.postValue(UsersState.Error(error))
    }

    fun chatScreenError(error: Throwable) {
        _chatState.postValue(ChatState.Error(error))
    }
}