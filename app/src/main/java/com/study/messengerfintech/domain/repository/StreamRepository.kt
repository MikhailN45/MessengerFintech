package com.study.messengerfintech.domain.repository

import com.study.messengerfintech.domain.model.Stream
import com.study.messengerfintech.domain.model.User
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface StreamRepository {
    fun requestAllStreams(): Completable

    fun requestSubscribedStreams(): Completable

    fun getAllStreams(): Observable<List<Stream>>

    fun getSubscribedStreams(): Observable<List<Stream>>

    fun loadOwnUser(): Single<User>
}