package com.study.messengerfintech.presentation.viewmodel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.study.messengerfintech.domain.model.User
import com.study.messengerfintech.domain.repository.Repository
import com.study.messengerfintech.presentation.events.ChatEvent
import com.study.messengerfintech.utils.SchedulerRule
import com.study.messengerfintech.utils.SendType
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SendMessageTest {
    @get:Rule
    val schedulerRule = SchedulerRule()

    @get:Rule
    val executorRule = InstantTaskExecutorRule()

    @Mock
    lateinit var repository: Repository
    private lateinit var viewModel: ChatViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = ChatViewModel(repository)
    }

    @Test
    fun `add emoji to message`() {
        val messageId = 1
        val emojiName = "smile"
        val testObserver = TestObserver<Void>()

        doReturn(Completable.complete()).`when`(repository)
            .addEmoji(messageId, emojiName = emojiName)

        repository.addEmoji(messageId, emojiName)
            .subscribe(testObserver)

        testObserver.assertComplete()
        testObserver.assertNoErrors()

        verify(repository).addEmoji(messageId, emojiName = emojiName)
    }

    @Test
    fun `remove emoji from message`() {
        val messageId = 1
        val emojiName = "smile"
        val testObserver = TestObserver<Void>()

        doReturn(Completable.complete()).`when`(repository)
            .deleteEmoji(messageId, emojiName = emojiName)

        repository.deleteEmoji(messageId, emojiName)
            .subscribe(testObserver)

        testObserver.assertComplete()
        testObserver.assertNoErrors()

        verify(repository).deleteEmoji(messageId, emojiName = emojiName)
    }

    @Test
    fun `send message`() {
        val type = SendType.STREAM
        val to = "receiver"
        val content = "Hello, World!"
        val topic = "Greetings"
        val messageId = 123
        val streamId = 1

        doReturn(Single.just(messageId)).`when`(repository)
            .sendMessage(type, to, content, topic)

        viewModel.processEvent(ChatEvent.SendMessage.Topic(streamId, topic, content))

        assertEquals(false, viewModel.state.value?.isLoading)
        assertEquals(messageId, viewModel.state.value?.messages?.get(0)?.id)
        assertEquals(content, viewModel.state.value?.messages?.get(0)?.content)
        assertEquals(User.ME.id, viewModel.state.value?.messages?.get(0)?.userId)

        verify(repository).sendMessage(type, to, content, topic)
    }
}
