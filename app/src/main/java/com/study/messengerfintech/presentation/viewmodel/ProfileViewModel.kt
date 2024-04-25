package com.study.messengerfintech.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.data.repository.RepositoryImpl
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.repository.Repository
import com.study.messengerfintech.presentation.events.ProfileEvent
import com.study.messengerfintech.presentation.state.State
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ProfileViewModel : ViewModel() {
    private val repository: Repository = RepositoryImpl
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    val userStatus = MutableLiveData<State.Profile>()

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
            onSuccess = { userStatus.value = State.Profile(it) },
            onError = { error ->
                Log.e("loadStatus", error.toString())
            }
        ).addTo(compositeDisposable)

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}