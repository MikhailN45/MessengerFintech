package com.study.messengerfintech.data.repository

import android.util.Log
import com.study.messengerfintech.data.database.AppDatabase
import com.study.messengerfintech.data.network.ZulipApiService
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.model.UserStatus
import com.study.messengerfintech.domain.repository.UserRepository
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val service: ZulipApiService,
    private val database: AppDatabase
) : UserRepository {

    override fun loadUsers(): Observable<List<User>> {
        val localAnswer = database.userDao().getAll()
            .subscribeOn(Schedulers.io())
            .map { it.toListUser() }

        val remoteAnswer = service.getUsers()
            .subscribeOn(Schedulers.io())
            .map {
                it.members.map { userResponse -> userResponse.toUser() }
            }
            .flatMap { updateUsersPresence(it) }
            .doOnSuccess { database.userDao().insert(it.toListUserDb()) }
            .onErrorResumeNext { error ->
                Log.e("loadUsersRetrofit", "${error.message}")
                localAnswer
            }

        return Single.concat(localAnswer, remoteAnswer).toObservable()
    }

    private fun updateUsersPresence(users: List<User>): Single<List<User>> {
        val usersWithStatus = users.map { user ->
            loadStatus(user)
                .doOnError { error ->
                    Log.e("usersStatusPreload", "${error.message}")
                }
        }

        return Single.concatEager(usersWithStatus).toList()
    }

    override fun loadStatus(user: User): Single<User> =
        service.getPresence(user.id)
            .subscribeOn(Schedulers.io())
            .map { response ->
                response.presence["aggregated"]?.status?.let { status ->
                    user.status = UserStatus.stringToStatus(status)
                    user
                }
            }
            .onErrorReturn { user }
            .doOnError { error ->
                Log.e("getPresence", "${error.message}")
            }
}