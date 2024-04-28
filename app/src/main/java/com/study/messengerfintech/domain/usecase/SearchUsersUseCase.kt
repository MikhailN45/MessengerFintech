package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.repository.Repository
import io.reactivex.Observable
import javax.inject.Inject

interface SearchUsersUseCase {
    operator fun invoke(searchQuery: String): Observable<List<User>>
}

class SearchUsersUseCaseImpl @Inject constructor(
    private val repository: Repository
) : SearchUsersUseCase {

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