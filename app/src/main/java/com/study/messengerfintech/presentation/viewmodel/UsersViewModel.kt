package com.study.messengerfintech.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.domain.usecase.SearchUsersUseCase
import com.study.messengerfintech.domain.usecase.SearchUsersUseCaseImpl
import com.study.messengerfintech.presentation.events.UsersEvent
import com.study.messengerfintech.presentation.state.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class UsersViewModel : ViewModel() {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchUsersSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val searchUserUseCase: SearchUsersUseCase = SearchUsersUseCaseImpl()
    val users = MutableLiveData<State.Users>()
    private val _usersScreenState: MutableLiveData<State> = MutableLiveData()
    val usersScreenState: LiveData<State>
        get() = _usersScreenState

    init {
        subscribeToSearchUsers()
    }

    fun processEvent(event: UsersEvent) {
        when (event) {
            is UsersEvent.SearchForUsers ->
                searchUsersSubject.onNext(event.query)
        }
    }

    private fun subscribeToSearchUsers() {
        searchUsersSubject
            .subscribeOn(Schedulers.io())
            .distinctUntilChanged()
            .doOnNext { _usersScreenState.postValue(State.Loading) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery -> searchUserUseCase(searchQuery) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    users.value = State.Users(it)
                    _usersScreenState.value = State.Success
                },
                onError = { _usersScreenState.value = State.Error }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}