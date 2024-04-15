package com.study.messengerfintech.model.source

import com.study.messengerfintech.model.data.Stream
import com.study.messengerfintech.model.data.User
import io.reactivex.Observable

interface FakeDataSource {
    fun loadStream(id: Int): Observable<Stream>

    fun loadStreams(): Observable<List<Stream>>

    fun loadUsers(): Observable<List<User>>
}