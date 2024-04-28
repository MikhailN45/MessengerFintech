package com.study.messengerfintech.presentation.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.repository.Repository
import com.study.messengerfintech.presentation.events.ProfileEvent
import com.study.messengerfintech.presentation.state.ProfileState
import com.study.messengerfintech.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ProfileViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    val userStatus = MutableLiveData<ProfileState>()
    private val messageEvent = SingleLiveEvent<String>()

    fun processEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.SetUserStatus ->
                loadStatus(event.user)
        }
    }

    private fun loadStatus(user: User) = repository.loadStatus(user)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeBy(
            onSuccess = { userStatus.value = ProfileState(it) },
            onError = { messageEvent.value = it.message.orEmpty() }
        ).addTo(compositeDisposable)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}