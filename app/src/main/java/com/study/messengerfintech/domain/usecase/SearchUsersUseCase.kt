package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.domain.repository.Repository
import com.study.messengerfintech.data.repository.RepositoryImpl
import com.study.messengerfintech.domain.model.User
import io.reactivex.Observable

interface SearchUsersUseCase {
    operator fun invoke(searchQuery: String): Observable<List<User>>
}

class SearchUsersUseCaseImpl : SearchUsersUseCase {
    private val repository: Repository = RepositoryImpl
    override fun invoke(searchQuery: String): Observable<List<User>> {
        return repository.loadUsers().toObservable()
            .map { users ->
                if (searchQuery.isNotEmpty())
                    users.filter { user ->
                        user.name.contains(searchQuery, ignoreCase = true)
                    }
                else
                    users
            }
    }
}