package com.study.messengerfintech.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.domain.usecase.SearchUsersUseCase
import com.study.messengerfintech.presentation.events.UsersEvent
import com.study.messengerfintech.presentation.state.UsersState
import com.study.messengerfintech.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UsersViewModel @Inject constructor(
    private val searchUserUseCase: SearchUsersUseCase
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val searchUsersSubject: BehaviorSubject<String> = BehaviorSubject.create()
    private val messageEvent = SingleLiveEvent<String>()
    private val _state: MutableLiveData<UsersState> = MutableLiveData()
    val state: LiveData<UsersState>
        get() = _state

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
            .doOnNext { _state.postValue(UsersState(users = emptyList(), isLoading = true)) }
            .debounce(500, TimeUnit.MILLISECONDS, Schedulers.io())
            .switchMap { searchQuery -> searchUserUseCase(searchQuery) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = {
                    _state.value = UsersState(it)
                    _state.value = _state.value?.copy(isLoading = false)
                },
                onError = {
                    messageEvent.value = it.message.orEmpty()
                    _state.value = _state.value?.copy(isLoading = false)
                }
            )
            .addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}