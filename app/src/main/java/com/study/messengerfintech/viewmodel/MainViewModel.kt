package com.study.messengerfintech.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.model.source.FakeDataSourceImpl
import com.study.messengerfintech.model.data.Chat
import com.study.messengerfintech.model.data.StreamAndChatItem
import com.study.messengerfintech.model.data.User
import com.study.messengerfintech.domain.usecase.CheckSubscribedUseCase
import com.study.messengerfintech.domain.usecase.ICheckSubscribedUseCase
import com.study.messengerfintech.domain.usecase.ISearchStreamUseCase
import com.study.messengerfintech.domain.usecase.ISearchUsersUseCase
import com.study.messengerfintech.domain.usecase.SearchStreamChatsUseCase
import com.study.messengerfintech.domain.usecase.SearchUsersUseCase
import com.study.messengerfintech.view.State
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
    val users = MutableLiveData<List<User>>()
    val chat = MutableLiveData<Pair<Int, Int>>()
    val streams = MutableLiveData<List<StreamAndChatItem>>()
    val subscribedStreams = MutableLiveData<List<StreamAndChatItem>>()

    private val searchUsersSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val searchStreamsSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val searchUsersUseCase: ISearchUsersUseCase = SearchUsersUseCase()
    private val searchStreamChatsUseCase: ISearchStreamUseCase = SearchStreamChatsUseCase()
    private val checkSubscribedUseCase: ICheckSubscribedUseCase = CheckSubscribedUseCase()

    private val _state: MutableLiveData<State> = MutableLiveData()
    val state: LiveData<State>
        get() = _state

    fun searchStreams(request: String) {
        searchStreamsSubject.onNext(request)
    }

    fun searchUsers(request: String) {
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
            .doOnNext { _state.postValue(State.Loading) }
            .debounce(500, TimeUnit.MILLISECONDS)
            .switchMap { searchRequest -> searchUsersUseCase(searchRequest) }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    users.value = it
                    _state.value = State.Result
                },
                onError = { _state.value = State.Error(it) }
            )
            .addTo(compositeDisposable)
    }


    private fun subscribeToSearchStreams() {
        val flow = searchStreamsSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _state.postValue(State.Loading) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchRequest -> searchStreamChatsUseCase(searchRequest) }
            .share()

        flow.map(checkSubscribedUseCase)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    subscribedStreams.value = it
                    _state.value = State.Result
                },
                onError = { _state.value = State.Error(it) }
            )
            .addTo(compositeDisposable)

        flow.observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    streams.value = it
                    _state.value = State.Result
                },
                onError = { _state.value = State.Error(it) }
            )
            .addTo(compositeDisposable)
    }

    fun getChat(streamId: Int, chatId: Int): Single<Chat> =
        getChats(streamId).map { it[chatId] }

    fun getChats(streamId: Int): Single<List<Chat>> =
        dataSource.loadStream(streamId).singleOrError().map { it.chats }

    fun openChat(streamId: Int, chatId: Int) = chat.postValue(streamId to chatId)

    fun loading() = _state.postValue(State.Loading)

    fun result() = _state.postValue(State.Result)

    fun error(error: Throwable) = _state.postValue(State.Error(error))

}