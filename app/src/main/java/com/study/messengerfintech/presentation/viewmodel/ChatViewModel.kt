package com.study.messengerfintech.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.Reaction
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.repository.Repository
import com.study.messengerfintech.presentation.events.ChatEvent
import com.study.messengerfintech.presentation.state.ChatState
import com.study.messengerfintech.utils.SendType
import com.study.messengerfintech.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ChatViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val messageEvent = SingleLiveEvent<String>()

    private val _state: MutableLiveData<ChatState> = MutableLiveData(ChatState())
    val state: LiveData<ChatState> = _state

    fun processEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.SendMessage.Topic ->
                sendPublicMessage(event.streamId, event.topicName, event.content)

            is ChatEvent.SendMessage.Private ->
                sendPrivateMessage(event.userEmail, event.content)

            is ChatEvent.LoadMessages.Topic -> loadTopicMessages(
                event.streamId,
                event.topicName,
                event.anchor
            )

            is ChatEvent.LoadMessages.Private ->
                loadPrivateMessages(event.userEmail)

            is ChatEvent.Emoji.Add -> addEmojiToMessage(event.messageId, event.emojiName)

            is ChatEvent.Emoji.Remove ->
                deleteReaction(event.messageId, event.emojiName)
        }
    }

    fun setReactionToMessage(reaction: Reaction, messagePosition: Int) {
        val messages = state.value?.messages ?: return
        messages[messagePosition].addEmoji(reaction)
        //redeclaration for livedata change trigger
        _state.value = state.value?.copy(messages = emptyList())
        _state.value = state.value?.copy(messages = messages)

        processEvent(
            ChatEvent.Emoji.Add(
                messages[messagePosition].id,
                reaction.name
            )
        )
    }

    private fun sendMessage(
        type: SendType,
        to: String,
        content: String,
        topic: String = ""
    ) {
        _state.value = state.value?.copy(isLoading = true)
        repository.sendMessage(type, to, content, topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { id ->
                    _state.value = state.value?.copy(isLoading = false)
                    val message = Message(id, content, User.ME.id, true)
                    val updatedMessages =
                        state.value?.messages?.toMutableList()?.apply { add(0, message) }
                    _state.value = state.value?.copy(messages = updatedMessages?.toList().orEmpty())
                },
                onError = {
                    _state.value = state.value?.copy(isLoading = false)
                    messageEvent.value = it.message.orEmpty()
                },
            ).addTo(compositeDisposable)
    }

    private fun sendPrivateMessage(userEmail: String, content: String) =
        sendMessage(SendType.PRIVATE, userEmail, content)

    private fun sendPublicMessage(stream: Int, topic: String, content: String) =
        sendMessage(SendType.STREAM, "[$stream]", content, topic)

    private fun loadPrivateMessages(user: String) {
        _state.value = state.value?.copy(isLoading = true)
        repository.loadPrivateMessages(user)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { messageList ->
                    updateMessages(messageList)
                    _state.value = state.value?.copy(isLoading = false)
                },
                onError = {
                    _state.value = state.value?.copy(isLoading = false)
                    messageEvent.value = it.message.orEmpty()
                }
            )
            .addTo(compositeDisposable)
    }

    private fun loadTopicMessages(streamId: Int, topic: String, anchor: String = "newest") {
        _state.value = state.value?.copy(isLoading = true)
        repository.loadTopicMessages(streamId, topic, anchor)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { messageList ->
                    updateMessages(messageList)
                    _state.value = state.value?.copy(isLoading = false)
                },
                onError = {
                    _state.value = state.value?.copy(isLoading = false)
                    messageEvent.value = it.message.orEmpty()
                }
            )
            .addTo(compositeDisposable)
    }

    private fun updateMessages(messages: List<Message>) {
        val isMessagesRemaining = messages.isEmpty() || messages.size < 20
        _state.value = state.value?.copy(
            messages = messages,
            loaded = isMessagesRemaining
        )
    }

    private fun addEmojiToMessage(messageId: Int, emojiName: String) {
        repository.addEmoji(messageId, emojiName)
            .subscribeBy(
                onComplete = {},
                onError = { messageEvent.postValue(it.message.orEmpty()) }
            ).addTo(compositeDisposable)
    }

    private fun deleteReaction(messageId: Int, emojiName: String) {
        repository.deleteEmoji(messageId, emojiName)
            .subscribeBy(
                onComplete = {},
                onError = { messageEvent.postValue(it.message.orEmpty()) }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}