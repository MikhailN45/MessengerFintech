package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.data.repository.Repository
import com.study.messengerfintech.data.repository.RepositoryImpl
import io.reactivex.Observable

interface SearchUsersUseCase : (String) -> Observable<List<User>> {
    override fun invoke(searchQuery: String): Observable<List<User>>
}

class SearchUsersUseCaseImpl : SearchUsersUseCase {
    private val repository: Repository = RepositoryImpl
    override fun invoke(searchQuery: String): Observable<List<User>> {
        return repository.loadUsers().toObservable()
            .map { users ->
                if (searchQuery.isNotEmpty())
                    users.filter { user ->
                        user.name.contains(searchQuery, ignoreCase = true) }
                else
                    users
            }
    }
}