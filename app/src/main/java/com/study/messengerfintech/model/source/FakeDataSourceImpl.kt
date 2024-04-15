package com.study.messengerfintech.model.source

import com.study.messengerfintech.model.data.Chat
import com.study.messengerfintech.model.data.Message
import com.study.messengerfintech.model.data.Stream
import com.study.messengerfintech.model.data.User
import com.study.messengerfintech.utils.Utils
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

object FakeDataSourceImpl : FakeDataSource {
    private val users: MutableList<User> = mutableListOf()
    private val streams: MutableList<Stream> = mutableListOf()

    init {
        users.addAll(
            mutableListOf(
                User(0, "Darell Steward1"),
                User(1, "Darell Steward2"),
                User(2, "Darell Steward3"),
                User(3, "Darell Steward4"),
                User(4, "Darell Steward5"),
                User(5, "Darell Steward6"),
                User(6, "Darell Steward7"),
                User(7, "Darell Steward8"),
                User(8, "Darell Steward9"),
                User(9, "Darell Steward0")
            )
        )

        streams.addAll(
            mutableListOf(
                Stream(
                    "#General", 0, true,
                    chats = mutableListOf(
                        Chat("Testing"),
                        Chat("Bruh"),
                        Chat("Lectures"),
                        Chat("Homework")
                    )
                ),
                Stream(
                    "#Development", 1, true,
                    chats = mutableListOf(
                        Chat("Android"),
                        Chat("KMP"),
                        Chat("IOS"),
                        Chat("BackEnd")
                    )
                ),
                Stream(
                    "#Design", 2, true,
                    chats = mutableListOf(
                        Chat("UI/UX"),
                        Chat("Prototype")
                    )
                ),
                Stream(
                    "#PR", 3,
                    chats = mutableListOf(
                        Chat("SMM"),
                        Chat("Targeting"),
                        Chat("Blog")
                    )
                ),
                Stream(
                    "#Recruiting", 4,
                    chats = mutableListOf(
                        Chat("Review"),
                        Chat("Meetings")
                    )
                )
            )
        )

        streams[0].chats[0].messages.addAll(with(User(nickName = "Jake Wharton")) {
            mutableListOf(
                Message(1, this, message = Utils.fakeChat[0]),
                Message(2, this, message = Utils.fakeChat[1]),
                Message(3, this, message = Utils.fakeChat[2]),
                Message(4, this, message = Utils.fakeChat[3]),
                Message(5, this, message = Utils.fakeChat[4]),
                Message(6, this, message = Utils.fakeChat[5]),
                Message(7, this, message = Utils.fakeChat[6])
            )
        })
    }

    override fun loadStreams(): Observable<List<Stream>> =
        Observable.fromArray(streams.toList())
            .subscribeOn(Schedulers.io())
            .delay(500, TimeUnit.MILLISECONDS)

    override fun loadStream(id: Int): Observable<Stream> =
        Observable.just(streams[id])
            .subscribeOn(Schedulers.io())
            .delay(500, TimeUnit.MILLISECONDS)

    override fun loadUsers(): Observable<List<User>> =
        Observable.fromArray(users.toList())
            .subscribeOn(Schedulers.io())
            .delay(500, TimeUnit.MILLISECONDS)
}