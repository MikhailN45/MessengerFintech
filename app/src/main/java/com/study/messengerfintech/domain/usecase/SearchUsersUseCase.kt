package com.study.messengerfintech.domain.usecase

import com.study.messengerfintech.model.source.FakeDataSourceImpl
import com.study.messengerfintech.model.data.User
import io.reactivex.Observable

interface SearchUsersUseCase : (String) -> Observable<List<User>> {
    override fun invoke(request: String): Observable<List<User>>
}

class SearchUsersUseCaseImpl : SearchUsersUseCase {
    private val dataSource = FakeDataSourceImpl

    override fun invoke(request: String): Observable<List<User>> {
        return dataSource.loadUsers()
            .map { users ->
                if (request.isNotEmpty())
                    users.filter { user ->
                        user.nickName.contains(request, ignoreCase = true)
                    }
                else
                    users
            }
    }
}