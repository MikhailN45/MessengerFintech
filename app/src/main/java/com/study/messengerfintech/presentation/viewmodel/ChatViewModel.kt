package com.study.messengerfintech.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.messengerfintech.domain.model.Message
import com.study.messengerfintech.domain.model.Reaction
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.repository.ChatRepository
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
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val messageEvent = SingleLiveEvent<String>()
    val scrollEvent = SingleLiveEvent<Unit>()

    private val _state: MutableLiveData<ChatState> = MutableLiveData(ChatState())
    val state: LiveData<ChatState> = _state

    fun processEvent(event: ChatEvent) {
        when (event) {
            is ChatEvent.SendMessage.Topic ->
                sendPublicMessage(event.streamId, event.topicTitle, event.content)

            is ChatEvent.SendMessage.Private ->
                sendPrivateMessage(event.userEmail, event.content)

            is ChatEvent.LoadMessages.Topic ->
                loadTopicMessages(
                    event.streamId,
                    event.topicTitle,
                    event.anchor
                )

            is ChatEvent.LoadMessages.Private ->
                loadPrivateMessages(event.userEmail, event.anchor)

            is ChatEvent.Emoji.Add ->
                addEmojiToMessage(event.messageId, event.emojiName)

            is ChatEvent.Emoji.Remove ->
                deleteReaction(event.messageId, event.emojiName)

            is ChatEvent.ReactionClick ->
                setReactionToMessage(event.reaction, event.messagePosition)
        }
    }

    private fun setReactionToMessage(reaction: Reaction, messagePosition: Int) {
        val messages = state.value?.messages ?: return
        messages[messagePosition].addEmoji(reaction)
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
        chatRepository.sendMessage(type, to, content, topic)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { id ->
                    _state.value = state.value?.copy(isLoading = false)
                    val message = Message(id, content, User.ME.id, true)
                    val updatedMessages =
                        state.value?.messages?.toMutableList()?.apply { add(0, message) }
                    _state.value = state.value?.copy(messages = updatedMessages?.toList().orEmpty())
                    scrollEvent.value = Unit
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

    private fun loadPrivateMessages(user: String, anchor: String) {
        _state.value = state.value?.copy(isLoading = true)
        chatRepository.loadPrivateMessages(user, anchor)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { messageList ->
                    updateMessagesForPrivate(messageList, user)
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
        chatRepository.loadTopicMessages(streamId, topic, anchor)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = { messageList ->
                    updateMessagesForTopic(messageList, topic)
                    _state.value = state.value?.copy(isLoading = false)
                },
                onError = {
                    _state.value = state.value?.copy(isLoading = false)
                    messageEvent.value = it.message.orEmpty()
                }
            )
            .addTo(compositeDisposable)
    }

    private fun updateMessagesForTopic(messages: List<Message>, newMessagesTopic: String) {
        val isMessagesAreOver = messages.isEmpty() || messages.size < 20
        val currentMessagesTopic = state.value?.messages?.firstOrNull()?.topicTitle
        val newMessages = mutableListOf<Message>()
        if (currentMessagesTopic == newMessagesTopic) {
            newMessages.addAll(state.value?.messages.orEmpty())
        }
        newMessages.addAll(messages)

        _state.value = state.value?.copy(
            messages = newMessages,
            isAllChatMessageAreLoaded = isMessagesAreOver
        )
    }

    private fun updateMessagesForPrivate(messages: List<Message>, user: String) {
        val isMessagesAreOver = messages.isEmpty() || messages.size < 20
        val currentCompanion = state.value?.messages?.firstOrNull()?.userEmail
        val newMessages = mutableListOf<Message>()
        if (currentCompanion == user) {
            newMessages.addAll(state.value?.messages.orEmpty())
        }
        newMessages.addAll(messages)

        _state.value = state.value?.copy(
            messages = newMessages,
            isAllChatMessageAreLoaded = isMessagesAreOver
        )
    }

    private fun addEmojiToMessage(messageId: Int, emojiName: String) {
        chatRepository.addEmoji(messageId, emojiName)
            .subscribeBy(
                onComplete = { },
                onError = { messageEvent.postValue(it.message.orEmpty()) }
            ).addTo(compositeDisposable)
    }

    private fun deleteReaction(messageId: Int, emojiName: String) {
        chatRepository.deleteEmoji(messageId, emojiName)
            .subscribeBy(
                onComplete = { },
                onError = { messageEvent.postValue(it.message.orEmpty()) }
            ).addTo(compositeDisposable)
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}