package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.model.data.User
import com.study.messengerfintech.model.source.RepositoryImpl
import io.reactivex.Observable

interface SearchUsersUseCase : (String) -> Observable<List<User>> {
    override fun invoke(searchQuery: String): Observable<List<User>>
}

internal class SearchUsersUseCaseImpl : SearchUsersUseCase {
    private val dataProvider = RepositoryImpl
    override fun invoke(searchQuery: String): Observable<List<User>> {
        return dataProvider.loadUsers().toObservable()
            .map { users ->
                if (searchQuery.isNotEmpty())
                    users.filter { user -> user.name.contains(searchQuery, ignoreCase = true) }
                else
                    users
            }
    }
}