package com.study.messengerfintech.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.data.repository.RepositoryImpl
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.repository.Repository
import com.study.messengerfintech.presentation.events.ChatEvent
import com.study.messengerfintech.presentation.state.State
import com.study.messengerfintech.utils.SendType
import com.study.messengerfintech.utils.SingleLiveEvent
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers

class ChatViewModel : ViewModel() {
    private val repository: Repository = RepositoryImpl
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    val positionToScroll = SingleLiveEvent<Int>()
    val chat = MutableLiveData(State.Chat())

    private val _chatScreenState: MutableLiveData<State> = MutableLiveData()
    val chatScreenState: LiveData<State>
        get() = _chatScreenState

    fun processEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.SendMessage.Topic ->
                sendPublicMessage(event.streamId, event.topicName, event.content)

            is ChatEvent.SendMessage.Private ->
                sendPrivateMessage(event.userEmail, event.content)

            is ChatEvent.LoadMessages.Topic ->
                manageChatMessages(getTopicMessages(event.streamId, event.topicName))

            is ChatEvent.LoadMessages.Private ->
                manageChatMessages(getPrivateMessages(event.userEmail))

            is ChatEvent.Emoji.Add ->
                addReaction(event.messageId, event.emojiName)

            is ChatEvent.Emoji.Remove ->
                deleteReaction(event.messageId, event.emojiName)
        }
    }

    fun setChatTitle(title: String) {
        chat.value = State.Chat(title)
    }

    private fun sendMessage(
        type: SendType,
        to: String,
        content: String,
        topic: String = ""
    ) {
        _chatScreenState.postValue(State.Loading)
        repository.sendMessage(type, to, content, topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { id ->
                    val message = Message(id, content, User.ME.id, true)
                    val state = chat.value!!
                    val updatedList =
                        state.messages.toMutableList().apply { add(0, message) }
                    chat.value = state.copy(messages = updatedList)
                    positionToScroll.value = 0
                    _chatScreenState.postValue(State.Success)
                },
                onError = { _chatScreenState.postValue(State.Error) }
            ).addTo(compositeDisposable)
    }

    private fun sendPrivateMessage(userEmail: String, content: String) =
        sendMessage(SendType.PRIVATE, userEmail, content)

    private fun sendPublicMessage(stream: Int, topic: String, content: String) =
        sendMessage(SendType.STREAM, "[$stream]", content, topic)

    private fun manageChatMessages(flow: Single<List<Message>>) {
        _chatScreenState.postValue(State.Loading)
        val chat = chat.value!!.copy()
        flow
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { messageList ->
                    _chatScreenState.postValue(State.Success)
                    val isMessagesRemaining = messageList.isEmpty()
                    val messages =
                        chat.messages.toMutableList().apply { addAll(messageList) }
                    this.chat.value = chat.copy(messages = messages, loaded = isMessagesRemaining)
                    if (chat.messages.isEmpty())
                        positionToScroll.value = 0
                },
                onError = { _chatScreenState.postValue(State.Error) }
            )
            .addTo(compositeDisposable)
    }

    private fun getTopicMessages(streamId: Int, topic: String): Single<List<Message>> =
        repository.loadTopicMessages(streamId, topic)

    private fun getPrivateMessages(user: String): Single<List<Message>> =
        repository.loadPrivateMessages(user)

    private fun addReaction(messageId: Int, emojiName: String) {
        repository.addEmoji(messageId, emojiName)
            .subscribeBy(
                onComplete = {},
                onError = { _chatScreenState.postValue(State.Error) }
            ).addTo(compositeDisposable)
    }

    private fun deleteReaction(messageId: Int, emojiName: String) {
        repository.deleteEmoji(messageId, emojiName)
            .subscribeBy(
                onComplete = {},
                onError = { _chatScreenState.postValue(State.Error) }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}