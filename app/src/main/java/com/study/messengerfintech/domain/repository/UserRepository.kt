package com.study.messengerfintech.domain.repository

import com.study.messengerfintech.domain.model.User
import io.reactivex.Observable
import io.reactivex.Single

interface UserRepository {
    fun loadStatus(user: User): Single<User>

    fun loadUsers(): Observable<List<User>>
}