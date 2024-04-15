package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.domain.data.User
import com.study.messengerfintech.model.source.Repository
import com.study.messengerfintech.model.source.RepositoryImpl
import io.reactivex.Observable

interface SearchUsersUseCase : (String) -> Observable<List<User>> {
    override fun invoke(searchQuery: String): Observable<List<User>>
}

class SearchUsersUseCaseImpl : SearchUsersUseCase {
    private val dataProvider: Repository = RepositoryImpl
    override fun invoke(searchQuery: String): Observable<List<User>> {
        return dataProvider.loadUsers().toObservable()
            .map { users ->
                if (searchQuery.isNotEmpty())
                    users.filter { user ->
                        user.name.contains(searchQuery, ignoreCase = true) }
                else
                    users
            }
    }
}