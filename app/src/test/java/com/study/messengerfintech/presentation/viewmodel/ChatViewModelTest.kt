package com.study.messengerfintech.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.study.messengerfintech.domain.repository.ChatRepository
import com.study.messengerfintech.utils.SchedulerRule
import com.study.messengerfintech.utils.SendType
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ChatViewModelTest {
    @get:Rule
    val schedulerRule = SchedulerRule()

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var chatRepository: ChatRepository
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        viewModel = ChatViewModel(chatRepository = mock(ChatRepository::class.java))
    }

    @Test
    fun `add emoji to message`() {
        val messageId = 1
        val emojiName = "smile"

        doReturn(Completable.complete()).`when`(chatRepository)
            .addEmoji(messageId, emojiName = emojiName)

        chatRepository.addEmoji(messageId, emojiName)
            .test()
            .assertComplete()

        verify(chatRepository).addEmoji(messageId, emojiName = emojiName)
    }

    @Test
    fun `remove emoji from message`() {
        val messageId = 1
        val emojiName = "smile"

        doReturn(Completable.complete()).`when`(chatRepository)
            .deleteEmoji(messageId, emojiName = emojiName)

        chatRepository.deleteEmoji(messageId, emojiName)
            .test()
            .assertComplete()

        verify(chatRepository).deleteEmoji(messageId, emojiName = emojiName)
    }

    @Test
    fun `send message`() {
        val type = SendType.STREAM
        val to = "receiver"
        val content = "Hello, World!"
        val topic = "Greetings"
        val messageId = 123

        doReturn(Single.just(messageId)).`when`(chatRepository)
            .sendMessage(type, to, content, topic)

        chatRepository.sendMessage(type, to, content, topic)
            .test()
            .assertComplete()

        verify(chatRepository).sendMessage(type, to, content, topic)
    }
}
