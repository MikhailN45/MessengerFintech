package com.study.messengerfintech.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.model.source.FakeDataSourceImpl
import com.study.messengerfintech.model.data.Chat
import com.study.messengerfintech.model.data.StreamAndChatItem
import com.study.messengerfintech.domain.usecase.CheckSubscribedUseCaseImpl
import com.study.messengerfintech.domain.usecase.CheckSubscribedUseCase
import com.study.messengerfintech.domain.usecase.SearchStreamUseCase
import com.study.messengerfintech.domain.usecase.SearchUsersUseCase
import com.study.messengerfintech.domain.usecase.SearchStreamChatsUseCaseImpl
import com.study.messengerfintech.domain.usecase.SearchUsersUseCaseImpl
import com.study.messengerfintech.view.states.StreamsAndChatsState
import com.study.messengerfintech.view.states.MessengerState
import com.study.messengerfintech.view.states.UsersState
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class MainViewModel : ViewModel() {
    private val dataSource = FakeDataSourceImpl
    val chat = MutableLiveData<Pair<Int, Int>>()
    val streams = MutableLiveData<List<StreamAndChatItem>>()
    val subscribedStreams = MutableLiveData<List<StreamAndChatItem>>()

    private val searchUsersSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val searchStreamsSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val searchUsersUseCase: SearchUsersUseCase = SearchUsersUseCaseImpl()
    private val searchStreamChatsUseCase: SearchStreamUseCase = SearchStreamChatsUseCaseImpl()
    private val checkSubscribedUseCase: CheckSubscribedUseCase = CheckSubscribedUseCaseImpl()

    private val _usersState: MutableLiveData<UsersState> = MutableLiveData()
    val usersState: LiveData<UsersState> = _usersState

    private val _streamsAndChatsState: MutableLiveData<StreamsAndChatsState> = MutableLiveData()
    val streamsAndChatsState: LiveData<StreamsAndChatsState> = _streamsAndChatsState

    private val _messengerState: MutableLiveData<MessengerState> = MutableLiveData()
    val messengerState: LiveData<MessengerState> = _messengerState

    fun onStreamsFragmentViewCreated() {
        searchStreamsSubject.onNext("")
    }

    fun onStreamsFragmentSearchUsersTextChanged(request: String) {
        searchStreamsSubject.onNext(request)
    }

    fun onUsersFragmentViewCreated() {
        searchUsersSubject.onNext("")
    }

    fun onUsersFragmentSearchUsersTextChanged(request: String) {
        searchUsersSubject.onNext(request)
    }

    init {
        subscribeToSearchUsers()
        subscribeToSearchStreams()
    }

    private fun subscribeToSearchUsers() {
        searchUsersSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _usersState.postValue(UsersState.Loading) }
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMap { searchRequest -> searchUsersUseCase(searchRequest) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { _usersState.value = UsersState.Success(it) },
                onError = { _usersState.value = UsersState.Error }
            )
            .addTo(compositeDisposable)
    }


    private fun subscribeToSearchStreams() {
        val flow = searchStreamsSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _streamsAndChatsState.postValue(StreamsAndChatsState.Loading) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchRequest -> searchStreamChatsUseCase(searchRequest) }
            .share()

        flow.map(checkSubscribedUseCase)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    subscribedStreams.value = it
                    _streamsAndChatsState.value = StreamsAndChatsState.Success
                },
                onError = { _streamsAndChatsState.value = StreamsAndChatsState.Error }
            )
            .addTo(compositeDisposable)

        flow.observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    streams.value = it
                    _streamsAndChatsState.value = StreamsAndChatsState.Success
                },
                onError = { _streamsAndChatsState.value = StreamsAndChatsState.Error }
            )
            .addTo(compositeDisposable)
    }

    fun getChat(streamId: Int, chatId: Int): Single<Chat> =
        getChats(streamId).map { it[chatId] }

    fun getChats(streamId: Int): Single<List<Chat>> =
        dataSource.loadStream(streamId).singleOrError().map { it.chats }

    fun openChat(streamId: Int, chatId: Int) = chat.postValue(streamId to chatId)

    fun onChatViewCreated() = _streamsAndChatsState.postValue(StreamsAndChatsState.Loading)

    fun resultChats() = _streamsAndChatsState.postValue(StreamsAndChatsState.Success)

    fun messageSendingError(error: Throwable) {
        _messengerState.value = MessengerState.Error(error)
    }
}